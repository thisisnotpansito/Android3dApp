# db/ · Base de datos (PostgreSQL en Neon)

Diseño, diagrama y diccionario de datos: [`../docs/base-de-datos.md`](../docs/base-de-datos.md).

```
db/
├── migraciones/
│   └── 001_esquema_inicial.sql   usuarios, modelos, fotos, trabajos_generacion, archivos_modelo
└── semillas/
    └── 001_datos_ejemplo.sql     los 6 modelos de RepositorioSimulado (solo desarrollo)
```

## Reglas

1. **Nunca se edita una migración ya aplicada.** Un cambio es un archivo nuevo: `002_…sql`, que termina insertando su
   fila en `esquema_version`.
2. **Ninguna credencial entra al repositorio.** La cadena de conexión de Neon se pide al responsable de la base por un
   canal privado y se guarda en una variable de entorno (`DATABASE_URL`), nunca en el código ni en el APK.
3. Las semillas son solo para desarrollo.

## Aplicar las migraciones

Con `psql` (o el *SQL Editor* de la consola de Neon, pegando el archivo):

```bash
export DATABASE_URL="postgresql://…"        # pedirla en privado; no se comparte en el repositorio
psql "$DATABASE_URL" -f db/migraciones/001_esquema_inicial.sql
psql "$DATABASE_URL" -f db/semillas/001_datos_ejemplo.sql
psql "$DATABASE_URL" -c "SELECT version, descripcion, aplicada_en FROM esquema_version;"
```

Con la CLI de Neon se obtiene la cadena sin copiarla de la consola:

```bash
npx neonctl auth
npx neonctl connection-string --project-id <id-del-proyecto> --database-name neondb
```

## Recrear desde cero (solo desarrollo)

```sql
DROP SCHEMA public CASCADE; CREATE SCHEMA public;
```

y volver a aplicar las migraciones en orden. En Neon es más seguro crear una **rama** de la base (*Branches → New
branch*) y probar ahí.
