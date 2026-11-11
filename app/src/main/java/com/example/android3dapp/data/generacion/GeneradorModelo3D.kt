package com.example.android3dapp.data.generacion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

/** Resultado de convertir una foto en 3D. */
class ModeloGenerado(
    val glb: ByteArray,
    val poligonos: Int,
    /** Quién lo generó; va a `trabajos_generacion.proveedor`. */
    val proveedor: String,
    /** false si no se reconoció un objeto y salió una lámina con la foto. */
    val usoSilueta: Boolean,
)

/** No se pudo generar el modelo (foto ilegible, servicio caído…). */
class ErrorDeGeneracion(mensaje: String, causa: Throwable? = null) : Exception(mensaje, causa)

/**
 * HU-02 · Contrato del generador de imagen a 3D. Hoy lo cumple [GeneradorInflado], en el
 * teléfono; un servicio de IA (INV-05) sería otra implementación sin tocar las pantallas.
 */
interface GeneradorModelo3D {
    /** @param fotoJpeg la foto ya preparada (orientada y reducida). */
    suspend fun generar(fotoJpeg: ByteArray): ModeloGenerado
}

/** Implementación en el teléfono: silueta inflada ([MallaInflada]) escrita como .glb. */
class GeneradorInflado(
    private val resolucionMalla: Int = 120,
    private val resolucionTextura: Int = 1024,
) : GeneradorModelo3D {

    override suspend fun generar(fotoJpeg: ByteArray): ModeloGenerado = withContext(Dispatchers.Default) {
        val foto = BitmapFactory.decodeByteArray(fotoJpeg, 0, fotoJpeg.size)
            ?: throw ErrorDeGeneracion("La foto no se pudo leer")
        try {
            val chica = reducir(foto, resolucionMalla)
            val pix = IntArray(chica.width * chica.height)
            chica.getPixels(pix, 0, chica.width, 0, 0, chica.width, chica.height)
            val malla = MallaInflada.construir(pix, chica.width, chica.height)
            if (malla.triangulos == 0) throw ErrorDeGeneracion("No se encontró ningún objeto en la foto")

            val textura = ByteArrayOutputStream().also {
                reducir(foto, resolucionTextura).compress(Bitmap.CompressFormat.JPEG, 85, it)
            }.toByteArray()
            val glb = EscritorGlb.escribir(malla, textura, "Creative 3D Builder · inflado de silueta")
            ModeloGenerado(glb, malla.triangulos, PROVEEDOR, malla.usoSilueta)
        } catch (e: ErrorDeGeneracion) {
            throw e
        } catch (e: Exception) {
            throw ErrorDeGeneracion("No se pudo generar el modelo 3D", e)
        }
    }

    private fun reducir(b: Bitmap, lado: Int): Bitmap {
        val f = lado.toFloat() / max(b.width, b.height)
        if (f >= 1f) return b
        return Bitmap.createScaledBitmap(b, (b.width * f).roundToInt().coerceAtLeast(1), (b.height * f).roundToInt().coerceAtLeast(1), true)
    }

    companion object {
        const val PROVEEDOR = "local-inflado"
    }
}
