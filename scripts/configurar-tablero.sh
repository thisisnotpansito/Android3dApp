#!/usr/bin/env bash
# =============================================================================
# Configura el tablero de GitHub Projects según la guía-taller del Sprint 0:
#   · nombre, descripción (Objetivo del Producto) y README del proyecto
#   · Status con las seis columnas del curso
#   · campos Tipo, Prioridad, Épica y Puntos
#   · añade los issues del backlog (HU-/TEC-/INV-) y rellena sus campos
#
# Requiere rol Write o Admin en el proyecto (lo da su dueño en
# Project → ⋯ → Settings → Manage access) y el permiso `project` del CLI:
#   gh auth refresh -s project
#
# Lo que la API de GitHub no permite y se hace a mano (docs/tablero.md):
# límites WIP, campo Sprint de tipo iteración, vistas y workflows.
# =============================================================================
set -euo pipefail
OWNER="${OWNER:-thisisnotpansito}"
NUM="${NUM:-2}"
REPO="${REPO:-thisisnotpansito/Android3dApp}"

OBJETIVO="Que un estudiante de ingeniería convierta la foto de un objeto en un modelo 3D que pueda girar en 360° y ver sobre una superficie real, en menos de dos minutos y desde un celular de gama media."

PID="$(gh project view "$NUM" --owner "$OWNER" --format json --jq .id)"
gh project edit "$NUM" --owner "$OWNER" --title "Creative 3D Builder" --description "$OBJETIVO" \
  --readme "$(cat docs/tablero-readme.md)" >/dev/null
echo "✓ nombre, descripción y README"

# --- Status: seis columnas (reemplaza las opciones; los valores se vuelven a poner abajo)
SID="$(gh project field-list "$NUM" --owner "$OWNER" --format json --jq '.fields[] | select(.name=="Status") | .id')"
gh api graphql -f query='mutation($f:ID!){ updateProjectV2Field(input:{fieldId:$f, singleSelectOptions:[
  {name:"Backlog",        color:GRAY,   description:"Cualquier idea con título claro. El PO decide su orden."},
  {name:"Listo",          color:BLUE,   description:"INVEST, criterios de aceptación y puntos."},
  {name:"En desarrollo",  color:YELLOW, description:"Asignada y con su rama. Una tarjeta por persona."},
  {name:"En revisión",    color:ORANGE, description:"PR abierto que enlaza el issue; lint limpio."},
  {name:"En dispositivo", color:PURPLE, description:"PR aprobado; APK instalado en un Android físico."},
  {name:"Hecho",          color:GREEN,  description:"Cumple la Definición de Terminado y el PR está fusionado."}
]}){ projectV2Field { ... on ProjectV2SingleSelectField { id } } } }' -f f="$SID" >/dev/null
echo "✓ Status con seis columnas"

campo() { # nombre tipo [opciones]
  if ! gh project field-list "$NUM" --owner "$OWNER" --format json --jq '.fields[].name' | grep -Fxq "$1"; then
    if [ "$2" = SINGLE_SELECT ]; then
      gh project field-create "$NUM" --owner "$OWNER" --name "$1" --data-type SINGLE_SELECT --single-select-options "$3" >/dev/null
    else
      gh project field-create "$NUM" --owner "$OWNER" --name "$1" --data-type "$2" >/dev/null
    fi
  fi
}
campo Tipo SINGLE_SELECT "Historia,Técnica,Defecto,Investigación"
campo Prioridad SINGLE_SELECT "Must,Should,Could"
campo "Épica" SINGLE_SELECT "Base,Explorar,Crear,Visualizar,Compartir"
campo Puntos NUMBER
echo "✓ campos Tipo, Prioridad, Épica y Puntos"

CAMPOS="$(gh project field-list "$NUM" --owner "$OWNER" --format json)"
fid()  { jq -r --arg n "$1" '.fields[] | select(.name==$n) | .id' <<<"$CAMPOS"; }
oid()  { jq -r --arg n "$1" --arg o "$2" '.fields[] | select(.name==$n) | .options[] | select(.name==$o) | .id' <<<"$CAMPOS"; }
poner() { # item campo valor
  local f o; f="$(fid "$2")"; o="$(oid "$2" "$3")"
  [ -n "$o" ] && gh project item-edit --id "$1" --project-id "$PID" --field-id "$f" --single-select-option-id "$o" >/dev/null
}

# Columna de cada elemento al terminar el entregable 1
status_de() {
  case "$1" in
    INV-04) echo "Hecho" ;;
    TEC-02|TEC-03|HU-03|HU-05|HU-04|INV-07) echo "En revisión" ;;
    TEC-01) echo "En desarrollo" ;;
    INV-02) echo "Listo" ;;
    *) echo "Backlog" ;;
  esac
}

gh issue list -R "$REPO" --state all --limit 300 --json number,title,url,body |
jq -c '.[] | select(.title | test("^(HU|TEC|INV)-[0-9]+: "))' |
while read -r fila; do
  url="$(jq -r .url <<<"$fila")"; titulo="$(jq -r .title <<<"$fila")"; id="${titulo%%:*}"
  # La tabla de metadatos es la 3.ª línea del cuerpo: | Tipo | Prioridad | Épica | Puntos |
  meta="$(jq -r .body <<<"$fila" | sed -n 3p)"
  IFS='|' read -r _ tipo prio epica pts _ <<<"$meta"
  tipo="$(xargs <<<"$tipo")"; prio="$(xargs <<<"$prio")"; epica="$(xargs <<<"$epica")"; pts="$(xargs <<<"$pts")"
  item="$(gh project item-add "$NUM" --owner "$OWNER" --url "$url" --format json --jq .id)"
  poner "$item" Tipo "$tipo"
  [ "$prio" != "—" ] && poner "$item" Prioridad "$prio"
  poner "$item" "Épica" "$epica"
  gh project item-edit --id "$item" --project-id "$PID" --field-id "$(fid Puntos)" --number "$pts" >/dev/null
  poner "$item" Status "$(status_de "$id")"
  echo "✓ $titulo"
done

# Los issues antiguos (#1–#12, backlog anterior al cambio de rumbo) quedan en Backlog
gh project item-list "$NUM" --owner "$OWNER" --limit 200 --format json |
jq -r '.items[] | select((.status // "") == "") | .id' | while read -r item; do poner "$item" Status Backlog; done
echo "Listo. Falta lo manual de docs/tablero.md (WIP, Sprint, vistas y workflows)."
