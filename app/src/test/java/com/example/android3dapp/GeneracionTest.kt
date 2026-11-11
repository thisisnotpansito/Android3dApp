package com.example.android3dapp

import com.example.android3dapp.data.generacion.EscritorGlb
import com.example.android3dapp.data.generacion.MallaInflada
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** HU-02 · El generador en el teléfono: silueta, malla cerrada y archivo .glb válido. */
class GeneracionTest {

    private val blanco = 0xFFFFFFFF.toInt()
    private val rojo = 0xFFC0392B.toInt()

    /** Un círculo rojo sobre fondo blanco, como una foto de un objeto sobre una mesa. */
    private fun circulo(w: Int = 60, h: Int = 60): IntArray = IntArray(w * h) { i ->
        val x = i % w - w / 2f; val y = i / w - h / 2f
        if (x * x + y * y < (w * 0.3f) * (w * 0.3f)) rojo else blanco
    }

    @Test
    fun `separa el objeto del fondo liso`() {
        val malla = MallaInflada.construir(circulo(), 60, 60)
        assertTrue(malla.usoSilueta)
        assertTrue("debe tener triángulos", malla.triangulos > 1_000)
    }

    @Test
    fun `la malla es un volumen con frente y espalda`() {
        val malla = MallaInflada.construir(circulo(), 60, 60)
        val z = (0 until malla.vertices).map { malla.posiciones[it * 3 + 2] }
        assertTrue("hay puntos adelante", z.any { it > 0.05f })
        assertTrue("hay puntos atrás", z.any { it < -0.05f })
        // Los mismos triángulos adelante y atrás: número par.
        assertEquals(0, malla.triangulos % 2)
        // Todos los índices apuntan a vértices que existen.
        assertTrue(malla.indices.all { it in 0 until malla.vertices })
    }

    @Test
    fun `sin un objeto claro usa la foto entera como lamina`() {
        val ruido = IntArray(40 * 40) { if ((it * 7919) % 3 == 0) rojo else blanco }
        val malla = MallaInflada.construir(ruido, 40, 40)
        assertFalse(malla.usoSilueta)
        assertTrue(malla.triangulos > 0)
    }

    @Test
    fun `el glb tiene cabecera, json y binario correctos`() {
        val malla = MallaInflada.construir(circulo(), 60, 60)
        val jpegFalso = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 1, 2, 3, 0xFF.toByte(), 0xD9.toByte())
        val glb = EscritorGlb.escribir(malla, jpegFalso, "prueba")
        val bb = ByteBuffer.wrap(glb).order(ByteOrder.LITTLE_ENDIAN)

        assertEquals("magia glTF", 0x46546C67, bb.getInt(0))
        assertEquals("versión 2", 2, bb.getInt(4))
        assertEquals("largo total", glb.size, bb.getInt(8))
        val largoJson = bb.getInt(12)
        assertEquals("bloque JSON", 0x4E4F534A, bb.getInt(16))
        assertEquals("alineado a 4", 0, largoJson % 4)
        val json = String(glb, 20, largoJson)
        assertTrue(json.contains("\"POSITION\":0"))
        assertTrue(json.contains("\"count\":${malla.indices.size}"))
        assertTrue(json.contains("image/jpeg"))
        assertEquals("bloque BIN", 0x004E4942, bb.getInt(20 + largoJson + 4))
    }
}
