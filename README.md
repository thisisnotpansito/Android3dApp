# Creative 3D Builder

**De una foto a un modelo 3D que puedes girar y poner en tu espacio · Android nativo (Kotlin + Jetpack Compose)**

Proyecto del curso *Desarrollo de Aplicaciones Móviles* (Ingeniería Informática, VIII semestre, UNIPAZ), equipo
**Los Chontos**. Marco de trabajo: Scrumban con fases de Mobile-D.

> **Estado: entregable 2 · foto a modelo 3D (versión 2.0).** Se toma o se elige una foto, la app genera un modelo 3D
> real (`.glb`) **en el teléfono**, lo sube a la **API en Cloudflare Workers**, que lo guarda en **Neon**, y se puede
> **girar en 360°**. Hay dos modelos subidos (una taza y un cactus). Falta probarlo en el Xiaomi. Detalle en [`docs/entregables/entregable-2.md`](docs/entregables/entregable-2.md).

## Objetivo del Producto

> «Que un estudiante de ingeniería convierta la foto de un objeto en un modelo 3D que pueda girar en 360° y ver sobre una
> superficie real, en menos de dos minutos y desde un celular de gama media».

## Visión

**Para** estudiantes de ingeniería **que** quieren ver un objeto en 3D sin saber modelar ni tener un computador a mano,
**Creative 3D Builder es una** app móvil de captura 3D **que** convierte la foto de un objeto en un modelo que se gira en
360° y se pone sobre una superficie real. **A diferencia de** modelar en Blender o de las apps de escaneo que piden
decenas de fotos, **nuestro producto** parte de una sola foto y funciona en un celular de gama media.

## Los tres entregables

| Entregable | El usuario puede… | Hito de GitHub | Estado |
|---|---|---|---|
| **1 · Fase inicial** | Abrir la app y recorrer su interfaz con datos de prueba; base de datos creada | *Entregable 1 · Fase inicial* | Fusionado (PR #38) |
| **2 · Foto a modelo 3D** | Tomar una foto, obtener su modelo 3D y girarlo en 360° | *Entregable 2 · Foto a modelo 3D* | Construido · API publicada |
| **3 · Realidad aumentada** | Ver el modelo sobre una mesa o el piso a través de la cámara | *Entregable 3 · Realidad aumentada* | Backlog |

## Roles del equipo

| Integrante | Rol |
|---|---|
| Alexander Ricaurte | Product Owner |
| Cristian Meneses | Scrum Master |
| Joan Pedrozo | Developer |
| Erick Martinez | Developer · responsable de base de datos y documentación |

Los cuatro son Developers. Roles confirmados por el equipo el 4 de noviembre de 2026. **Rotación:** *por definir por el
equipo*.

## Alcance (MoSCoW)

| Must | Should | Could | Won't (este semestre) |
|---|---|---|---|
| Mis Modelos 3D (HU-05) | Pantalla de inicio (HU-03) | Exportar .glb / .stl (HU-09) | Versión iOS |
| Detalle de un modelo (HU-04) | Realidad aumentada (HU-06) | Compartir con un enlace (HU-12) | Editar la malla a mano |
| Foto con cámara o galería (HU-07) | Reintentar una generación (HU-14) | Modo oscuro (HU-13) | Multijugador |
| Crear el modelo desde la foto (HU-02) | Estado de la generación (HU-08) | | Pagos |
| Girar en 360° (HU-01) | Borrar (HU-10) · Renombrar (HU-11) | | Raytracing |

Las Must suman **30 de 52 puntos (58 %)**. Backlog completo: [`docs/backlog.md`](docs/backlog.md).

**Fuera del alcance:** iOS, editar la malla a mano, fotogrametría con muchas fotos, cuentas con contraseña (se decide en
el entregable 2) y pagos.

## Dónde está cada cosa

| Tema | Archivo |
|---|---|
| **Actores, eventos, flujo y arquitectura** (con diagramas) | [`docs/actores-eventos-flujo.md`](docs/actores-eventos-flujo.md) · [PDF](docs/actores-eventos-flujo.pdf) |
| Visión, objetivo, MoSCoW y cambio de rumbo | [`docs/vision-y-alcance.md`](docs/vision-y-alcance.md) |
| Backlog ordenado y estimado | [`docs/backlog.md`](docs/backlog.md) |
| Tablero de GitHub Projects | [`docs/tablero.md`](docs/tablero.md) |
| Flujo de trabajo y Definición de Terminado | [`docs/flujo-de-trabajo.md`](docs/flujo-de-trabajo.md) |
| Criterios de aceptación (Gherkin) | [`docs/criterios/`](docs/criterios/) |
| Base de datos: diagrama, diccionario y decisiones | [`docs/base-de-datos.md`](docs/base-de-datos.md) · [`db/`](db/) |
| Registro de los entregables | [1](docs/entregables/entregable-1.md) · [2](docs/entregables/entregable-2.md) |
| INV-05 · cómo se genera el 3D | [`docs/inv-05-servicio-imagen-3d.md`](docs/inv-05-servicio-imagen-3d.md) |
| API (Cloudflare Workers) | [`api/`](api/) |
| Informe formal del entregable 1 (APA 7) | [Word](docs/entregables/Informe-Entregable-1.docx) · [PDF](docs/entregables/Informe-Entregable-1.pdf) |
| Capturas | [entregable 1](docs/capturas/entregable-1/) · [entregable 2](docs/capturas/entregable-2/) |
| Examen del Sprint 1 | [`examen-sprint1.md`](examen-sprint1.md) |

## Arquitectura (MVVM)

```
app/src/main/java/com/example/android3dapp/
├── domain/      Modelo3D, EstadoModelo y NuevoModelo: Kotlin puro, mismos campos que la tabla `modelos`
├── data/        ModeloRepository (contrato) · RepositorioSimulado + AlmacenLocal (en el teléfono) · RepositorioRemoto (API)
│   ├── generacion/  GeneradorModelo3D (contrato) · MallaInflada (foto → malla) · EscritorGlb (malla → .glb)
│   └── fotos/       LectorFotos (EXIF, 1600 px, JPEG)
├── ui/          Una carpeta por pantalla: inicio/ · modelos/ · detalle/ · nuevo/ · visor/ · comun/ · theme/
│                Cada pantalla lee su ViewModel (StateFlow) y nunca llama al repositorio
└── app/         AppContenedor (elige la implementación) y Navegacion (rutas, detalle por id)
db/              Migraciones y semillas de PostgreSQL (Neon)
api/             API en Cloudflare Workers (TypeScript): Neon + Workers KV
```

**La regla:** las pantallas solo hablan con su ViewModel, y los ViewModel solo con el **contrato**. Por eso cambiar los
datos simulados por la API del entregable 2 es cambiar **una línea** en `app/AppContenedor.kt`.

## Ejecutar

Requisitos: Android Studio (trae su propio JDK), Android SDK 37 y un Android con depuración USB, o un emulador.

```bash
./gradlew assembleDebug        # compila el APK
./gradlew testDebugUnitTest    # 16 pruebas
./gradlew lintDebug            # meta: 0 errores
./gradlew installDebug         # instala en el teléfono conectado
```

En la pantalla «Mis Modelos 3D» de una compilación de depuración, el botón **Escenario** cambia entre *Con datos*,
*Vacía*, *Falla* y *Cargando*.

**Crear un modelo:** Inicio → «Crear desde una foto» → «Tomar foto» o «Galería» → nombre → «Generar modelo 3D» → en el
detalle, «Girar en 360°». Mejor con el objeto centrado sobre un fondo liso.

## Flujo de trabajo

- `master` protegida: todo entra por pull request con una revisión y `Closes #N`.
- Una rama por elemento (`hu-05-…`, `tec-03-…`) o por entregable (`entregable-1/fase-inicial`).
- Los commits empiezan por el ID: `TEC-02: …`.
- Columnas: Backlog · Listo · En desarrollo · En revisión · En dispositivo · Hecho. Políticas, WIP y Definición de
  Terminado en [`docs/flujo-de-trabajo.md`](docs/flujo-de-trabajo.md).

## Historial

| Entregable | Objetivo | Puntos | Resultado |
|---|---|---|---|
| Sprint 1 (examen) | La lista de «Mis Modelos 3D» funciona en un Android con sus cuatro situaciones | — | PR #13 fusionado |
| 1 · Fase inicial | Interfaz completa con datos simulados y esquema de base de datos creado en Neon | 15 | Fusionado (PR #38) · falta probarlo en el Xiaomi |
| 2 · Foto a modelo 3D | Un estudiante toma o elige la foto de un objeto y, en segundos, lo ve en 3D y lo gira en 360° | 25 | Construido · API publicada · falta probarlo en el Xiaomi |

---
Desarrollo de Aplicaciones Móviles · Ingeniería Informática · UNIPAZ · 2026
