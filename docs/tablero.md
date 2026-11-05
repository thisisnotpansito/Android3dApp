# El tablero de Creative 3D Builder en GitHub Projects

> Sprint 0 · Paso 5 de la guía-taller. Tablero: <https://github.com/users/thisisnotpansito/projects/2>

## Estado (entregable 1)

| Parte | Estado | Cómo |
|---|---|---|
| Etiquetas `historia`, `tecnica`, `defecto`, `investigacion`, `bloqueada` | ✔ Hecho | `scripts/crear-backlog.sh` |
| Hitos por entregable (1, 2 y 3) | ✔ Hecho | `scripts/crear-backlog.sh` |
| 24 issues con ID (HU- / TEC- / INV-), cuerpo con historia, criterios y metadatos | ✔ Hecho (#14–#37) | `scripts/crear-backlog.sh` |
| Nombre «Creative 3D Builder», descripción (Objetivo del Producto) y README del proyecto | ✔ Hecho | `scripts/configurar-tablero.sh` |
| Status con seis columnas; cada tarjeta en su columna (#1–#12 en Backlog, PR #38 en «En revisión») | ✔ Hecho | `scripts/configurar-tablero.sh` |
| Campos Tipo, Prioridad, Épica y Puntos, con valores en las 24 tarjetas del backlog nuevo | ✔ Hecho | `scripts/configurar-tablero.sh` |
| Campo **Sprint** (iteración de 1 semana): *Entregable 1* · *Entregable 2 · semana 1 y 2* · *Entregable 3 · semana 1 y 2*; los elementos del entregable 1 asignados | ✔ Hecho | API de GraphQL (`createProjectV2Field`) |
| Límites WIP, tres vistas y workflows | ⏳ A mano (la API no lo permite) | Pasos de abajo |

## 1. Columnas, límites y campos

**Status** (columnas, en este orden): Backlog · Listo · En desarrollo · En revisión · En dispositivo · Hecho.

**Límites WIP** (menú de cada columna → *Set column limit*, solo en vistas Board): Listo 5 · En desarrollo 4 ·
En revisión 2 · En dispositivo 2.

| Campo | Tipo | Valores |
|---|---|---|
| Tipo | Single select | Historia · Técnica · Defecto · Investigación |
| Prioridad | Single select | Must · Should · Could |
| Épica | Single select | Base · Explorar · Crear · Visualizar · Compartir |
| Puntos | Number | 1 · 2 · 3 · 5 · 8 |
| Sprint | Iteration | 1 semana, empezando el día de la Sesión B *(a mano: vista de tabla → + → New field → Iteration)* |

## 2. Cargar el backlog

```bash
gh auth refresh -s project          # una sola vez
./scripts/crear-backlog.sh           # etiquetas, hitos e issues (ya hecho; es idempotente)
./scripts/configurar-tablero.sh      # nombre, Status, campos y valores de cada tarjeta
```

## 3. Vistas

| Vista | Layout | Configuración |
|---|---|---|
| **Backlog** | Table | Filtro `-status:Hecho`, orden manual del PO, *View → Field sum → Puntos* |
| **Sprint actual** | Board | Columnas por Status, filtro `sprint:@current`. Es la que se proyecta en el Daily |
| **Por épica** | Table | *View → Group by → Épica* |
| **Por entregable** | Table | *View → Group by → Milestone* |

## 4. Flujos automáticos

**⋯ → Workflows:** activar *Item added to project* → Backlog; *Item closed* → Hecho; *Pull request merged* → Hecho.
El PR que dice `Closes #N` cierra el issue al fusionarse y la tarjeta llega sola a «Hecho».

## Errores frecuentes

| Síntoma | Qué hacer |
|---|---|
| `viewerCanUpdate: false` o «Resource not accessible» al editar el proyecto | Pedir al dueño el rol Write en *Manage access* |
| `gh` responde «missing required scopes» | `gh auth refresh -s project` |
| No aparece *Set column limit* | La vista está en Table o Roadmap: cámbiala a Board |
| La plantilla de issue no sale | Debe estar en `.github/ISSUE_TEMPLATE/`, terminar en `.yml` y estar en `master` |
