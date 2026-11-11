package com.example.android3dapp.domain

/**
 * Estado de un modelo 3D. Coincide con el tipo `estado_modelo` de la base de datos
 * (`db/migraciones/001_esquema_inicial.sql`).
 *
 * * BORRADOR: se creó, pero aún no tiene foto.
 * * PROCESANDO: la foto se está convirtiendo en 3D (entregable 2).
 * * LISTO: tiene su archivo 3D y se puede ver en 360° y en realidad aumentada.
 * * ERROR: la generación falló; se puede reintentar.
 */
enum class EstadoModelo { BORRADOR, PROCESANDO, LISTO, ERROR }

/**
 * HU-05 y HU-04 · El modelo que ve el usuario en «Mis Modelos 3D» y en su detalle.
 *
 * Kotlin puro, sin Android: es la capa de dominio. Sus campos salen de la tabla
 * `modelos` (id, nombre, creado_en, estado, poligonos).
 */
data class Modelo3D(
    val id: String,
    val nombre: String,
    val creadoEnMillis: Long,
    val estado: EstadoModelo,
    val poligonos: Int?,
    /** Tiene su archivo .glb (tabla `archivos_modelo`): se puede girar en 360°. */
    val tieneArchivo3D: Boolean = false,
    /** Mensaje del último intento fallido (tabla `trabajos_generacion`). */
    val mensajeError: String? = null,
)

/** HU-07 + HU-02 · Lo que se envía para guardar un modelo recién generado. */
class NuevoModelo(
    val nombre: String,
    val fotoJpeg: ByteArray,
    val anchoPx: Int,
    val altoPx: Int,
    val glb: ByteArray,
    val poligonos: Int,
    val proveedor: String,
)
