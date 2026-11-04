# Product Backlog de Creative 3D Builder

> El orden de esta tabla es el de la vista «Backlog» del tablero; lo mantiene el PO. Cada fila es un issue del
> repositorio, y `scripts/crear-backlog.sh` los crea todos con `gh`, idempotente.

**Épicas:** Base (técnicas) · Explorar · Crear · Visualizar · Compartir.
**Historia de referencia:** HU-04 «Detalle de un modelo» = **2 puntos**. Escala: 1, 2, 3, 5, 8. Nada de 13.
**Hitos de GitHub:** uno por entregable (*Entregable 1 · Fase inicial*, *Entregable 2 · Foto a modelo 3D*,
*Entregable 3 · Realidad aumentada*).

## Backlog ordenado

| Orden | ID | Issue | Épica | Elemento | MoSCoW | Pts | Entregable |
|---|---|---|---|---|---|---|---|
| 1 | TEC-01 | #14 | Base | Proyecto Android en Kotlin + Compose, ramas y protección de `master` | Must | 2 | 1 |
| 2 | TEC-02 | #15 | Base | Arquitectura MVVM: pantalla, ViewModel y contrato de repositorio | Must | 3 | 1 |
| 3 | TEC-03 | #16 | Base | Esquema de base de datos en Neon (migración 001 y semilla) | Must | 3 | 1 |
| 4 | HU-05 | #18 | Explorar | Como estudiante, quiero ver la lista de mis modelos 3D para retomar los que ya hice | Must | 3 | 1 |
| 5 | HU-04 | #19 | Explorar | Como estudiante, quiero abrir uno de mis modelos para ver su estado, su fecha y su tamaño | Must | 2 | 1 |
| 6 | TEC-05 | #24 | Base | API en Cloudflare Workers conectada a Neon | Must | 5 | 2 |
| 7 | TEC-06 | #25 | Base | Almacenamiento de fotos y modelos en Cloudflare R2 | Must | 3 | 2 |
| 8 | HU-07 | #26 | Crear | Como estudiante, quiero tomar la foto del objeto con la cámara o elegirla de la galería para crear su modelo 3D | Must | 3 | 2 |
| 9 | HU-02 | #27 | Crear | Como estudiante, quiero convertir la foto en un modelo 3D para no tener que modelarlo a mano | Must | 3 | 2 |
| 10 | HU-01 | #28 | Visualizar | Como estudiante, quiero girar el modelo con el dedo para verlo desde todos sus ángulos | Must | 3 | 2 |
| | | | | **— línea del MVP · 10 elementos · 30 puntos —** | | | |
| 11 | HU-03 | #17 | Explorar | Como estudiante, quiero ver al abrir la app qué puedo hacer con ella para empezar sin que nadie me explique | Should | 2 | 1 |
| 12 | HU-14 | #37 | Crear | Como estudiante, quiero reintentar la conversión de una foto que falló para no crear el modelo otra vez | Should | 2 | 2 |
| 13 | HU-08 | #29 | Crear | Como estudiante, quiero saber si mi modelo se está procesando o falló para no esperar sin saber | Should | 2 | 2 |
| 14 | HU-10 | #30 | Explorar | Como estudiante, quiero borrar un modelo que salió mal para mantener mi lista ordenada | Should | 2 | 2 |
| 15 | HU-06 | #32 | Visualizar | Como estudiante, quiero poner mi modelo sobre una mesa o el piso a través de la cámara para ver su tamaño real | Should | 5 | 3 |
| 16 | HU-11 | #33 | Explorar | Como estudiante, quiero cambiar el nombre de un modelo para encontrarlo después | Should | 1 | 3 |
| 17 | HU-09 | #34 | Compartir | Como estudiante, quiero exportar el modelo para abrirlo en Blender o imprimirlo en 3D | Could | 3 | 3 |
| 18 | HU-12 | #35 | Compartir | Como estudiante, quiero compartir un enlace a mi modelo para que un compañero lo vea sin instalar nada | Could | 3 | 3 |
| 19 | HU-13 | #36 | Visualizar | Como estudiante, quiero usar la app en modo oscuro para no cansar la vista de noche | Could | 2 | 3 |

| Grupo | Elementos | Puntos | % |
|---|---|---|---|
| Must | 10 | 30 | **58 %** |
| Should | 6 | 14 | 27 % |
| Could | 3 | 8 | 15 % |
| **Total** | **19** | **52** | 100 % |

**MVP:** 10 elementos · 30 de 52 puntos · **58 %** (tope del curso: 60 %). Todas las actividades del usuario tienen al
menos una historia sobre la línea: explorar (HU-05, HU-04), crear (HU-07, HU-02) y visualizar (HU-01).

### Qué cambió al refinar, y por qué

| Elemento | Antes | Después | Motivo |
|---|---|---|---|
| HU-02 | Crear el modelo desde la foto · 5 pts | Crear el modelo · 3 pts + **HU-14** reintentar · 2 pts | Mezclaba dos caminos (P de SPIDR); con 5 el MVP pasaba del 60 % |
| HU-03 | Pantalla de inicio · Must | Should | La app cumple su objetivo sin ella; ya está construida en el entregable 1 |
| HU-06 | Realidad aumentada · Must | Should | «Candidato para el Hito 3» según la definición del curso; es el entregable 3 |
| #1–#12 | Editor de primitivas | Se conservan en «Backlog» | Cambio de rumbo, ver [`vision-y-alcance.md`](vision-y-alcance.md) |

## Elementos de investigación y documentación

No suman al backlog del producto. Tipo «Investigación», etiqueta `investigacion`. Pasan de «En revisión» a «Hecho» sin
pasar por «En dispositivo».

| ID | Issue | Elemento | Pts | Entregable | Estado |
|---|---|---|---|---|---|
| INV-04 | #20 | Prueba técnica del examen: Mis Modelos 3D con datos simulados | 3 | 1 | Hecho (PR #13) |
| INV-02 | #21 | Validar los criterios con dos estudiantes reales | 3 | 1 | **Pendiente** |
| INV-07 | #22 | Documentación y evidencias del entregable 1 | 2 | 1 | En revisión |
| INV-05 | #23 | Elegir el servicio de imagen a 3D (costo, límites y formato) | 2 | 2 | Backlog |
| INV-06 | #31 | Comprobar la compatibilidad con ARCore del Xiaomi del equipo | 1 | 3 | Backlog |
