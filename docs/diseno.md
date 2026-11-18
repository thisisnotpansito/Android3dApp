# Kit UX/UI de Creative 3D Builder

> Entregable 2 · Guía 31 «UX y UI en tu app» y su roadmap de ocho paradas
> (<https://ux-ui-appmovil-ecoreport.netlify.app/>). Versión de la app: **2.0** (PR #40). Medido el 18 de noviembre de
> 2026 en un emulador Pixel 6 con Android 16. Lo que solo se puede responder con personas está marcado **pendiente**:
> no se inventa.

## Resumen

| Parada | Pieza del kit | Estado | Dónde |
|---|---|---|---|
| 1 · Persona y escena | Ficha de persona + escena | **Supuestos** (0 de 3 entrevistas) | §1 |
| 2 · Recorrido y tarea | Recorrido + flujo con toques | Hecho | §2 · [`actores-eventos-flujo.md`](actores-eventos-flujo.md) |
| 3 · Navegación | Árbol de pantallas justificado | Hecho | §3 |
| 4 · Boceto → prototipo | Prototipo navegable en Figma 360 × 800 | **Pendiente** (hay app funcional, no Figma) | §4 |
| 5 · Sistema visual | Paleta medida + tema en el código | Hecho · 15 de 15 pares pasan | §5 |
| 6 · Componentes y textos | Inventario de componentes + términos | Hecho | §6 |
| 7 · Accesibilidad | Lista aprobada + prueba automática | Parcial: 5 de 10 puntos pasan; sin prueba automática | §7 |
| 8 · Evaluar y mejorar | Auditoría + SUS + plan de mejora | Auditoría hecha (12 hallazgos); **SUS pendiente** | §8 |

---

## 1 · Persona y escena

| Campo | Contenido | ¿Confirmado? |
|---|---|---|
| Persona | Estudiante de ingeniería informática, 20–24 años | Supuesto (examen del Sprint 1) |
| Teléfono | Xiaomi de gama media, Android 13 o más | Supuesto |
| Conexión | Inestable: datos móviles en el bus | Supuesto |
| Quiere lograr | Ver en 3D un objeto o una idea sin computador ni saber modelar | Supuesto |
| La frena hoy | Los programas 3D son pesados, complejos y piden PC | Supuesto |
| Frase | «Se me ocurren geometrías en el bus pero no tengo dónde bocetarlas en 3D» | Supuesta |

**Escena difícil y frecuente:** en el bus o en la cafetería, de pie o con una sola mano libre, el objeto sobre una mesa
cualquiera (con textura, sombras y cosas alrededor), con poca batería y datos limitados. Tiene **menos de dos minutos**.

> **Entrevistas: 0 de 3.** Es el issue INV-02 (#21). Mientras no se hagan, toda la ficha queda como supuesto.

## 2 · Recorrido y tarea

**Tarea principal:** convertir la foto de un objeto en un modelo 3D y girarlo.

| Camino | Toques | Recorrido |
|---|---|---|
| Feliz, desde la galería | **6** + escribir el nombre | Inicio → «Crear desde una foto» (1) → «Galería» (2) → elegir la foto (3) → campo de nombre (4) → «Generar modelo 3D» (5) → se abre el detalle solo → «Girar en 360°» (6) |
| Feliz, con la cámara | **7** + escribir el nombre | … «Tomar foto» (2) → disparar (3) → aceptar (4) → nombre (5) → «Generar» (6) → «Girar en 360°» (7) |
| Desvío: falta algo | +0 | «Generar» sin foto o sin nombre → «Falta la foto», «Falta el nombre»; no se pierde lo escrito |
| Desvío: sin red al guardar | +1 | «El modelo se generó, pero no se pudo guardar…» → «Reintentar» guarda sin volver a generar |
| Desvío: foto ilegible | +3 | «No pudimos leer esa foto. Prueba con otra.» → volver a elegir |

| Momento | Hace | Siente | Oportunidad |
|---|---|---|---|
| Tiene la idea | Ve un objeto que le sirve de modelo | Curiosidad | Abrir la app y crear sin pasos previos (no hay cuenta) |
| Toma la foto | Apunta con una mano | Prisa | Consejo en el sitio: «Mejor si está sobre un fondo liso» |
| Espera | Mira la pantalla | Incertidumbre | «Generando el modelo 3D en tu teléfono…» (HU-08) |
| Ve el resultado | Gira el modelo | Satisfacción o decepción | Que el modelo sea **el objeto** y no la foto entera (D1) |

**Acción destacada:** «Crear desde una foto». *Hallazgo D2: hoy es el botón secundario del inicio.*

## 3 · Navegación

```
Inicio ──► Mis Modelos 3D ──► Detalle ──► Vista 360°
   │              │ (vacía: «Crear el primero»)
   └──► Nuevo modelo ──(al crear)──► Detalle del modelo nuevo
```

| Decisión | Por qué |
|---|---|
| **Pila** (lista → detalle → visor), sin barra inferior | Solo hay **dos destinos de primer nivel** (ver y crear). La guía pide barra inferior con 3 a 5 destinos; se agregará cuando llegue la realidad aumentada (entregable 3) |
| La acción principal del inicio va abajo, al alcance del pulgar | Botones al final de la columna |
| Al crear, el formulario se cierra y abre el detalle del modelo nuevo | «Atrás» no vuelve a un formulario ya usado |
| De borde a borde (`enableEdgeToEdge`) | Las pantallas respetan barras de estado y navegación (`Scaffold` / `statusBarsPadding`) |

Hallazgo D3: desde «Mis Modelos 3D» con datos no hay forma de crear un modelo nuevo.

## 4 · Boceto → prototipo

| Estado | ¿Existe? | Cómo se ve |
|---|---|---|
| Cargando | Sí | Indicador; nunca pantalla en blanco (lista, detalle, visor, formulario) |
| Vacía | Sí | «Aún no tienes modelos 3D» + «Crear el primero» |
| Error | Sí | «No pudimos leer tus modelos» + «Reintentar» |
| Sin permiso | No aplica | La app no pide permisos peligrosos: la cámara la abre la app de cámara y la galería, el selector de Android |
| Sin red | Parcial | Se muestra como error («Revisa tu conexión…»), sin un estado propio |

> **Prototipo en Figma (360 × 800): pendiente.** El equipo pasó directo a la app funcional. Es una brecha frente a la
> guía y a la Definición de Terminado de Exploración; le corresponde al líder de UX/UI.

## 5 · Sistema visual

**Paleta medida** (WCAG 2.2 AA: 4,5:1 texto · 3:1 bordes de controles). Cálculo: `scripts/contraste.py`.

| Tema | Par | Texto | Fondo | Contraste | ¿Pasa? |
|---|---|---|---|---|---|
| Claro | Botón principal | `#FFFFFF` | `#4F46E5` | 6,3:1 | Sí |
| Claro | Botón de texto («Volver») | `#4F46E5` | `#FEF7FF` | 6,0:1 | Sí |
| Claro | Texto principal | `#1D1B20` | `#FEF7FF` | 16,2:1 | Sí |
| Claro | Texto secundario | `#49454F` | `#FEF7FF` | 8,9:1 | Sí |
| Claro | Error | `#B3261E` | `#FEF7FF` | 6,2:1 | Sí |
| Claro | Pastilla «Listo» | `#065F46` | `#D1FAE5` | 6,8:1 | Sí |
| Claro | Pastilla «Procesando» | `#92400E` | `#FEF3C7` | 6,4:1 | Sí |
| Claro | Pastilla «Con error» | `#991B1B` | `#FEE2E2` | 6,8:1 | Sí |
| Claro | Pastilla «Borrador» | `#374151` | `#E5E7EB` | 8,3:1 | Sí |
| Claro | Borde del campo de texto | `#79747E` | `#FEF7FF` | 4,3:1 | Sí (mín. 3:1) |
| Oscuro | Botón principal | `#381E72` | `#C7D2FE` | 8,8:1 | Sí |
| Oscuro | Botón de texto | `#C7D2FE` | `#141218` | 12,5:1 | Sí |
| Oscuro | Texto principal | `#E6E0E9` | `#141218` | 14,3:1 | Sí |
| Oscuro | Texto secundario | `#CAC4D0` | `#141218` | 10,9:1 | Sí |
| Oscuro | Pastilla «Listo» | `#065F46` | `#D1FAE5` | 6,8:1 | Sí |

Colores de marca en `ui/theme/Color.kt`; superficies y textos con los tokens de Material 3.

| Decisión | Valor | Dónde |
|---|---|---|
| Marca | Índigo `#4F46E5` · verde azulado `#0F766E` · ámbar `#B45309` (claro); `#C7D2FE` · `#99F6E4` · `#FDE68A` (oscuro) | `ui/theme/Color.kt` |
| Color dinámico de Android 12+ | Desactivado: la app se ve igual en todos los teléfonos | `ui/theme/Theme.kt` |
| Tipografía | Material 3: título 22 sp · cuerpo 16 / 14 sp · etiquetas 12 sp | `ui/theme/Type.kt` |
| Espaciado | Márgenes de 16 dp, separación de 12 a 16 dp | Pantallas |
| Botones | 40 dp visibles con área táctil de 48 dp | Material 3 |
| Temas | Claro y oscuro (capturas `ux/oscuro-*`) | — |

## 6 · Componentes y textos

| Componente | Uso | Archivo |
|---|---|---|
| `Aviso` | Pantalla completa de «vacío», «error» y «no existe»: qué pasó + la acción que lo resuelve | `ui/comun/Componentes.kt` |
| `EtiquetaEstado` | Pastilla de estado con **texto y color** (no solo color) | `ui/comun/Componentes.kt` |
| `Paso` | Tarjeta numerada del inicio | `ui/inicio/InicioScreen.kt` |
| `EstadoFase` | Tarjeta de progreso o error del formulario | `ui/nuevo/NuevoModeloScreen.kt` |
| `Dato` | Fila etiqueta–valor del detalle | `ui/detalle/DetalleModeloScreen.kt` |
| Diálogo de confirmación | Borrar, con lo que se pierde y «No se puede deshacer» | `ui/detalle/DetalleModeloScreen.kt` |
| Botón principal / secundario / de texto | Una acción principal por pantalla | Material 3 |
| Campo con contador | «Te quedan N caracteres» (máx. 80) | `ui/nuevo/NuevoModeloScreen.kt` |
| Visor 3D | SceneView: arrastrar gira, pellizcar acerca | `ui/visor/VisorModeloScreen.kt` |

**Un botón principal por pantalla:** Inicio («Ver mis modelos 3D»*) · Nuevo modelo («Generar modelo 3D») · Detalle
(«Girar en 360°»). *Ver D2.

**Botones: verbo + objeto**

| Texto | ¿Verbo + objeto? |
|---|---|
| «Ver mis modelos 3D» · «Crear desde una foto» · «Tomar foto» · «Generar modelo 3D» · «Girar en 360°» · «Borrar modelo» · «Crear el primero» | Sí |
| «Reintentar» · «Volver» · «Inicio» · «Cancelar» | Verbo de navegación (aceptado) |
| «Galería» | **No** → «Elegir de la galería» (D10) |

**Tabla de términos**

| Término | Se usa así | No se usa |
|---|---|---|
| Modelo 3D | El objeto en 3D del estudiante | «Malla» como nombre, «escena», «proyecto» |
| Generar | Pasar de la foto al modelo | «Procesar», «renderizar» |
| Girar en 360° | Ver el modelo por todos lados | «Visualizar», «orbitar» |
| Listo · Procesando · Con error · Borrador | Los cuatro estados, iguales en la lista y el detalle | «Completado», «Fallido» |
| Polígonos | Tamaño del modelo | (término técnico, ver D9) |

**Mensajes**

| Situación | Texto en la app | Regla de la guía |
|---|---|---|
| Falta un dato | «Falta la foto» · «Falta el nombre» | Dice qué falta en vez de apagar el botón |
| No se lee la lista | «No pudimos leer tus modelos» + «Revisa tu conexión…» + «Reintentar» | Qué pasó + qué hacer |
| Falla la generación | «No pudimos convertir la foto en 3D. Prueba con el objeto sobre un fondo liso.» | Qué pasó + qué hacer |
| Se generó pero no se guardó | «El modelo se generó, pero no se pudo guardar. Revisa tu conexión y reintenta.» | No se pierde el trabajo |

## 7 · Accesibilidad (WCAG 2.2 AA)

| Punto | Resultado | Evidencia |
|---|---|---|
| Contraste 4,5:1 | **Pasa** (15 de 15 pares) | §5 |
| Área táctil 48 dp | **Pasa** por diseño (Material 3 aplica 48 dp mínimos); falta medirlo con Accessibility Scanner | — |
| Letra al 200 % | **Pasa con reparos:** nada se corta, pero en el inicio la acción principal queda fuera de la vista (D5) | `ux/letra200-*` |
| 360 dp de ancho | **Pasa:** sin desbordes en inicio, lista y formulario | `ux/360dp-*` |
| Nombres para TalkBack | Parcial: botones con texto, foto e indicador de carga con descripción; el visor 3D no tiene (D12) | Código |
| No solo color | **Pasa:** cada estado lleva su palabra | `EtiquetaEstado` |
| Encabezados | **No:** los títulos no están marcados como encabezado (D7) | Código |
| Región viva | **No:** el progreso y los errores del formulario no se anuncian (D4) | Código |
| Tarea con ojos cerrados | **Pendiente** | — |
| Prueba automática | **Pendiente** (no hay prueba de accesibilidad en `test/`) | — |

## 8 · Evaluar y mejorar

### Auditoría heurística

Auditoría inicial del equipo. Cada integrante debe auditar por separado y unir los hallazgos con la herramienta de la
guía. Severidad: 1 cosmético · 2 menor · 3 mayor · 4 catástrofe.

| # | Hallazgo | Heurística | Pantalla | Sev. | Qué se hará |
|---|---|---|---|---|---|
| D1 | Con fotos reales (fondo con textura, cosas detrás) el «modelo» sale como **una lámina con la foto entera**, no como el objeto. Evidencia: 4 de los 5 modelos creados por el equipo el 12 de noviembre tienen 40 716 polígonos, el tamaño de la lámina completa | H2 · H9 | Nuevo modelo | **4** | Recortar el objeto con IA en el teléfono (MediaPipe, en curso) y, si no se encuentra, decirlo en vez de crear la lámina |
| D2 | La tarea principal («Crear desde una foto») es el botón secundario; el principal es «Ver mis modelos 3D» | H4 · H8 | Inicio | **3** | Invertir: «Crear desde una foto» como botón principal |
| D3 | En «Mis Modelos 3D» con datos no hay forma de crear un modelo; solo cuando la lista está vacía | H7 | Mis Modelos 3D | **3** | Botón flotante «Nuevo modelo» |
| D4 | «Generando…», «Guardando…» y los errores no se anuncian a TalkBack | ACC | Nuevo modelo | **3** | `liveRegion` en la tarjeta de estado |
| D5 | Con letra al 200 % la acción principal del inicio queda fuera de la vista | ACC | Inicio | 2 | Acciones en una barra inferior fija |
| D6 | No hay pasos ni tiempo estimado mientras se genera | H1 | Nuevo modelo | 2 | «Recortando… · Generando… · Guardando…» |
| D7 | Los títulos de pantalla no están marcados como encabezado | ACC | Todas | 2 | `semantics { heading() }` |
| D8 | «Volver» e «Inicio» son texto, no la flecha estándar de Android | H4 | Todas | 2 | Flecha atrás con descripción |
| D11 | La base tiene modelos de prueba («o», «chongo») que se ven en la lista de la demo | H8 | Mis Modelos 3D | 2 | Que el equipo decida cuáles borrar antes de la Review |
| D12 | El visor 3D no tiene descripción para TalkBack ni otra forma de girar que el gesto | ACC | Vista 360° | 2 | Descripción + botones «Girar ←/→» |
| D9 | «Malla · polígonos» e «Identificador» (un UUID largo) son datos técnicos | H2 | Detalle | 1 | Ocultar el id; explicar «polígonos» |
| D10 | «Galería» no es verbo + objeto | H4 | Nuevo modelo | 1 | «Elegir de la galería» |

**Resumen:** 1 catástrofe · 3 mayores · 6 menores · 2 cosméticos. Según la guía, **los de severidad 3 y 4 (D1–D4)
entran al backlog** del siguiente sprint y se vuelven a medir.

### Prueba con personas y SUS

**Pendiente.** Plan de la guía: **5 personas** (estudiantes, no del equipo), pensando en voz alta, **3 tareas**, y el
cuestionario SUS al final (promedio de referencia: 68).

| Tarea | Éxito si… |
|---|---|
| T1 · Crea un modelo 3D de un objeto que tengas a mano | Llega al detalle del modelo nuevo sin ayuda |
| T2 · Mira la parte de atrás del modelo | Gira el modelo en el visor |
| T3 · Borra un modelo que no te sirvió | Confirma y vuelve a la lista sin ese modelo |

| Participante | P1 | P2 | P3 | P4 | P5 | Promedio |
|---|---|---|---|---|---|---|
| SUS | — | — | — | — | — | — |

## Evidencias

Capturas del emulador en [`capturas/entregable-2/`](capturas/entregable-2/) y [`capturas/entregable-2/ux/`](capturas/entregable-2/ux/):
`360dp-*` (360 dp de ancho), `letra200-*` (letra al 200 % y 360 dp), `oscuro-*` (tema oscuro).
