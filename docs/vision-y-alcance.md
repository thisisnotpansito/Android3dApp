# Visión y alcance de Creative 3D Builder

> Sprint 0 · Paso 1 y Paso 2 de la guía-taller. Actualizado en el **entregable 1** (noviembre de 2026), cuando el
> equipo cambió el rumbo de un editor de primitivas a **foto → modelo 3D → realidad aumentada**.

## Problema

Los estudiantes de ingeniería tienen ideas de objetos y quieren verlas en 3D, pero modelar a mano exige un computador,
programas pesados como Blender y horas de práctica. Desde el celular, las apps de modelado son lentas, están llenas de
botones y agotan la batería.

- **Usuario principal:** estudiante universitario de ingeniería informática con un Android de gama media (persona en
  [`../examen-sprint1.md`](../examen-sprint1.md)).
- **Lo que lo frena hoy:** «Se me ocurren geometrías en el bus pero no tengo dónde bocetarlas en 3D».

## Declaración de visión (plantilla de Moore)

**Para** estudiantes de ingeniería **que** quieren ver un objeto en 3D sin saber modelar ni tener un computador a mano,
**Creative 3D Builder es una** app móvil de captura 3D **que** convierte la foto de un objeto en un modelo que se gira en
360° y se pone sobre una superficie real.
**A diferencia de** modelar en Blender o de las apps de escaneo que piden decenas de fotos, **nuestro producto** parte de
una sola foto y funciona en un celular de gama media.

## Objetivo del Producto

> «Que un estudiante de ingeniería convierta la foto de un objeto en un modelo 3D que pueda girar en 360° y ver sobre una
> superficie real, en menos de dos minutos y desde un celular de gama media».

Va también en la descripción del tablero de GitHub Projects.

| Parte del objetivo | Cómo se comprueba | Entregable |
|---|---|---|
| «un estudiante de ingeniería» | Prueba con dos estudiantes reales (INV-02) | 1 |
| «convierta la foto de un objeto en un modelo 3D» | HU-07 + HU-02 · RC-03 | 2 |
| «que pueda girar en 360°» | HU-01 · RC-04 | 2 |
| «y ver sobre una superficie real» | HU-06 | 3 |
| «en menos de dos minutos» | RC-03, medido con cronómetro en el teléfono | 2 |
| «desde un celular de gama media» | Pruebas en el Xiaomi del equipo · INV-06 | 1–3 |

## Alcance con MoSCoW

| Must | Should | Could | Won't (este semestre) |
|---|---|---|---|
| Lista de Mis Modelos 3D (HU-05) | Pantalla de inicio (HU-03) | Exportar .glb / .stl (HU-09) | Versión iOS |
| Detalle de un modelo (HU-04) | Ver el modelo en mi espacio, AR (HU-06) | Compartir con un enlace (HU-12) | Editor de mallas a mano |
| Elegir la foto: cámara o galería (HU-07) | Reintentar una generación fallida (HU-14) | Modo oscuro (HU-13) | Multijugador o modelos compartidos en tiempo real |
| Crear el modelo 3D desde la foto (HU-02) | Ver el estado de la generación (HU-08) | | Monetización o pagos |
| Girar el modelo en 360° (HU-01) | Borrar un modelo (HU-10) · Renombrar (HU-11) | | Renderizado hiperrealista (raytracing) |

Tareas técnicas que sostienen todo: TEC-01 (proyecto), TEC-02 (MVVM), TEC-03 (base de datos), TEC-05 (API) y TEC-06
(almacenamiento), todas Must.

**Regla del 60 %:** las Must suman **30 de 52 puntos (58 %)**. Detalle en [`backlog.md`](backlog.md).

**¿Por qué la realidad aumentada es Should y no Must?** Según la definición del curso, *Should* es «importante, pero la app
sigue sirviendo sin ello; candidato para el Hito 3». Sin AR, la app ya cumple «foto → modelo que se gira en 360°». Es el
**entregable 3** planeado, no un recorte.

**Revisión contra el Hito 3** (API + persistencia + función del dispositivo):

| Exige | Lo cubre | Prioridad |
|---|---|---|
| API | TEC-05 · API en Cloudflare Workers + servicio de imagen a 3D | Must |
| Persistencia | TEC-03 · PostgreSQL en Neon (ya creada) | Must |
| Función del dispositivo | HU-07 · cámara · HU-06 · ARCore | Must · Should |

## Cambio de rumbo (entregable 1)

| Antes (backlog #1–#12) | Después | Motivo |
|---|---|---|
| Editor de primitivas: cubo, raycasting, gizmos, color | Foto → modelo 3D | El usuario no quiere modelar a mano: quiere ver su idea en 3D rápido |
| Exportar .stl / .gltf como Should | Exportar como Could (HU-09) | Deja de ser el centro del producto |
| «Guardado en la nube» como Could | API + base de datos como Must (TEC-03, TEC-05) | Sin API no se cumple el Hito 3 |
| Objetivo «Lanzar una aplicación que permita…» | Objetivo medible (arriba) | El anterior era una lista de funciones, no un cambio observable |

Los issues #1 a #12 se conservan en el tablero, en «Backlog», como historia del refinamiento. El equipo decide si se
cierran.

## Fuera del alcance este semestre

- Publicar en Google Play o en iOS: se reparte el APK.
- Modelar o editar la malla a mano (vértices, caras, gizmos).
- Escaneo con varias fotos o con LiDAR (fotogrametría).
- Cuentas con contraseña y recuperación de contraseña: se decide en el entregable 2 si hay inicio de sesión.
- Garantizar la calidad del modelo de cualquier objeto: depende del servicio de imagen a 3D (INV-05).
