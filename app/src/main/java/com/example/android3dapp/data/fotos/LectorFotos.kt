package com.example.android3dapp.data.fotos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

/** Una foto lista para generar y subir: derecha, reducida y en JPEG. */
class FotoPreparada(val jpeg: ByteArray, val ancho: Int, val alto: Int)

/** No se pudo leer la foto elegida. */
class ErrorDeFoto(mensaje: String) : Exception(mensaje)

/**
 * HU-07 · Lee la foto que llega de la cámara o de la galería: la endereza según su
 * EXIF, la reduce a 1600 px como máximo y la comprime a JPEG. Así pesa poco al subirla
 * (el celular de la persona tiene poco espacio y datos limitados).
 */
class LectorFotos(private val context: Context) {

    suspend fun leer(uri: Uri, ladoMax: Int = 1600): FotoPreparada = withContext(Dispatchers.IO) {
        val cr = context.contentResolver
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        // Con inJustDecodeBounds, decodeStream devuelve null a propósito: solo llena las medidas.
        val flujo = cr.openInputStream(uri) ?: throw ErrorDeFoto("No se pudo abrir la foto")
        flujo.use { BitmapFactory.decodeStream(it, null, limites) }
        if (limites.outWidth <= 0) throw ErrorDeFoto("El archivo no es una imagen")

        var muestra = 1
        while (max(limites.outWidth, limites.outHeight) / (muestra * 2) >= ladoMax) muestra *= 2
        val bitmap = cr.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = muestra })
        } ?: throw ErrorDeFoto("No se pudo leer la foto")

        val grados = cr.openInputStream(uri)?.use {
            when (ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f

        var foto: Bitmap = bitmap
        if (grados != 0f) foto = Bitmap.createBitmap(foto, 0, 0, foto.width, foto.height, Matrix().apply { postRotate(grados) }, true)
        val f = ladoMax.toFloat() / max(foto.width, foto.height)
        if (f < 1f) foto = Bitmap.createScaledBitmap(foto, (foto.width * f).roundToInt(), (foto.height * f).roundToInt(), true)

        val jpeg = ByteArrayOutputStream().also { foto.compress(Bitmap.CompressFormat.JPEG, 85, it) }.toByteArray()
        FotoPreparada(jpeg, foto.width, foto.height)
    }
}
