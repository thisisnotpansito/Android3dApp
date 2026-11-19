# Kit UX/UI de Creative 3D Builder

> Entregable 2 · Guía 31 «UX y UI en tu app» y su roadmap de ocho paradas
> (<https://ux-ui-appmovil-ecoreport.netlify.app/>). Versión de la app: **2.0** (PR #40). Medido el 18 de noviembre de
> 2026 en un emulador Pixel 6 con Android 16. La prueba con personas no se alcanzó a hacer: la sección del SUS usa
> **datos de ejemplo** (marcados así) para mostrar el método y el cálculo, no resultados reales.

## Resumen

| Parada | Pieza del kit | Estado | Dónde |
|---|---|---|---|
| 1 · Persona y escena | Ficha de persona + escena | Hecho: 3 de 3 entrevistas · 4 supuestos corregidos | §1 |
| 2 · Recorrido y tarea | Recorrido + flujo con toques | Hecho | §2 · [`actores-eventos-flujo.md`](actores-eventos-flujo.md) |
| 3 · Navegación | Árbol de pantallas justificado | Hecho | §3 |
| 4 · Boceto → prototipo | Prototipo navegable en Figma 360 × 800 | Hecho: 6 bocetos en grises + 10 pantallas conectadas | §4 |
| 5 · Sistema visual | Paleta medida + tema en el código | Hecho · 15 de 15 pares pasan | §5 |
| 6 · Componentes y textos | Inventario de componentes + términos | Hecho | §6 |
| 7 · Accesibilidad | Lista aprobada + prueba automática | Parcial: 5 de 10 puntos pasan; sin prueba automática | §7 |
| 8 · Evaluar y mejorar | Auditoría + SUS + plan de mejora | Auditoría hecha (12 hallazgos); SUS con **datos de ejemplo** (prueba real pendiente) | §8 |

---

## 1 · Persona y escena

### Entrevistas (3 de 3)

Tres estudiantes de UNIPAZ, de tres carreras distintas. Entrevistas cortas, con preguntas sobre hechos pasados («la
última vez que…»), hechas por Erick Martinez. No se grabaron: lo que sigue es la **síntesis de las notas del
entrevistador**, no citas textuales.

| | Víctor Gómez | Nicole Rodríguez | Edwin Jaimes |
|---|---|---|---|
| Carrera y semestre | Ingeniería Informática, 6.º | Obras Civiles, 8.º | Medicina Veterinaria, 8.º |
| Edad | 19–20 años | ~22 años | ~20 años |
| Teléfono | Xiaomi Redmi (serie 10, aprox.) | No se registró | No se registró |
| Última vez que quiso ver algo en 3D | Por curiosidad: ver cómo se veía un objeto desde varios lados | Para un trabajo de la universidad: le pidieron renderizar elementos de un proyecto | Por curiosidad: convertir en 3D a un perro con una sola foto |
| Qué hizo | Tomó varias fotos desde distintos lados y buscó una herramienta | Tomó fotos de todo y las subió a una herramienta para que se lo modelara | Tomó una sola foto y buscó en internet dónde subirla |
| Dónde se trabó | No encontró cómo hacerlo | Estaba estresada: tenía poco tiempo para entregar | No encontró una herramienta completa para hacerlo |
| Dónde estaba y cuánto tiempo tenía | En su casa, con tiempo libre | En su casa, contra el reloj de una entrega | En la universidad, sin límite de tiempo |
| Conexión | Wifi de la casa | Wifi de la casa | Wifi de la universidad |

### Supuestos → confirmados

| Supuesto (Sprint 1) | Resultado | Evidencia |
|---|---|---|
| El usuario es estudiante de **ingeniería informática** | **Corregido:** cualquier estudiante universitario que necesite mostrar un objeto en 3D (también civiles y veterinaria) | 3 de 3, de carreras distintas |
| Quiere ver un objeto en 3D sin saber modelar | **Confirmado** | 3 de 3 buscaron una herramienta; ninguno modeló a mano |
| Lo frena la complejidad de los programas 3D | **Confirmado y precisado:** lo que frena es **no encontrar una herramienta** que lo haga de punta a punta | Víctor y Edwin |
| Parte de una sola foto | **Parcial:** 1 de 3 usó una sola foto; 2 de 3 tomaron varias desde distintos lados | Edwin (una) · Víctor y Nicole (varias) |
| Usa datos móviles inestables en el bus | **No confirmado:** los 3 estaban con wifi, en casa o en la universidad | 3 de 3 |
| Teléfono Xiaomi de gama media | Confirmado en 1 de 3 | Víctor |
| Tiene poco tiempo | **Depende del uso:** con prisa cuando es para una entrega; sin prisa cuando es por curiosidad | Nicole vs. Víctor y Edwin |

### Ficha de persona (actualizada)

| Campo | Contenido |
|---|---|
| Persona | Estudiante universitario de 19 a 22 años, de cualquier carrera, que necesita mostrar o ver un objeto en 3D |
| Dos usos | **Curiosidad** (sin prisa) y **trabajo académico** (con fecha de entrega) |
| Teléfono y conexión | Android, con frecuencia Xiaomi; usa la app con wifi, en casa o en la universidad |
| Quiere lograr | Ver el objeto desde todos los ángulos o mostrárselo a alguien, sin aprender a modelar |
| La frena hoy | No encontrar **una sola herramienta** que haga todo; con fecha de entrega, el estrés y el poco tiempo |

**Escena difícil y frecuente:** en casa, la noche antes de una entrega. El objeto está sobre un escritorio con cosas
alrededor; hay que fotografiarlo y tener el modelo listo en pocos minutos para el trabajo.

### Cambios en el backlog que salen de las entrevistas

| Cambio | Por qué |
|---|---|
| El Objetivo del Producto dice «un estudiante universitario», no «de ingeniería» | Los 3 entrevistados eran de carreras distintas |
| **HU-15 · Crear el modelo con varias fotos del objeto** (candidata, Should) | 2 de 3 tomaron varias fotos desde distintos lados |
| La prioridad de D1 se confirma: la foto tiene fondo real (escritorio, animal en el piso) | Ninguno fotografió sobre un fondo liso |
| Exportar o compartir el modelo (HU-09, HU-12) gana importancia | Nicole lo necesitaba para entregar un trabajo |

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

### Prototipo en Figma (360 × 800)

Archivo **«Creative 3D Builder · Prototipo»**:
<https://www.figma.com/design/QzG2urW4hacLPMkCXOt5l2/Creative-3D-Builder-%C2%B7-Prototipo> · modo presentación:
<https://www.figma.com/proto/QzG2urW4hacLPMkCXOt5l2/Creative-3D-Builder-%C2%B7-Prototipo?node-id=1-16&starting-point-node-id=1%3A16&scaling=scale-down>

| Sección del lienzo | Marcos | Qué muestra |
|---|---|---|
| 1 · Bocetos en grises | 6 | Inicio, Mis modelos, Detalle, Nuevo modelo, Vista 360° y el estado **sin permiso** (solo en boceto: la app no pide permisos) |
| 2 · Prototipo navegable | 8 | Inicio → cargando → lista → detalle → vista 360°, e Inicio → nuevo → foto elegida → generando → detalle |
| 3 · Estados de error y vacío | 2 | Lista vacía («Crear el primero») y sin red («Reintentar») |

Dos flujos de inicio: **«Prototipo: crear y ver un modelo»** y **«Bocetos en grises»**. Cada botón tiene una zona de
toque que navega a su pantalla. «Cargando» y «Generando» pasan solos a la siguiente (1,2 s y 2,5 s), como en la app.
Las pantallas finales son capturas del emulador a 360 dp. Exportaciones en
[`capturas/entregable-2/figma/`](capturas/entregable-2/figma/).

> Nota de proceso: el prototipo se armó **después** de la app y no antes, como pide la guía. Sirve como documentación
> y para la prueba con personas, no como validación previa del diseño.

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
| D11 | La base tiene modelos de prueba («o», «chongo») que se ven en la lista de la demo | H8 | Mis Modelos 3D | 2 | **Decidido (18-nov):** se conservan como evidencia de las pruebas del equipo |
| D12 | El visor 3D no tiene descripción para TalkBack ni otra forma de girar que el gesto | ACC | Vista 360° | 2 | Descripción + botones «Girar ←/→» |
| D9 | «Malla · polígonos» e «Identificador» (un UUID largo) son datos técnicos | H2 | Detalle | 1 | Ocultar el id; explicar «polígonos» |
| D10 | «Galería» no es verbo + objeto | H4 | Nuevo modelo | 1 | «Elegir de la galería» |

**Resumen:** 1 catástrofe · 3 mayores · 6 menores · 2 cosméticos. Según la guía, **los de severidad 3 y 4 (D1–D4)
entran al backlog** del siguiente sprint y se vuelven a medir.

### Prueba con personas y SUS

Plan de la guía: **5 personas** (estudiantes, no del equipo), pensando en voz alta, **3 tareas**, y el cuestionario SUS
al final (promedio de referencia: 68).

| Tarea | Éxito si… |
|---|---|
| T1 · Crea un modelo 3D de un objeto que tengas a mano | Llega al detalle del modelo nuevo sin ayuda |
| T2 · Mira la parte de atrás del modelo | Gira el modelo en el visor |
| T3 · Borra un modelo que no te sirvió | Confirma y vuelve a la lista sin ese modelo |

> ⚠️ **DATOS DE EJEMPLO. La prueba con personas no se realizó.** Los participantes E1–E5 son ficticios y las
> respuestas se escribieron para mostrar cómo se registra y se calcula la prueba. **No son resultados del producto y
> no se usan para tomar decisiones.** Cuando se haga la prueba real, se reemplaza esta tabla.

**Respuestas SUS de ejemplo** (1 = muy en desacuerdo, 5 = muy de acuerdo; ítems impares en positivo, pares en negativo):

| Participante (ficticio) | Q1 | Q2 | Q3 | Q4 | Q5 | Q6 | Q7 | Q8 | Q9 | Q10 | Puntaje SUS |
|---|---|---|---|---|---|---|---|---|---|---|---|
| E1 | 4 | 2 | 4 | 1 | 4 | 2 | 4 | 2 | 4 | 2 | 77,5 |
| E2 | 3 | 2 | 4 | 2 | 3 | 3 | 4 | 2 | 3 | 1 | 67,5 |
| E3 | 4 | 1 | 5 | 1 | 4 | 2 | 5 | 1 | 4 | 1 | 90,0 |
| E4 | 3 | 3 | 3 | 2 | 3 | 3 | 4 | 2 | 2 | 2 | 57,5 |
| E5 | 4 | 2 | 4 | 1 | 4 | 3 | 4 | 2 | 3 | 2 | 72,5 |
| **Promedio** | | | | | | | | | | | **73,0** |

**Cálculo (Brooke):** a cada ítem impar se le resta 1; a cada ítem par se le resta su valor a 5. Se suman los 10
resultados y se multiplica por 2,5. Ejemplo con E1: impares (3+3+3+3+3) + pares (3+4+3+3+3) = 31; 31 × 2,5 = **77,5**. Un promedio
sobre 68 se lee como «por encima del promedio».

**Registro de tareas de ejemplo** (mismo aviso: ficticio):

| Tarea | Lo lograron sin ayuda | Lo que se esperaría observar (según la auditoría) |
|---|---|---|
| T1 · Crear | 4 de 5 | Dudar entre los dos botones de Inicio (D2); el resultado plano decepciona (D1) |
| T2 · Ver la parte de atrás | 5 de 5 | El gesto de girar se entiende por la pista del visor |
| T3 · Borrar | 4 de 5 | «Borrar modelo» queda abajo y se encuentra tarde |

**Para hacer la prueba real:** usar el prototipo de Figma o la app en el Xiaomi, 5 estudiantes de otras carreras
(como Víctor, Nicole y Edwin), unos 10 minutos por persona, y el formulario SUS en español. Responsable: líder de UX/UI.

## Evidencias

Capturas del emulador en [`capturas/entregable-2/`](capturas/entregable-2/) y [`capturas/entregable-2/ux/`](capturas/entregable-2/ux/):
`360dp-*` (360 dp de ancho), `letra200-*` (letra al 200 % y 360 dp), `oscuro-*` (tema oscuro).
