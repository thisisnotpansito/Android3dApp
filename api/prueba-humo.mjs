// Prueba de humo de la API. Uso: node api/prueba-humo.mjs <URL> <modelo.glb> <foto.jpg>
// Lista, 404, crear con un modelo real, bajar el .glb, validación y borrar (deja la base como estaba).
import { readFileSync } from "node:fs";
const B = process.argv[2], glbPath = process.argv[3], fotoPath = process.argv[4];
const ok = (c, m) => console.log((c ? "✔ " : "✘ ") + m);

let r = await fetch(`${B}/modelos`); const lista = await r.json();
ok(r.status === 200 && lista.length >= 6, `GET /modelos → ${r.status}, ${lista.length} modelos`);
r = await fetch(`${B}/modelos/00000000-0000-4000-8000-0000000000ff`); ok(r.status === 404, `GET id inexistente → ${r.status}`);
r = await fetch(`${B}/modelos/no-es-uuid`); ok(r.status === 404, `GET id inválido → ${r.status}`);

const f = new FormData();
f.set("nombre", "Prueba de la API"); f.set("poligonos", "8192"); f.set("proveedor", "local-inflado");
f.set("ancho_px", "1200"); f.set("alto_px", "900");
f.set("foto", new Blob([readFileSync(fotoPath)], { type: "image/jpeg" }), "foto.jpg");
f.set("modelo", new Blob([readFileSync(glbPath)], { type: "model/gltf-binary" }), "modelo.glb");
r = await fetch(`${B}/modelos`, { method: "POST", body: f }); const nuevo = await r.json();
ok(r.status === 201 && nuevo.tiene_modelo, `POST /modelos → ${r.status}, id ${nuevo.id}, tiene_modelo=${nuevo.tiene_modelo}`);

r = await fetch(`${B}/modelos/${nuevo.id}/modelo.glb`); const glb = new Uint8Array(await r.arrayBuffer());
ok(r.status === 200 && new TextDecoder().decode(glb.slice(0, 4)) === "glTF" && glb.length === readFileSync(glbPath).length, `GET modelo.glb → ${r.status}, ${glb.length} bytes, igual al subido`);

const malo = new FormData(); malo.set("nombre", ""); r = await fetch(`${B}/modelos`, { method: "POST", body: malo });
ok(r.status === 400, `POST sin datos → ${r.status} ${(await r.json()).error}`);

r = await fetch(`${B}/modelos/${nuevo.id}`, { method: "DELETE" }); ok(r.status === 204, `DELETE → ${r.status}`);
r = await fetch(`${B}/modelos/${nuevo.id}`); ok(r.status === 404, `GET después de borrar → ${r.status}`);
