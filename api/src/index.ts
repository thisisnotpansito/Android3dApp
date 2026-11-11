/**
 * TEC-05 · API de Creative 3D Builder.
 *
 * La app nunca habla con la base de datos: habla con esta API, que es la única pieza
 * que conoce las credenciales de Neon. Los archivos (fotos y modelos .glb) se guardan
 * en Workers KV; la base guarda solo su clave (columna clave_r2, que conserva su nombre
 * de la migración 001 aunque el almacenamiento sea KV).
 *
 *   GET    /salud                      → estado de la API y de la base
 *   GET    /modelos                    → «Mis Modelos 3D» (HU-05)
 *   GET    /modelos/:id                → detalle (HU-04)
 *   POST   /modelos                    → crear desde una foto (HU-07, HU-02) · multipart
 *   GET    /modelos/:id/modelo.glb     → archivo 3D para el visor 360° (HU-01)
 *   GET    /modelos/:id/foto.jpg       → foto de origen
 *   DELETE /modelos/:id                → borrar con sus archivos (HU-10)
 */
import { neon, type NeonQueryFunction } from "@neondatabase/serverless";

type Sql = NeonQueryFunction<false, false>;

interface Env {
  DATABASE_URL: string;
  ARCHIVOS: KVNamespace;
  USUARIO_DEMO: string;
}

const MAX_FOTO = 5 * 1024 * 1024;     // 5 MB
const MAX_MODELO = 20 * 1024 * 1024;  // 20 MB (KV admite hasta 25 MiB por valor)
const UUID = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

type Fila = {
  id: string;
  nombre: string;
  estado: "borrador" | "procesando" | "listo" | "error";
  poligonos: number | null;
  creado_en: string;
  tiene_modelo: boolean;
  proveedor: string | null;
  mensaje_error: string | null;
};

const json = (cuerpo: unknown, status = 200) =>
  new Response(JSON.stringify(cuerpo), {
    status,
    headers: { "content-type": "application/json; charset=utf-8", "cache-control": "no-store" },
  });

const error = (status: number, mensaje: string) => json({ error: mensaje }, status);

// Una sola consulta para la lista y el detalle: el modelo, si tiene .glb y su último trabajo.
const SELECT_MODELO = `
  SELECT m.id, m.nombre, m.estado, m.poligonos,
         to_char(m.creado_en AT TIME ZONE 'UTC', 'YYYY-MM-DD"T"HH24:MI:SS"Z"') AS creado_en,
         EXISTS (SELECT 1 FROM archivos_modelo a WHERE a.modelo_id = m.id AND a.formato = 'glb') AS tiene_modelo,
         t.proveedor, t.mensaje_error
    FROM modelos m
    LEFT JOIN LATERAL (
      SELECT proveedor, mensaje_error FROM trabajos_generacion
       WHERE modelo_id = m.id ORDER BY iniciado_en DESC LIMIT 1
    ) t ON true`;

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url);
    const partes = url.pathname.split("/").filter(Boolean);
    const sql = neon(env.DATABASE_URL);

    try {
      // GET /salud
      if (request.method === "GET" && url.pathname === "/salud") {
        const [v] = await sql.query("SELECT max(version) AS version FROM esquema_version");
        return json({ ok: true, esquema: v.version });
      }

      // GET /modelos
      if (request.method === "GET" && partes.length === 1 && partes[0] === "modelos") {
        const filas = (await sql.query(
          `${SELECT_MODELO} WHERE m.usuario_id = $1 ORDER BY m.creado_en DESC, m.id DESC`,
          [env.USUARIO_DEMO],
        )) as Fila[];
        return json(filas);
      }

      // POST /modelos  (multipart: nombre, foto, modelo, poligonos, proveedor)
      if (request.method === "POST" && partes.length === 1 && partes[0] === "modelos") {
        return await crearModelo(request, env, sql);
      }

      if (partes[0] === "modelos" && partes.length >= 2) {
        const id = partes[1];
        if (!UUID.test(id)) return error(404, "El modelo no existe");

        // GET /modelos/:id
        if (request.method === "GET" && partes.length === 2) {
          const filas = (await sql.query(`${SELECT_MODELO} WHERE m.id = $1 AND m.usuario_id = $2`, [id, env.USUARIO_DEMO])) as Fila[];
          return filas.length ? json(filas[0]) : error(404, "El modelo no existe");
        }

        // GET /modelos/:id/modelo.glb  ·  GET /modelos/:id/foto.jpg
        if (request.method === "GET" && partes.length === 3 && (partes[2] === "modelo.glb" || partes[2] === "foto.jpg")) {
          const esModelo = partes[2] === "modelo.glb";
          const clave = esModelo ? `modelos/${id}.glb` : `fotos/${id}.jpg`;
          const datos = await env.ARCHIVOS.get(clave, "arrayBuffer");
          if (!datos) return error(404, esModelo ? "Este modelo no tiene archivo 3D" : "Este modelo no tiene foto");
          return new Response(datos, {
            headers: {
              "content-type": esModelo ? "model/gltf-binary" : "image/jpeg",
              "cache-control": "private, max-age=3600",
            },
          });
        }

        // DELETE /modelos/:id  (HU-10)
        if (request.method === "DELETE" && partes.length === 2) {
          const borradas = await sql.query("DELETE FROM modelos WHERE id = $1 AND usuario_id = $2 RETURNING id", [id, env.USUARIO_DEMO]);
          if (!borradas.length) return error(404, "El modelo no existe");
          await Promise.all([env.ARCHIVOS.delete(`modelos/${id}.glb`), env.ARCHIVOS.delete(`fotos/${id}.jpg`)]);
          return new Response(null, { status: 204 });
        }
      }

      return error(404, "Ruta no encontrada");
    } catch (e) {
      console.error(e);
      return error(500, "Error interno de la API");
    }
  },
} satisfies ExportedHandler<Env>;

async function crearModelo(request: Request, env: Env, sql: Sql): Promise<Response> {
  const tipo = request.headers.get("content-type") ?? "";
  if (!tipo.includes("multipart/form-data")) return error(415, "Se espera multipart/form-data");

  const form = await request.formData();
  const nombre = String(form.get("nombre") ?? "").trim();
  // Con multipart, los campos de archivo llegan como File (los tipos de Workers dicen string).
  const foto = form.get("foto") as unknown as File | string | null;
  const modelo = form.get("modelo") as unknown as File | string | null;
  const poligonos = Number(form.get("poligonos"));
  const proveedor = String(form.get("proveedor") ?? "desconocido").slice(0, 60);
  const ancho = Number(form.get("ancho_px")) || null;
  const alto = Number(form.get("alto_px")) || null;

  if (nombre.length < 1 || nombre.length > 80) return error(400, "El nombre debe tener entre 1 y 80 caracteres");
  if (foto === null || typeof foto === "string" || foto.size === 0) return error(400, "Falta la foto");
  if (modelo === null || typeof modelo === "string" || modelo.size === 0) return error(400, "Falta el modelo 3D");
  if (foto.size > MAX_FOTO) return error(413, "La foto pasa de 5 MB");
  if (modelo.size > MAX_MODELO) return error(413, "El modelo 3D pasa de 20 MB");
  if (!Number.isInteger(poligonos) || poligonos <= 0) return error(400, "Polígonos inválidos");

  const bytesModelo = await modelo.arrayBuffer();
  // Un .glb válido empieza con la palabra mágica «glTF».
  if (new TextDecoder().decode(bytesModelo.slice(0, 4)) !== "glTF") return error(400, "El archivo no es un .glb válido");

  const id = crypto.randomUUID();
  const claveFoto = `fotos/${id}.jpg`;
  const claveModelo = `modelos/${id}.glb`;

  // Primero los archivos; si la base falla, se borran para no dejar basura.
  await Promise.all([
    env.ARCHIVOS.put(claveFoto, await foto.arrayBuffer()),
    env.ARCHIVOS.put(claveModelo, bytesModelo),
  ]);
  try {
    await sql.transaction((tx) => [
      tx.query(
        "INSERT INTO modelos (id, usuario_id, nombre, estado, poligonos) VALUES ($1, $2, $3, 'listo', $4)",
        [id, env.USUARIO_DEMO, nombre, poligonos],
      ),
      tx.query(
        "INSERT INTO fotos (modelo_id, clave_r2, ancho_px, alto_px, tamano_bytes) VALUES ($1, $2, $3, $4, $5)",
        [id, claveFoto, ancho, alto, foto.size],
      ),
      tx.query(
        "INSERT INTO trabajos_generacion (modelo_id, proveedor, estado, terminado_en) VALUES ($1, $2, 'terminado', now())",
        [id, proveedor],
      ),
      tx.query(
        "INSERT INTO archivos_modelo (modelo_id, formato, clave_r2, tamano_bytes) VALUES ($1, 'glb', $2, $3)",
        [id, claveModelo, bytesModelo.byteLength],
      ),
    ]);
  } catch (e) {
    await Promise.all([env.ARCHIVOS.delete(claveFoto), env.ARCHIVOS.delete(claveModelo)]);
    throw e;
  }

  const [fila] = (await sql.query(`${SELECT_MODELO} WHERE m.id = $1`, [id])) as Fila[];
  return json(fila, 201);
}
