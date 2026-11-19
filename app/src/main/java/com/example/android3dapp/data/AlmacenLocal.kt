package com.example.android3dapp.data

import android.content.res.AssetManager
import com.example.android3dapp.domain.EstadoModelo
import com.example.android3dapp.domain.Modelo3D
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Guarda en el teléfono los modelos creados sin API: la lista en `modelos.json` y cada
 * modelo en `modelos/<id>.glb`, dentro de la carpeta privada de la app (nadie más la lee).
 *
 * También siembra los modelos de demostración que vienen dentro del APK
 * (`assets/demos/`), para que la app tenga modelos 3D reales desde la primera vez.
 */
class AlmacenLocal(private val carpeta: File) {

    private val indice = File(carpeta, "modelos.json")
    private val dirModelos = File(carpeta, "modelos").apply { mkdirs() }

    fun cargar(): List<Modelo3D> {
        if (!indice.exists()) return emptyList()
        return runCatching {
            val arr = JSONArray(indice.readText())
            List(arr.length()) { desdeJson(arr.getJSONObject(it)) }.filter { File(dirModelos, "${it.id}.glb").exists() }
        }.getOrDefault(emptyList())
    }

    fun guardarLista(modelos: List<Modelo3D>) {
        val arr = JSONArray()
        modelos.forEach { arr.put(aJson(it)) }
        val tmp = File(carpeta, "modelos.json.tmp")
        tmp.writeText(arr.toString())
        tmp.renameTo(indice) || run { indice.writeText(arr.toString()); tmp.delete() }
    }

    fun guardarArchivo(id: String, glb: ByteArray) = File(dirModelos, "$id.glb").writeBytes(glb)
    fun leerArchivo(id: String): ByteArray? = File(dirModelos, "$id.glb").takeIf { it.exists() }?.readBytes()
    fun borrarArchivo(id: String) { File(dirModelos, "$id.glb").delete() }

    /**
     * Copia los modelos de `assets/demos/` la primera vez. Cada demo es un par
     * `<id>.json` (nombre, polígonos, fecha) + `<id>.glb`.
     */
    fun sembrarDemos(assets: AssetManager): List<Modelo3D> {
        val nombres = runCatching { assets.list("demos")?.toList() }.getOrNull().orEmpty()
        return nombres.filter { it.endsWith(".json") }.mapNotNull { json ->
            runCatching {
                val m = desdeJson(JSONObject(assets.open("demos/$json").bufferedReader().readText()))
                if (!File(dirModelos, "${m.id}.glb").exists()) {
                    assets.open("demos/${m.id}.glb").use { entrada -> File(dirModelos, "${m.id}.glb").outputStream().use { entrada.copyTo(it) } }
                }
                
                // Intentar copiar la imagen para la preview
                val dirPreviews = File(carpeta, "previews").apply { mkdirs() }
                if (!File(dirPreviews, "${m.id}.jpg").exists()) {
                    runCatching {
                        assets.open("demos/${m.id}.jpg").use { entrada -> File(dirPreviews, "${m.id}.jpg").outputStream().use { entrada.copyTo(it) } }
                    }
                }
                m
            }.getOrNull()
        }
    }

    private fun aJson(m: Modelo3D) = JSONObject()
        .put("id", m.id).put("nombre", m.nombre).put("creadoEnMillis", m.creadoEnMillis)
        .put("estado", m.estado.name).put("poligonos", m.poligonos ?: JSONObject.NULL)
        .put("tienePreview", m.tienePreview)

    private fun desdeJson(o: JSONObject) = Modelo3D(
        id = o.getString("id"),
        nombre = o.getString("nombre"),
        creadoEnMillis = o.getLong("creadoEnMillis"),
        estado = EstadoModelo.valueOf(o.optString("estado", "LISTO")),
        poligonos = if (o.isNull("poligonos")) null else o.getInt("poligonos"),
        tieneArchivo3D = true,
        tienePreview = o.optBoolean("tienePreview", false)
    )
}
