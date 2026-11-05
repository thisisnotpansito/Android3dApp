# Changelog

## [Entregable 1 · Fase inicial] · 2026-11-04

### Añadido
- **TEC-02:** arquitectura MVVM. Contrato `ModeloRepository`, `MisModelosViewModel` y `DetalleModeloViewModel` con
  `StateFlow`, y `AppContenedor` como único punto que elige la implementación.
- **HU-05:** «Mis Modelos 3D» con sus cuatro estados, orden del más reciente al más antiguo y etiqueta de estado.
- **HU-04:** detalle de un modelo por id (`modelos/{id}`), con «Este modelo ya no existe».
- **HU-03:** pantalla de inicio con los tres pasos del producto.
- Formulario «Nuevo modelo» (solo interfaz; la generación llega en el entregable 2).
- **TEC-03:** `db/migraciones/001_esquema_inicial.sql` y `db/semillas/001_datos_ejemplo.sql`, aplicados en Neon.
- 6 pruebas unitarias de los ViewModel (`MisModelosViewModelTest`).
- Documentación en `docs/`, criterios Gherkin, plantillas de issue y PR, y `scripts/` para el backlog y el tablero.

### Cambiado
- El nombre visible de la app pasa a «Creative 3D Builder».
- Tema con paleta propia (índigo, verde azulado, ámbar) y sin color dinámico.
- `MisModelosScreen.kt` y `RepositorioSimulado.kt` pasan a `ui/modelos/` y `data/`.
- La dependencia de SceneView se declara en el catálogo de versiones.

## [Sprint 1 · examen] · 2026-10-15

### Añadido
- **INV-04:** pantalla «Mis Modelos 3D» con cuatro estados y repositorio simulado (PR #13).
