package com.example.android3dapp.data.generacion

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale

/**
 * Escribe una [MallaInflada.Malla] y su textura JPEG como un archivo **.glb** (glTF 2.0
 * binario): el formato que leen el visor 360° (SceneView), la realidad aumentada
 * (ARCore) y programas como Blender. Kotlin puro.
 *
 * Estructura: cabecera de 12 bytes · bloque JSON (la descripción) · bloque BIN
 * (posiciones, normales, coordenadas de textura, índices y la foto).
 */
object EscritorGlb {

    private const val MAGIA = 0x46546C67     // «glTF»
    private const val BLOQUE_JSON = 0x4E4F534A
    private const val BLOQUE_BIN = 0x004E4942

    fun escribir(malla: MallaInflada.Malla, texturaJpeg: ByteArray, generador: String): ByteArray {
        val bin = ByteArrayOutputStream()
        fun alinear() { while (bin.size() % 4 != 0) bin.write(0) }
        fun floats(a: FloatArray): IntArray {
            alinear(); val inicio = bin.size()
            val bb = ByteBuffer.allocate(a.size * 4).order(ByteOrder.LITTLE_ENDIAN)
            a.forEach { bb.putFloat(it) }; bin.write(bb.array()); return intArrayOf(inicio, a.size * 4)
        }
        val vPos = floats(malla.posiciones)
        val vNor = floats(malla.normales)
        val vUv = floats(malla.uvs)
        alinear()
        val vInd = intArrayOf(bin.size(), malla.indices.size * 4).also {
            val bb = ByteBuffer.allocate(malla.indices.size * 4).order(ByteOrder.LITTLE_ENDIAN)
            malla.indices.forEach { i -> bb.putInt(i) }; bin.write(bb.array())
        }
        alinear()
        val vImg = intArrayOf(bin.size(), texturaJpeg.size).also { bin.write(texturaJpeg) }
        alinear()
        val datos = bin.toByteArray()

        val (mn, mx) = MallaInflada.minMax(malla.posiciones)
        fun f(x: Float) = String.format(Locale.ROOT, "%.6f", x)
        fun vista(v: IntArray, destino: Int?) =
            """{"buffer":0,"byteOffset":${v[0]},"byteLength":${v[1]}${destino?.let { ""","target":$it""" } ?: ""}}"""
        val nv = malla.vertices
        val json = """
            {"asset":{"version":"2.0","generator":"${generador.replace("\"", "'")}"},
             "scene":0,"scenes":[{"nodes":[0]}],
             "nodes":[{"mesh":0,"name":"modelo"}],
             "meshes":[{"primitives":[{"attributes":{"POSITION":0,"NORMAL":1,"TEXCOORD_0":2},"indices":3,"material":0}]}],
             "materials":[{"name":"foto","doubleSided":true,
               "pbrMetallicRoughness":{"baseColorTexture":{"index":0},"metallicFactor":0.0,"roughnessFactor":0.85}}],
             "textures":[{"source":0,"sampler":0}],
             "samplers":[{"magFilter":9729,"minFilter":9729,"wrapS":33071,"wrapT":33071}],
             "images":[{"bufferView":4,"mimeType":"image/jpeg"}],
             "accessors":[
               {"bufferView":0,"componentType":5126,"count":$nv,"type":"VEC3",
                "min":[${f(mn[0])},${f(mn[1])},${f(mn[2])}],"max":[${f(mx[0])},${f(mx[1])},${f(mx[2])}]},
               {"bufferView":1,"componentType":5126,"count":$nv,"type":"VEC3"},
               {"bufferView":2,"componentType":5126,"count":$nv,"type":"VEC2"},
               {"bufferView":3,"componentType":5125,"count":${malla.indices.size},"type":"SCALAR"}],
             "bufferViews":[${vista(vPos, 34962)},${vista(vNor, 34962)},${vista(vUv, 34962)},${vista(vInd, 34963)},${vista(vImg, null)}],
             "buffers":[{"byteLength":${datos.size}}]}
        """.trimIndent().replace("\n", "")

        var jsonBytes = json.toByteArray(Charsets.UTF_8)
        if (jsonBytes.size % 4 != 0) jsonBytes += ByteArray(4 - jsonBytes.size % 4) { ' '.code.toByte() }

        val total = 12 + 8 + jsonBytes.size + 8 + datos.size
        return ByteBuffer.allocate(total).order(ByteOrder.LITTLE_ENDIAN).apply {
            putInt(MAGIA); putInt(2); putInt(total)
            putInt(jsonBytes.size); putInt(BLOQUE_JSON); put(jsonBytes)
            putInt(datos.size); putInt(BLOQUE_BIN); put(datos)
        }.array()
    }
}
