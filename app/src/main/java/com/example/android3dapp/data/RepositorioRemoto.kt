package com.example.android3dapp.data

import com.example.android3dapp.domain.EstadoModelo
import com.example.android3dapp.domain.Modelo3D
import com.example.android3dapp.domain.NuevoModelo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

/**
 * TEC-05 · El contrato cumplido por la API en Cloudflare Workers (carpeta `api/`).
 *
 * La app solo conoce la URL de la API; las credenciales de Neon viven como secreto del
 * Worker. Usa HttpURLConnection y org.json (vienen con Android): ninguna dependencia nueva.
 */
class RepositorioRemoto(private val base: String) : ModeloRepository {

    override suspend fun obtenerTodos(): List<Modelo3D> = leer {
        val arr = JSONArray(String(pedir("GET", "/modelos").cuerpo))
        List(arr.length()) { desdeJson(arr.getJSONObject(it)) }
    }

    override suspend fun obtenerPorId(id: String): Modelo3D? = leer {
        val r = pedir("GET", "/modelos/$id", aceptar404 = true)
        if (r.codigo == 404) null else desdeJson(JSONObject(String(r.cuerpo)))
    }

    override suspend fun crear(nuevo: NuevoModelo): Modelo3D = withContext(Dispatchers.IO) {
        try {
            val limite = "----c3db" + UUID.randomUUID().toString().replace("-", "")
            val cuerpo = ByteArrayOutputStream().apply {
                fun campo(nombre: String, valor: String) =
                    write("--$limite\r\nContent-Disposition: form-data; name=\"$nombre\"\r\n\r\n$valor\r\n".toByteArray())
                fun archivo(nombre: String, archivo: String, tipo: String, datos: ByteArray) {
                    write("--$limite\r\nContent-Disposition: form-data; name=\"$nombre\"; filename=\"$archivo\"\r\nContent-Type: $tipo\r\n\r\n".toByteArray())
                    write(datos); write("\r\n".toByteArray())
                }
                campo("nombre", nuevo.nombre)
                campo("poligonos", nuevo.poligonos.toString())
                campo("proveedor", nuevo.proveedor)
                campo("ancho_px", nuevo.anchoPx.toString())
                campo("alto_px", nuevo.altoPx.toString())
                archivo("foto", "foto.jpg", "image/jpeg", nuevo.fotoJpeg)
                archivo("modelo", "modelo.glb", "model/gltf-binary", nuevo.glb)
                write("--$limite--\r\n".toByteArray())
            }.toByteArray()
            val r = pedir("POST", "/modelos", cuerpo, "multipart/form-data; boundary=$limite")
            desdeJson(JSONObject(String(r.cuerpo)))
        } catch (e: Exception) {
            throw ErrorDeGuardado("No se pudo guardar el modelo", e)
        }
    }

    override suspend fun obtenerArchivo3D(id: String): ByteArray = leer { pedir("GET", "/modelos/$id/modelo.glb").cuerpo }

    override suspend fun eliminar(id: String) = withContext(Dispatchers.IO) {
        try { pedir("DELETE", "/modelos/$id"); Unit } catch (e: Exception) { throw ErrorDeGuardado("No se pudo borrar el modelo", e) }
    }

    // ------------------------------------------------------------------

    private class Respuesta(val codigo: Int, val cuerpo: ByteArray)

    private suspend fun <T> leer(bloque: () -> T): T = withContext(Dispatchers.IO) {
        try { bloque() } catch (e: Exception) { throw ErrorDeLectura("No se pudo leer de la API", e) }
    }

    private fun pedir(
        metodo: String,
        ruta: String,
        cuerpo: ByteArray? = null,
        tipo: String? = null,
        aceptar404: Boolean = false,
    ): Respuesta {
        val c = URL(base.trimEnd('/') + ruta).openConnection() as HttpURLConnection
        try {
            c.requestMethod = metodo
            c.connectTimeout = 15_000
            c.readTimeout = 30_000
            if (cuerpo != null) {
                c.doOutput = true
                c.setRequestProperty("Content-Type", tipo)
                c.setFixedLengthStreamingMode(cuerpo.size)
                c.outputStream.use { it.write(cuerpo) }
            }
            val codigo = c.responseCode
            val flujo = if (codigo in 200..299) c.inputStream else c.errorStream
            val datos = flujo?.use { it.readBytes() } ?: ByteArray(0)
            if (codigo == 404 && aceptar404) return Respuesta(codigo, datos)
            if (codigo !in 200..299) throw IOException("HTTP $codigo: ${String(datos).take(200)}")
            return Respuesta(codigo, datos)
        } finally {
            c.disconnect()
        }
    }

    private fun desdeJson(o: JSONObject) = Modelo3D(
        id = o.getString("id"),
        nombre = o.getString("nombre"),
        creadoEnMillis = FECHA.get()!!.parse(o.getString("creado_en"))!!.time,
        estado = EstadoModelo.valueOf(o.getString("estado").uppercase(Locale.ROOT)),
        poligonos = if (o.isNull("poligonos")) null else o.getInt("poligonos"),
        tieneArchivo3D = o.optBoolean("tiene_modelo", false),
        mensajeError = if (o.isNull("mensaje_error")) null else o.optString("mensaje_error"),
    )

    private companion object {
        // ThreadLocal.withInitial necesita API 26; minSdk es 24.
        val FECHA = object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue() =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT).apply { timeZone = TimeZone.getTimeZone("UTC") }
        }
    }
}
