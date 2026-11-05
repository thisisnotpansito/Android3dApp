-- =============================================================================
-- Creative 3D Builder · Migración 001 · Esquema inicial
-- TEC-03 · Entregable 1 (fase inicial)
-- Motor: PostgreSQL 16+ (Neon). Ejecutar una sola vez, en orden.
--
-- Se crea el esquema completo de los tres entregables, aunque en la fase inicial
-- la app todavía no se conecta (usa RepositorioSimulado):
--   Entregable 1 · usuarios, modelos            → «Mis Modelos 3D» y su detalle
--   Entregable 2 · fotos, trabajos_generacion,  → foto → modelo 3D que gira 360°
--                  archivos_modelo
--   Entregable 3 · archivos_modelo (formato glb) → realidad aumentada
--
-- Regla: los archivos (fotos .jpg y modelos .glb) NO se guardan aquí. Van a un
-- almacenamiento de objetos (Cloudflare R2) y la base guarda solo su clave.
-- =============================================================================

BEGIN;

-- Control de versiones del esquema: cada migración deja su fila.
CREATE TABLE IF NOT EXISTS esquema_version (
    version      INTEGER     PRIMARY KEY,
    descripcion  TEXT        NOT NULL,
    aplicada_en  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------------
-- Tipos enumerados
-- ---------------------------------------------------------------------------
-- Igual a EstadoModelo en app/.../domain/Modelo3D.kt
CREATE TYPE estado_modelo  AS ENUM ('borrador', 'procesando', 'listo', 'error');
CREATE TYPE estado_trabajo AS ENUM ('en_cola', 'procesando', 'terminado', 'fallido');
CREATE TYPE formato_3d     AS ENUM ('glb', 'gltf', 'stl', 'usdz');

-- ---------------------------------------------------------------------------
-- usuarios · quién es dueño de cada modelo
-- ---------------------------------------------------------------------------
CREATE TABLE usuarios (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre     TEXT        NOT NULL CHECK (length(trim(nombre)) BETWEEN 1 AND 80),
    correo     TEXT        UNIQUE CHECK (correo IS NULL OR correo ~* '^[^@\s]+@[^@\s]+\.[^@\s]+$'),
    creado_en  TIMESTAMPTZ NOT NULL DEFAULT now()
);
COMMENT ON TABLE  usuarios        IS 'Dueños de los modelos. correo es opcional: se decide en el entregable 2 si habrá inicio de sesión.';

-- ---------------------------------------------------------------------------
-- modelos · lo que se ve en «Mis Modelos 3D» (HU-05) y en el detalle (HU-04)
-- ---------------------------------------------------------------------------
CREATE TABLE modelos (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id      UUID          NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    nombre          TEXT          NOT NULL CHECK (length(trim(nombre)) BETWEEN 1 AND 80),
    estado          estado_modelo NOT NULL DEFAULT 'borrador',
    poligonos       INTEGER       CHECK (poligonos IS NULL OR poligonos >= 0),
    miniatura_clave TEXT,
    creado_en       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    actualizado_en  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    -- Un modelo «listo» siempre sabe cuántos polígonos tiene.
    CONSTRAINT listo_tiene_poligonos CHECK (estado <> 'listo' OR poligonos IS NOT NULL)
);
COMMENT ON COLUMN modelos.miniatura_clave IS 'Clave del archivo de la miniatura en R2, no la imagen.';

-- «Mis Modelos 3D» siempre lista los de un usuario, del más reciente al más antiguo.
CREATE INDEX idx_modelos_usuario_fecha ON modelos (usuario_id, creado_en DESC);

-- ---------------------------------------------------------------------------
-- fotos · foto(s) de origen de un modelo (entregable 2)
-- ---------------------------------------------------------------------------
CREATE TABLE fotos (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    modelo_id     UUID        NOT NULL REFERENCES modelos(id) ON DELETE CASCADE,
    clave_r2      TEXT        NOT NULL UNIQUE,
    ancho_px      INTEGER     CHECK (ancho_px > 0),
    alto_px       INTEGER     CHECK (alto_px > 0),
    tamano_bytes  INTEGER     CHECK (tamano_bytes > 0),
    orden         SMALLINT    NOT NULL DEFAULT 0 CHECK (orden >= 0),
    creado_en     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (modelo_id, orden)
);
CREATE INDEX idx_fotos_modelo ON fotos (modelo_id);

-- ---------------------------------------------------------------------------
-- trabajos_generacion · seguimiento de la conversión foto → 3D (entregable 2)
-- Va aparte de modelos: un intento puede fallar y reintentarse sin perder el modelo.
-- ---------------------------------------------------------------------------
CREATE TABLE trabajos_generacion (
    id             UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    modelo_id      UUID           NOT NULL REFERENCES modelos(id) ON DELETE CASCADE,
    proveedor      TEXT           NOT NULL,
    id_externo     TEXT,
    estado         estado_trabajo NOT NULL DEFAULT 'en_cola',
    mensaje_error  TEXT,
    iniciado_en    TIMESTAMPTZ    NOT NULL DEFAULT now(),
    terminado_en   TIMESTAMPTZ,
    CONSTRAINT fallido_tiene_mensaje CHECK (estado <> 'fallido' OR mensaje_error IS NOT NULL),
    CONSTRAINT termina_despues       CHECK (terminado_en IS NULL OR terminado_en >= iniciado_en)
);
CREATE INDEX idx_trabajos_modelo ON trabajos_generacion (modelo_id, iniciado_en DESC);

-- ---------------------------------------------------------------------------
-- archivos_modelo · resultado 3D; el glb es el que usan el visor y la AR
-- ---------------------------------------------------------------------------
CREATE TABLE archivos_modelo (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    modelo_id     UUID        NOT NULL REFERENCES modelos(id) ON DELETE CASCADE,
    formato       formato_3d  NOT NULL,
    clave_r2      TEXT        NOT NULL UNIQUE,
    tamano_bytes  INTEGER     CHECK (tamano_bytes > 0),
    creado_en     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (modelo_id, formato)
);

-- ---------------------------------------------------------------------------
-- actualizado_en se mantiene solo
-- ---------------------------------------------------------------------------
CREATE FUNCTION tocar_actualizado_en() RETURNS trigger AS $$
BEGIN
    NEW.actualizado_en := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_modelos_actualizado
    BEFORE UPDATE ON modelos
    FOR EACH ROW EXECUTE FUNCTION tocar_actualizado_en();

INSERT INTO esquema_version (version, descripcion)
VALUES (1, 'Esquema inicial: usuarios, modelos, fotos, trabajos_generacion, archivos_modelo');

COMMIT;
