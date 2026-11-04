# Definición del Flujo de Trabajo y Definición de Terminado

> Sprint 0 · Paso 6 de la guía-taller. Marco del curso: Scrumban con fases de Mobile-D; sprints de una semana, de
> Sesión B a Sesión B. Equipo de cuatro integrantes.

## Columnas del tablero y políticas de entrada

Cada política responde a una pregunta: **¿qué tiene que cumplir una tarjeta para entrar en esta columna?**

| Columna | Política de entrada | WIP |
|---|---|---|
| **Backlog** | Cualquier idea con título claro. El PO decide su orden. | — |
| **Listo** | Cumple INVEST, tiene criterios de aceptación y puntos, y el equipo la entiende. | 5 |
| **En desarrollo** | Alguien se la asigna y crea su rama (`hu-05-mis-modelos`). Solo una tarjeta por persona. | 4 |
| **En revisión** | Hay un PR abierto que enlaza el issue y el lint (`./gradlew lintDebug`) no reporta errores. | 2 |
| **En dispositivo** | PR aprobado por otra persona; APK de la rama instalado en un Android físico. | 2 |
| **Hecho** | Cumple la Definición de Terminado y el PR está fusionado. | — |

**Elementos de investigación (INV):** esperan en «Backlog» y pasan a «En desarrollo» cuando alguien los toma. **No pasan
por «En dispositivo»**: de «En revisión», donde otra persona lee el resultado, van a «Hecho».

## El resto de la definición

- **Elemento de trabajo:** un issue del repositorio (historia, tarea técnica, defecto o investigación) con su ID en el
  título: `HU-05: …`, `TEC-03: …`, `INV-02: …`.
- **Empieza** cuando entra en «En desarrollo»; **termina** cuando entra en «Hecho». Entre esos dos momentos se mide el
  *cycle time*.
- **Expectativa de nivel de servicio (provisional):** «la mayoría de nuestros elementos se terminan en 4 días o menos».
- **Bloqueos:** una tarjeta bloqueada lleva la etiqueta `bloqueada` y se menciona en el siguiente Daily. Si sigue así dos
  días, el Scrum Master interviene.
- **Ramas:** `master` protegida; una rama por elemento (`hu-…`, `tec-…`, `inv-…`) o por entregable cuando varios
  elementos dependen entre sí (`entregable-1/fase-inicial`). Todo entra por pull request con una revisión de otra
  persona. El PR dice `Closes #N` para que el issue se cierre al fusionar.
- **Commits:** empiezan por el ID del elemento: `TEC-02: …`, `HU-05: …`.

## Ceremonias

| Evento | Cuándo | Duración |
|---|---|---|
| Sprint Review | Inicio de cada Sesión B | 15 min |
| Retrospectiva | Después de la Review | 10 min |
| Sprint Planning | Después de la Retrospectiva | 20 min |
| Daily | Inicio de cada sesión; asíncrono lunes, miércoles y viernes | 10 min |

## Definición de Terminado de la fase de Exploración

Certifica que una historia está **lista para construirse**. Va como tres casillas dentro de cada issue:

- [ ] La historia tiene criterios de aceptación verificables en formato Dado / Cuando / Entonces, revisados por otra persona.
- [ ] Su pantalla existe en el prototipo de Figma, en baja fidelidad como mínimo.
- [ ] El PO la validó con al menos un usuario real y anotó qué dijo.

## Definición de Terminado base (desde la semana 6)

Una historia está **Hecha** cuando:

- [ ] Cumple todos sus criterios de aceptación, comprobados en un **Android físico** (el Xiaomi del equipo).
- [ ] Entró por un PR revisado por otra persona y fusionado en `master`.
- [ ] `./gradlew lintDebug` no reporta errores y `./gradlew testDebugUnitTest` pasa.
- [ ] Se ve bien en una pantalla de **360 dp** de ancho, sin desbordamientos.
- [ ] Toda lista modela sus **cuatro estados**: cargando, vacío, error y con datos.
- [ ] Si usa un permiso (cámara, almacenamiento), maneja el caso de **permiso denegado**.
- [ ] Toda área táctil mide al menos **48 dp** y el texto tiene un contraste de 4,5:1 o más.
- [ ] Ninguna credencial (base de datos, API) queda en el código ni en el APK.
