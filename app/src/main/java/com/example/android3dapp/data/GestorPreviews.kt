package com.example.android3dapp.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Gestiona las miniaturas (previews) reales de cada modelo para mostrarlas en listas 
 * y tarjetas sin penalizar el rendimiento ni instanciar múltiples motores 3D.
 */
class GestorPreviews(context: Context) {
    
    private val dirPreviews = File(context.filesDir, "previews").apply { mkdirs() }

    /**
     * Guarda una miniatura (ej. tomada desde la foto original o del Visor 360°).
     * @param bytes Imagen en formato JPEG o PNG
     */
    suspend fun guardarPreview(id: String, bytes: ByteArray) = withContext(Dispatchers.IO) {
        try {
            val file = File(dirPreviews, "$id.jpg")
            file.writeBytes(bytes)
        } catch (e: Exception) {
            Log.e("GestorPreviews", "Error guardando preview del modelo $id", e)
        }
    }

    /**
     * Guarda una miniatura a partir de un Bitmap ya procesado en memoria.
     */
    suspend fun guardarPreview(id: String, bitmap: Bitmap) = withContext(Dispatchers.IO) {
        try {
            val file = File(dirPreviews, "$id.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
        } catch (e: Exception) {
            Log.e("GestorPreviews", "Error comprimiendo/guardando preview de $id", e)
        }
    }

    /**
     * Obtiene el archivo local de la miniatura si existe.
     */
    fun obtenerArchivo(id: String): File? {
        val file = File(dirPreviews, "$id.jpg")
        return file.takeIf { it.exists() }
    }
    
    /**
     * Elimina la miniatura asociada al modelo
     */
    suspend fun eliminarPreview(id: String) = withContext(Dispatchers.IO) {
        File(dirPreviews, "$id.jpg").delete()
    }
    
    /**
     * Carga asíncronamente el Bitmap de un modelo desde disco.
     */
    suspend fun cargarBitmap(id: String): Bitmap? = withContext(Dispatchers.IO) {
        val file = obtenerArchivo(id) ?: return@withContext null
        BitmapFactory.decodeFile(file.absolutePath)
    }
}
