# Examen · Sprint 1 · Android Nativo (Kotlin + Compose)

- **Equipo y proyecto:** Los Chontos - Creative 3D Builder
- **Objetivo (sí o no):** Sí. La lista de "Mis Modelos 3D" funciona en un celular Android con sus cuatro situaciones simuladas y sus criterios están escritos.
- **Historia técnica:** HU-05 (Ver lista de Mis Modelos 3D) · **Otra Must:** HU-01 (Renderizar lienzo 3D base).

## 1. Persona y mapa de hoy
- **Contexto:** Estudiante universitario de artes plásticas o ingeniería, 22 años. 
- **Teléfono y conexión:** Xiaomi Android gama media, conexión inestable cuando viaja en el bus.
- **Quiere lograr:** Bocetar ideas geométricas espaciales (3D) rápidamente sin depender de un computador de escritorio.
- **La frena:** Los motores 3D móviles actuales son extremadamente pesados, complejos y agotan la batería.
- **Una frase suya:** "Se me ocurren geometrías en el bus pero no tengo dónde bocetarlas en 3D".

| | Momento 1 | Momento 2 | Momento 3 | Momento 4 |
|---|---|---|---|---|
| **Hace** | Tiene una idea espacial y la anota en una libreta 2D. | Intenta usar una app móvil de modelado tradicional. | Llega a casa a pasarlo a Blender en su PC. | Quiere compartir su idea con un compañero. |
| **Se frustra** | Pierde la proporción y la perspectiva 3D real de la idea. | La interfaz está saturada de botones y es lenta. | Olvidó detalles estructurales del boceto original. | Tomar capturas de pantalla no permite rotar el modelo. |
| **Historia** | HU-01 (Lienzo 3D) | HU-02 (Generar figuras UI) | HU-05 (Mis Modelos 3D) | HU-09 (Exportar a STL) |

**Cambio en el backlog:** Al analizar el momento 2, nos dimos cuenta de que el usuario necesita entender de inmediato cómo empezar. Nació la regla de "Estado Vacío" en la lista de modelos para ofrecer un botón directo de "Crear el primero".

## 2. Criterios (tres por historia: éxito, error, celular)
```gherkin
# language: es
Escenario: El usuario visualiza sus modelos guardados (Éxito)
  Dado que el usuario tiene modelos 3D creados previamente
  Cuando abre la pantalla "Mis Modelos 3D"
  Entonces ve la lista ordenada con los nombres y la fecha de creación

Escenario: Falla de permisos de almacenamiento (Error)
  Dado que el motor no puede acceder a la memoria del dispositivo
  Cuando la aplicación intenta leer los archivos locales
  Entonces muestra un aviso "No pudimos leer tus proyectos" y un botón "Reintentar"

Escenario: Primera instalación en el dispositivo (Celular - Vacío)
  Dado que el usuario acaba de instalar la aplicación
  Cuando entra a su lista de modelos por primera vez
  Entonces ve un mensaje "Aún no tienes modelos 3D" y un botón principal "Crear el primero"
