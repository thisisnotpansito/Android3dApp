#!/usr/bin/env bash
# =============================================================================
# Crea las etiquetas, los hitos (uno por entregable) y los issues del backlog de
# Creative 3D Builder. Es idempotente: si un issue con el mismo ID ya existe, lo salta.
#
# Uso (Git Bash, desde la carpeta del repositorio):
#   gh auth refresh -s project      # una sola vez
#   ./scripts/crear-backlog.sh
#
# Después: ./scripts/configurar-tablero.sh añade los issues al tablero y rellena
# Tipo, Prioridad, Épica, Puntos y Status (requiere permiso de escritura en el proyecto).
# =============================================================================
set -euo pipefail
REPO="${REPO:-thisisnotpansito/Android3dApp}"

etiqueta() { gh label create "$1" --color "$2" --description "$3" --force -R "$REPO" >/dev/null; }
etiqueta historia      2FD27A "Historia de usuario"
etiqueta tecnica       FFCB6B "Tarea técnica"
etiqueta defecto       FF6B7A "Defecto"
etiqueta investigacion 8FA3FF "Investigación, validación o documentación"
etiqueta bloqueada     B60205 "Bloqueada: se menciona en el Daily"

hito() {
  local t="$1" d="$2" f="$3"
  if ! gh api "repos/$REPO/milestones?state=all" --jq '.[].title' | grep -Fxq "$t"; then
    gh api "repos/$REPO/milestones" -f title="$t" -f description="$d" -f due_on="$f" >/dev/null
  fi
}
E1="Entregable 1 · Fase inicial"
E2="Entregable 2 · Foto a modelo 3D"
E3="Entregable 3 · Realidad aumentada"
hito "$E1" "Interfaz de la app en Kotlin + Compose con datos simulados y el esquema de la base de datos creado en Neon." "2026-11-11T23:59:59Z"
hito "$E2" "Subir una foto y obtener un modelo 3D que se gira en 360°. Cubre el Hito 3: API + persistencia + cámara." "2026-11-25T23:59:59Z"
hito "$E3" "Ver el modelo sobre una superficie real a través de la cámara (ARCore)." "2026-12-09T23:59:59Z"

EXISTENTES="$(gh issue list -R "$REPO" --state all --limit 300 --json title --jq '.[].title')"

# crear ID "Título" etiqueta hito Tipo Prioridad Épica Puntos "cuerpo o @archivo"
crear() {
  local id="$1" titulo="$2" etq="$3" hit="$4" tipo="$5" prio="$6" epica="$7" pts="$8" cuerpo="$9"
  if grep -q "^$id: " <<<"$EXISTENTES"; then echo "· ya existe $id"; return; fi
  local texto
  if [[ "$cuerpo" == @* ]]; then texto="$(cat "${cuerpo#@}")"; else texto="$cuerpo"; fi
  local meta="| Tipo | Prioridad | Épica | Puntos |
|---|---|---|---|
| $tipo | $prio | $epica | $pts |
"
  printf '%s\n%s\n' "$meta" "$texto" > /tmp/cuerpo-issue.md
  gh issue create -R "$REPO" --title "$id: $titulo" --label "$etq" --milestone "$hit" --body-file /tmp/cuerpo-issue.md
}

# ---------------------------------------------------------------- Entregable 1
crear TEC-01 "Proyecto Android en Kotlin + Compose, ramas y protección de master" tecnica "$E1" Técnica Must Base 2 \
"Proyecto Android Studio con Kotlin y Jetpack Compose (minSdk 24, targetSdk 37). Falta proteger \`master\`: todo entra por pull request con una revisión."
crear TEC-02 "Arquitectura MVVM: pantalla, ViewModel y contrato de repositorio" tecnica "$E1" Técnica Must Base 3 \
"Separar la pantalla, el ViewModel (StateFlow) y el contrato \`ModeloRepository\`. La pantalla nunca llama al repositorio; pasar de datos simulados a la API es cambiar una línea en \`app/AppContenedor.kt\`."
crear TEC-03 "Esquema de base de datos en Neon (migración 001 y semilla)" tecnica "$E1" Técnica Must Base 3 "@scripts/issues/TEC-03.md"
crear HU-03 "Pantalla de inicio" historia "$E1" Historia Should Explorar 2 "@scripts/issues/HU-03.md"
crear HU-05 "Lista de Mis Modelos 3D" historia "$E1" Historia Must Explorar 3 "@scripts/issues/HU-05.md"
crear HU-04 "Detalle de un modelo" historia "$E1" Historia Must Explorar 2 "@scripts/issues/HU-04.md"
crear INV-04 "Prueba técnica del examen: Mis Modelos 3D con datos simulados" investigacion "$E1" Investigación — Base 3 \
"Hecho en el PR #13 (rama \`examen/sprint1-tecnico\`): lista con cuatro estados y repositorio simulado."
crear INV-02 "Validar los criterios con dos estudiantes reales" investigacion "$E1" Investigación — Explorar 3 \
"Dos estudiantes de ingeniería (no del equipo), 15 minutos cada uno. Tres preguntas sobre hechos pasados («Cuénteme la última vez que…»). Guion en \`docs/validacion/\`. Resultado: una ficha por persona como comentario de este issue."
crear INV-07 "Documentación y evidencias del entregable 1" investigacion "$E1" Investigación — Base 2 \
"\`docs/\` (visión, backlog, tablero, flujo, base de datos, entregable 1), capturas del emulador y el informe formal."

# ---------------------------------------------------------------- Entregable 2
crear INV-05 "Elegir el servicio de imagen a 3D (costo, límites y formato)" investigacion "$E2" Investigación — Crear 2 \
"Comparar al menos tres servicios que convierten una foto en un modelo 3D (.glb): costo, cuota gratuita, tiempo de respuesta y calidad. Resultado: una tabla y una decisión en \`docs/\`."
crear TEC-05 "API en Cloudflare Workers conectada a Neon" tecnica "$E2" Técnica Must Base 5 \
"Worker con \`GET /modelos\`, \`GET /modelos/{id}\` y \`POST /modelos\`. Las credenciales de Neon viven como secreto del Worker, nunca en la app. Implementar \`RepositorioRemoto\` en la app."
crear TEC-06 "Almacenamiento de fotos y modelos en Cloudflare R2" tecnica "$E2" Técnica Must Base 3 \
"Bucket R2 para fotos (.jpg) y modelos (.glb). La base guarda solo la clave (\`fotos.clave_r2\`, \`archivos_modelo.clave_r2\`)."
crear HU-07 "Elegir la foto con la cámara o la galería" historia "$E2" Historia Must Crear 3 \
"Como estudiante, quiero tomar la foto del objeto con la cámara o elegirla de la galería para crear su modelo 3D. Escenarios: foto tomada · permiso de cámara negado (ofrece galería) · foto demasiado oscura."
crear HU-02 "Crear un modelo 3D a partir de una foto" historia "$E2" Historia Must Crear 3 \
"Como estudiante, quiero convertir la foto en un modelo 3D para no tener que modelarlo a mano. Escenarios: modelo generado · el servicio falla (mensaje llano) · sin conexión (queda como borrador). Reintentar una generación fallida es HU-14."
crear HU-14 "Reintentar una generación fallida" historia "$E2" Historia Should Crear 2 \
"Como estudiante, quiero reintentar la conversión de una foto que falló para no tener que crear el modelo otra vez. Partida de HU-02 por caminos (P de SPIDR)."
crear HU-01 "Girar el modelo en 360°" historia "$E2" Historia Must Visualizar 3 \
"Como estudiante, quiero girar el modelo con el dedo para verlo desde todos sus ángulos. Visor con SceneView. Escenarios: girar y hacer zoom · el archivo no carga · teléfono de gama media (fluido a 30 fps o más)."
crear HU-08 "Ver el estado de la generación" historia "$E2" Historia Should Crear 2 \
"Como estudiante, quiero saber si mi modelo se está procesando o falló para no esperar sin saber. Usa \`trabajos_generacion\`."
crear HU-10 "Borrar un modelo" historia "$E2" Historia Should Explorar 2 \
"Como estudiante, quiero borrar un modelo que salió mal para mantener mi lista ordenada. Con confirmación; borra también sus archivos en R2."

# ---------------------------------------------------------------- Entregable 3
crear INV-06 "Comprobar la compatibilidad con ARCore del Xiaomi del equipo" investigacion "$E3" Investigación — Visualizar 1 \
"Revisar la lista oficial de dispositivos compatibles con ARCore e instalar «Google Play Services for AR» en el teléfono de pruebas."
crear HU-06 "Ver el modelo en mi espacio (realidad aumentada)" historia "$E3" Historia Should Visualizar 5 \
"Como estudiante, quiero poner mi modelo sobre una mesa o el piso a través de la cámara para ver su tamaño real. Escenarios: superficie detectada · teléfono sin ARCore (mensaje y vuelta al visor 360°) · poca luz."
crear HU-11 "Renombrar un modelo" historia "$E3" Historia Should Explorar 1 \
"Como estudiante, quiero cambiar el nombre de un modelo para encontrarlo después."
crear HU-09 "Exportar el modelo en .glb o .stl" historia "$E3" Historia Could Compartir 3 \
"Como estudiante, quiero exportar el modelo para abrirlo en Blender o imprimirlo en 3D."
crear HU-12 "Compartir el modelo con un enlace" historia "$E3" Historia Could Compartir 3 \
"Como estudiante, quiero compartir un enlace a mi modelo para que un compañero lo vea sin instalar nada."
crear HU-13 "Modo oscuro" historia "$E3" Historia Could Visualizar 2 \
"Como estudiante, quiero usar la app en modo oscuro para no cansar la vista de noche."

echo "Listo. Revisa: gh issue list -R $REPO --milestone \"$E1\""
