-- =============================================================================
-- Creative 3D Builder · Semilla 001 · Datos de ejemplo
-- Los mismos seis modelos de RepositorioSimulado.kt, para comparar la app con la base.
-- Solo para desarrollo: no ejecutar en producción.
-- =============================================================================
BEGIN;

INSERT INTO usuarios (id, nombre, correo)
VALUES ('00000000-0000-4000-8000-000000000001', 'Estudiante de prueba', NULL);

INSERT INTO modelos (id, usuario_id, nombre, estado, poligonos, creado_en) VALUES
  ('00000000-0000-4000-8000-0000000000a1', '00000000-0000-4000-8000-000000000001', 'Taza de cerámica',       'listo',      4820,  '2026-10-23T00:00:00Z'),
  ('00000000-0000-4000-8000-0000000000a2', '00000000-0000-4000-8000-000000000001', 'Zapatilla deportiva',    'listo',      12340, '2026-10-26T00:00:00Z'),
  ('00000000-0000-4000-8000-0000000000a3', '00000000-0000-4000-8000-000000000001', 'Lámpara de escritorio',  'listo',      7960,  '2026-10-28T00:00:00Z'),
  ('00000000-0000-4000-8000-0000000000a4', '00000000-0000-4000-8000-000000000001', 'Maceta con suculenta',   'procesando', NULL,  '2026-10-30T00:00:00Z'),
  ('00000000-0000-4000-8000-0000000000a5', '00000000-0000-4000-8000-000000000001', 'Audífonos inalámbricos', 'error',      NULL,  '2026-10-31T00:00:00Z'),
  ('00000000-0000-4000-8000-0000000000a6', '00000000-0000-4000-8000-000000000001', 'Silla minimalista',      'borrador',   NULL,  '2026-11-01T00:00:00Z');

INSERT INTO trabajos_generacion (modelo_id, proveedor, estado, mensaje_error, iniciado_en, terminado_en) VALUES
  ('00000000-0000-4000-8000-0000000000a4', 'por-definir', 'procesando', NULL,                          '2026-10-30T00:01:00Z', NULL),
  ('00000000-0000-4000-8000-0000000000a5', 'por-definir', 'fallido',    'La foto está demasiado oscura', '2026-10-31T00:01:00Z', '2026-10-31T00:03:00Z');

COMMIT;
