import { bindings, defineConfig } from "cf/config";

import * as entrypoint from "./src/index.ts" with { type: "cf-worker" };

/**
 * TEC-05 · API de Creative 3D Builder en Cloudflare Workers.
 *
 * - DATABASE_URL: cadena de conexión de Neon. Es un SECRETO: nunca va en el repositorio
 *   ni en la app. Se sube con `cf deploy --secrets-file <archivo fuera del repo>`.
 * - ARCHIVOS: Workers KV con las fotos (.jpg) y los modelos 3D (.glb). Se usa KV porque
 *   R2 exige activarlo con un método de pago (TEC-06).
 */
export default defineConfig({
  worker: {
    name: "creative-3d-builder-api",
    entrypoint,
    compatibilityDate: "2026-10-01",
    compatibilityFlags: ["nodejs_compat"],
    env: {
      DATABASE_URL: bindings.secret(),
      // KV «creative-3d-builder-archivos» (creado con `cf kv namespaces create`)
      ARCHIVOS: bindings.kv({ id: "6cf95e4091aa4b23a3e15b7b23ecf3a9" }),
      USUARIO_DEMO: bindings.text("00000000-0000-4000-8000-000000000001"),
    },
  },
});
