# api/ · TEC-05 · API de Creative 3D Builder (Cloudflare Workers)

La app no habla con la base de datos: habla con esta API, la **única pieza que conoce las credenciales de Neon**.

| Método | Ruta | Para qué | Historia |
|---|---|---|---|
| GET | `/salud` | Comprueba la API y la versión del esquema | — |
| GET | `/modelos` | «Mis Modelos 3D», del más reciente al más antiguo | HU-05 |
| GET | `/modelos/:id` | Detalle | HU-04 |
| POST | `/modelos` | Crea un modelo: `multipart/form-data` con `nombre`, `foto` (.jpg), `modelo` (.glb), `poligonos`, `proveedor`, `ancho_px`, `alto_px` | HU-07 · HU-02 |
| GET | `/modelos/:id/modelo.glb` | Archivo 3D para el visor 360° | HU-01 |
| GET | `/modelos/:id/foto.jpg` | Foto de origen | — |
| DELETE | `/modelos/:id` | Borra el modelo con su foto y su archivo | HU-10 |

**Datos:** Neon (PostgreSQL 18), proyecto «Builder 3D». Las filas van a `modelos`, `fotos`, `trabajos_generacion` y
`archivos_modelo`.
**Archivos:** Workers KV `creative-3d-builder-archivos`, con claves `fotos/<id>.jpg` y `modelos/<id>.glb`. Se usa KV
porque R2 exige activarlo con un método de pago. La columna se sigue llamando `clave_r2` (migración 001) y guarda la
clave de KV.

**Validaciones:** nombre de 1 a 80 caracteres; foto de 5 MB como máximo; modelo de 20 MB como máximo; el `.glb` debe
empezar con la palabra mágica `glTF`. Las consultas van siempre parametrizadas (`$1`, `$2`…), nunca concatenadas
(OWASP M4).

## Desplegar

Usa el CLI `cf` (configuración en `cloudflare.config.ts`, sin Wrangler).

```bash
cd api
npm install
npx tsc --noEmit                         # tipos sin errores
cf auth login                            # una vez
# La cadena de Neon va como SECRETO, desde un archivo fuera del repositorio:
#   { "DATABASE_URL": "postgresql://…" }
cf deploy --secrets-file ../../secretos-api.json
```

Después de desplegar, pon la URL en `gradle.properties` y vuelve a compilar la app:

```properties
creative3d.apiUrl=https://creative-3d-builder-api.<subdominio>.workers.dev
```

Sin esa línea, la app guarda los modelos en el teléfono (`AlmacenLocal`).

## Estado (11 de noviembre de 2026)

| Pieza | Estado |
|---|---|
| Código de la API y tipos (`tsc --noEmit`) | ✔ Listo |
| KV `creative-3d-builder-archivos` | ✔ Creado en la cuenta de Cloudflare |
| Despliegue del Worker | ⏳ **Pendiente de aprobación del equipo** (publica un servicio en producción) |

## Riesgo conocido

La API no tiene inicio de sesión: usa un usuario de demostración fijo (`USUARIO_DEMO`). Cualquiera que conozca la URL
podría crear o borrar modelos. Antes de abrirla a más personas hay que agregar autenticación (decisión abierta del
entregable 2).
