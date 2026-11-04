## Qué
Crear en Neon (PostgreSQL 18) el esquema completo de los tres entregables, versionado como migración.

## Hecho cuando
- [ ] `db/migraciones/001_esquema_inicial.sql` crea `usuarios`, `modelos`, `fotos`, `trabajos_generacion`, `archivos_modelo` y `esquema_version`
- [ ] `db/semillas/001_datos_ejemplo.sql` carga los mismos 6 modelos de `RepositorioSimulado`
- [ ] La migración y la semilla están aplicadas en el proyecto Neon «Builder 3D»
- [ ] `docs/base-de-datos.md` tiene el diagrama entidad-relación y el diccionario de datos
- [ ] Ninguna credencial queda en el repositorio

## Fuera de este elemento
La app **no** se conecta a la base en el entregable 1: usa `RepositorioSimulado`. La conexión llega con TEC-05 (API) en el entregable 2.
