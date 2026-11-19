package com.example.android3dapp.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.android3dapp.domain.Modelo3D
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object ExportadorGlb {

    /** 
     * Escribe el modelo `.glb` a un OutputStream (ej. el de Storage Access Framework). 
     * Retorna true si tuvo éxito.
     */
    suspend fun exportarAStream(
        context: Context,
        modelo: Modelo3D,
        repositorio: ModeloRepository,
        outStream: OutputStream
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val bytes = repositorio.obtenerArchivo3D(modelo.id)
            outStream.use { it.write(bytes) }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Prepara el archivo `.glb` en caché y crea un Intent de compartir (ACTION_SEND).
     */
    suspend fun crearIntentCompartir(
        context: Context,
        modelo: Modelo3D,
        repositorio: ModeloRepository
    ): Intent? = withContext(Dispatchers.IO) {
        try {
            val bytes = repositorio.obtenerArchivo3D(modelo.id)
            val exportDir = File(context.cacheDir, "exportados").apply { mkdirs() }
            val nombreLimpio = modelo.nombre.replace(Regex("[^a-zA-Z0-9.-]"), "_") + ".glb"
            val file = File(exportDir, nombreLimpio)
            file.writeBytes(bytes)

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.archivos", file)
            Intent(Intent.ACTION_SEND).apply {
                type = "model/gltf-binary"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
