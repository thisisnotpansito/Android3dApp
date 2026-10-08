# Creative 3D Builder

## Visión del Producto
Proveer a creadores y diseñadores una herramienta móvil intuitiva para construir y manipular geometría 3D en tiempo real desde la palma de su mano, sin necesidad de hardware especializado.

## Objetivo del Producto
Lanzar una aplicación nativa en Android (MVP) que permita a los usuarios generar primitivas 3D, seleccionarlas mediante raycasting y exportar sus creaciones.

## Roles del Equipo
* **Product Owner (PO):** [Nombre del estudiante]
* **Scrum Master (SM):** [Nombre del estudiante]
* **Equipo de Desarrollo:** [Nombres de los estudiantes]
* **Rotación:** [Explicar brevemente cada cuántos sprints rotan los roles, ej. "Los roles de PO y SM rotarán cada 2 Sprints"].

## Tabla MoSCoW
| Must Have (Debe tener) | Should Have (Debería tener) | Could Have (Podría tener) | Won't Have (No tendrá ahora) |
| :--- | :--- | :--- | :--- |
| Lienzo 3D funcional | Gizmos de control (X, Y, Z) | Modo oscuro | Multijugador en tiempo real |
| Generación de formas base | Selector de colores | Guardado en la nube | Importación de modelos externos |
| Raycasting (Selección) | Exportación a .stl/.gltf | Historial de deshacer (Undo) | Animaciones complejas |

## Fuera del Alcance
* Renderizado hiperrealista con trazado de rayos (Raytracing).
* Aplicación para iOS en esta fase.
* Monetización o pasarelas de pago.

## Definición del Flujo de Trabajo
1. Seleccionar issue del Sprint Backlog.
2. Mover a columna "En Progreso".
3. Crear rama (ej. `feature/raycasting`).
4. Desarrollar y realizar commits con convenciones semánticas.
5. Mover a "Revisión" y crear Pull Request.
6. Aprobación y merge a `main`. Mover a "Hecho".

## Definition of Done (DoD) de Exploración
* El código compila sin errores en Android Studio.
* La funcionalidad cumple con los criterios de aceptación de la historia.
* El código sigue las buenas prácticas de Kotlin y Jetpack Compose.
* La interfaz visual (UI) no se superpone con el lienzo 3D.
