# Changelog

## [Entregable 2 · Foto a modelo 3D] · 2026-11-11 · versión 2.0

### Añadido
- **HU-07:** «Tomar foto» (app de cámara, vía FileProvider) y «Galería» (selector de fotos de Android), sin permisos de
  cámara ni de almacenamiento. La foto se endereza según su EXIF, se reduce a 1600 px y pasa a JPEG.
- **HU-02:** generador 3D en el teléfono: `MallaInflada` (silueta → distancia al borde → inflado) y `EscritorGlb`
  (glTF 2.0 binario con la foto como textura). Detrás del contrato `GeneradorModelo3D`.
- **HU-08 / HU-14:** el formulario muestra «Generando…» y «Guardando…», dice qué falta y permite reintentar sin volver
  a generar.
- **HU-01:** visor 360° con SceneView: arrastrar para girar y pellizcar para acercar.
- **HU-10:** «Borrar modelo» en el detalle, con confirmación.
- Persistencia en el teléfono (`AlmacenLocal`) y dos modelos de demostración en `assets/demos/` (taza y cactus).
- **TEC-05:** API en Cloudflare Workers (`api/`) con Neon y Workers KV, publicada en
  `https://creative-3d-builder-api.creative3dbuilder.workers.dev`; la app la usa (`creative3d.apiUrl`).
- **INV-05:** `docs/inv-05-servicio-imagen-3d.md`.
- 9 pruebas nuevas (16 en total).

### Cambiado
- El contrato `ModeloRepository` agrega `crear`, `obtenerArchivo3D` y `eliminar`.
- `Modelo3D` agrega `tieneArchivo3D` y `mensajeError`.
- La lista se refresca al volver a ella.
- Permiso `INTERNET` en el manifiesto.

### Corregido
- La foto elegida se leía como «no se pudo abrir» porque `decodeStream` devuelve `null` a propósito al leer solo las
  medidas.

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
