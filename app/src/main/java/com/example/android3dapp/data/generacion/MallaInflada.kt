package com.example.android3dapp.data.generacion

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * HU-02 · Convierte la foto de un objeto en una malla 3D cerrada, sin internet.
 *
 * Método «inflado de silueta» (como un globo con la forma del objeto):
 *  1. Separa el objeto del fondo: el fondo es lo que se parece al color de los bordes
 *     y está conectado con ellos (relleno por inundación).
 *  2. Calcula, para cada punto del objeto, su distancia al borde de la silueta.
 *  3. Lo «infla»: el grosor crece con esa distancia en un perfil circular, adelante y
 *     atrás. El borde queda en grosor cero, así la malla se cierra sola.
 *  4. La cara de adelante y la de atrás usan la foto como textura.
 *
 * Es una aproximación: no reconstruye la parte oculta del objeto. Un servicio de IA de
 * imagen a 3D (INV-05) daría más detalle; este generador no cuesta nada y funciona en
 * cualquier teléfono. Kotlin puro (sin Android) para poder probarlo en el computador.
 */
object MallaInflada {

    /** Malla lista para escribir en glTF. Coordenadas centradas; el lado mayor mide 1. */
    class Malla(
        val posiciones: FloatArray,
        val normales: FloatArray,
        val uvs: FloatArray,
        val indices: IntArray,
        /** false si no se encontró un objeto y se usó la foto entera como una lámina. */
        val usoSilueta: Boolean,
    ) {
        val vertices: Int get() = posiciones.size / 3
        val triangulos: Int get() = indices.size / 3
    }

    /** Grosor máximo como fracción del lado mayor de la foto. */
    private const val GROSOR = 0.16f

    /**
     * @param argb píxeles en formato ARGB (como `Bitmap.getPixels`).
     * @param grosorFondo 1f = inflado equilibrado, <1f sutil, >1f exagerado.
     */
    fun construir(argb: IntArray, ancho: Int, alto: Int, grosor: Float = 1.0f): Malla {
        require(ancho >= 8 && alto >= 8 && argb.size == ancho * alto) { "Imagen demasiado pequeña" }
        var mascara = separarObjeto(argb, ancho, alto)
        val area = mascara.count { it }
        val usoSilueta = area >= ancho * alto * 0.03 && area <= ancho * alto * 0.97
        if (!usoSilueta) mascara = laminaConMargen(ancho, alto)
        val distancia = distanciaAlBorde(mascara, ancho, alto)
        return mallar(mascara, distancia, ancho, alto, usoSilueta, grosor)
    }

    // ------------------------------------------------------------------ 1. silueta

    internal fun separarObjeto(argb: IntArray, w: Int, h: Int): BooleanArray {
        // Color medio del borde de la foto = color del fondo.
        var r = 0L; var g = 0L; var b = 0L; var n = 0
        fun suma(i: Int) { val c = argb[i]; r += (c shr 16) and 255; g += (c shr 8) and 255; b += c and 255; n++ }
        for (x in 0 until w) { suma(x); suma((h - 1) * w + x) }
        for (y in 1 until h - 1) { suma(y * w); suma(y * w + w - 1) }
        val fr = (r / n).toInt(); val fg = (g / n).toInt(); val fb = (b / n).toInt()

        // Umbral: un poco por encima de cuánto varía el propio borde (sombras, ruido).
        var desv = 0.0
        fun dist(c: Int) = abs(((c shr 16) and 255) - fr) + abs(((c shr 8) and 255) - fg) + abs((c and 255) - fb)
        for (x in 0 until w) { desv += dist(argb[x]) + dist(argb[(h - 1) * w + x]) }
        for (y in 1 until h - 1) { desv += dist(argb[y * w]) + dist(argb[y * w + w - 1]) }
        val umbral = max(45.0, desv / n * 2.5)

        // Inundación desde el borde: todo lo alcanzable y parecido al fondo es fondo.
        val fondo = BooleanArray(w * h)
        val pila = IntArray(w * h); var tope = 0
        fun empujar(i: Int) { if (!fondo[i] && dist(argb[i]) < umbral) { fondo[i] = true; pila[tope++] = i } }
        for (x in 0 until w) { empujar(x); empujar((h - 1) * w + x) }
        for (y in 0 until h) { empujar(y * w); empujar(y * w + w - 1) }
        while (tope > 0) {
            val i = pila[--tope]; val x = i % w; val y = i / w
            if (x > 0) empujar(i - 1); if (x < w - 1) empujar(i + 1)
            if (y > 0) empujar(i - w); if (y < h - 1) empujar(i + w)
        }
        // Huecos encerrados (el asa de una taza): zonas grandes del color del fondo que la
        // inundación no alcanzó porque el objeto las rodea. Las pequeñas (brillos) se quedan.
        quitarHuecos(argb, fondo, w, h, umbral * 0.8, ::dist)
        return mayorComponente(BooleanArray(w * h) { !fondo[it] }, w, h)
    }

    private fun quitarHuecos(argb: IntArray, fondo: BooleanArray, w: Int, h: Int, umbral: Double, dist: (Int) -> Int) {
        val visto = BooleanArray(w * h)
        val pila = IntArray(w * h)
        val zona = IntArray(w * h)
        val minimo = (w * h * 0.004).toInt().coerceAtLeast(6)
        for (inicio in fondo.indices) {
            if (fondo[inicio] || visto[inicio] || dist(argb[inicio]) >= umbral) continue
            var tope = 0; var tam = 0
            pila[tope++] = inicio; visto[inicio] = true
            while (tope > 0) {
                val i = pila[--tope]; zona[tam++] = i; val x = i % w; val y = i / w
                for (v in intArrayOf(if (x > 0) i - 1 else -1, if (x < w - 1) i + 1 else -1, if (y > 0) i - w else -1, if (y < h - 1) i + w else -1)) {
                    if (v >= 0 && !visto[v] && !fondo[v] && dist(argb[v]) < umbral) { visto[v] = true; pila[tope++] = v }
                }
            }
            if (tam >= minimo) for (k in 0 until tam) fondo[zona[k]] = true
        }
    }

    /** Se queda con la mancha más grande (descarta motas sueltas). */
    private fun mayorComponente(m: BooleanArray, w: Int, h: Int): BooleanArray {
        val etiqueta = IntArray(w * h)
        val pila = IntArray(w * h)
        var mejor = 0; var mejorTam = 0; var siguiente = 0
        for (inicio in m.indices) {
            if (!m[inicio] || etiqueta[inicio] != 0) continue
            siguiente++; var tam = 0; var tope = 0
            pila[tope++] = inicio; etiqueta[inicio] = siguiente
            while (tope > 0) {
                val i = pila[--tope]; tam++; val x = i % w; val y = i / w
                for (v in intArrayOf(if (x > 0) i - 1 else -1, if (x < w - 1) i + 1 else -1, if (y > 0) i - w else -1, if (y < h - 1) i + w else -1)) {
                    if (v >= 0 && m[v] && etiqueta[v] == 0) { etiqueta[v] = siguiente; pila[tope++] = v }
                }
            }
            if (tam > mejorTam) { mejorTam = tam; mejor = siguiente }
        }
        return BooleanArray(w * h) { etiqueta[it] == mejor && mejor != 0 }
    }

    /** Sin objeto claro (fondo cargado o foto muy cerrada): una lámina con la foto entera. */
    private fun laminaConMargen(w: Int, h: Int): BooleanArray =
        BooleanArray(w * h) { val x = it % w; val y = it / w; x in 1 until w - 1 && y in 1 until h - 1 }

    // ------------------------------------------------------------------ 2. distancia

    /** Distancia de chaflán (3-4) de cada punto del objeto al fondo, en píxeles. */
    internal fun distanciaAlBorde(m: BooleanArray, w: Int, h: Int): FloatArray {
        val inf = 1_000_000
        val d = IntArray(w * h) { if (m[it]) inf else 0 }
        fun v(x: Int, y: Int) = if (x < 0 || y < 0 || x >= w || y >= h) 0 else d[y * w + x]
        for (y in 0 until h) for (x in 0 until w) {
            val i = y * w + x; if (d[i] == 0) continue
            d[i] = minOf(d[i], v(x - 1, y) + 3, v(x, y - 1) + 3, v(x - 1, y - 1) + 4, v(x + 1, y - 1) + 4)
        }
        for (y in h - 1 downTo 0) for (x in w - 1 downTo 0) {
            val i = y * w + x; if (d[i] == 0) continue
            d[i] = minOf(d[i], v(x + 1, y) + 3, v(x, y + 1) + 3, v(x + 1, y + 1) + 4, v(x - 1, y + 1) + 4)
        }
        return FloatArray(w * h) { d[it] / 3f }
    }

    // ------------------------------------------------------------------ 3. malla

    private fun mallar(m: BooleanArray, dist: FloatArray, w: Int, h: Int, usoSilueta: Boolean, factorGrosor: Float): Malla {
        val dmax = max(1f, dist.max())
        val escala = max(w, h).toFloat()
        // Ajustamos la curva base (GROSOR) y la centramos exactamente en Z=0
        val grosorBase = GROSOR * escala * factorGrosor
        val cx = (w - 1) / 2f; val cy = (h - 1) / 2f

        // Un vértice adelante y otro atrás por cada píxel del objeto.
        val idx = IntArray(w * h) { -1 }
        var n = 0
        for (i in m.indices) if (m[i]) idx[i] = n++
        val pos = FloatArray(n * 2 * 3); val uv = FloatArray(n * 2 * 2)
        for (i in m.indices) {
            val k = idx[i]; if (k < 0) continue
            val x = i % w; val y = i / w
            val borde = esBorde(m, x, y, w, h)
            val t = (dist[i] / dmax).coerceIn(0f, 1f)
            
            // Perfil de inflado orgánico gaussiano/elíptico
            val relieve = if (borde) 0f else grosorBase * (1f - (1f - t) * (1f - t) * (1f - t))
            val px = (x - cx) / escala; val py = -(y - cy) / escala; val pz = relieve / escala
            
            for (lado in 0..1) {
                val v = k + lado * n
                pos[v * 3] = px
                pos[v * 3 + 1] = py
                pos[v * 3 + 2] = if (lado == 0) pz else -pz
                // UVs normalizadas y alineadas a glTF (sin estiramientos raros)
                uv[v * 2] = (x + 0.5f) / w
                uv[v * 2 + 1] = 1f - (y + 0.5f) / h
            }
        }

        val tris = ArrayList<Int>(n * 12)
        fun tri(a: Int, b: Int, c: Int) {
            tris += a; tris += b; tris += c            // adelante (mira a +Z)
            tris += a + n; tris += c + n; tris += b + n // atrás (orden inverso)
        }
        for (y in 0 until h - 1) for (x in 0 until w - 1) {
            val a = idx[y * w + x]; val b = idx[y * w + x + 1]
            val c = idx[(y + 1) * w + x]; val d = idx[(y + 1) * w + x + 1]
            val dentro = (if (a >= 0) 1 else 0) + (if (b >= 0) 1 else 0) + (if (c >= 0) 1 else 0) + (if (d >= 0) 1 else 0)
            when {
                dentro == 4 -> { tri(a, c, b); tri(b, c, d) }
                dentro == 3 && d < 0 -> tri(a, c, b)
                dentro == 3 && a < 0 -> tri(b, c, d)
                dentro == 3 && b < 0 -> tri(a, c, d)
                dentro == 3 && c < 0 -> tri(a, d, b)
            }
        }
        val indices = tris.toIntArray()
        return Malla(pos, normales(pos, indices), uv, indices, usoSilueta)
    }

    private fun esBorde(m: BooleanArray, x: Int, y: Int, w: Int, h: Int): Boolean =
        x == 0 || y == 0 || x == w - 1 || y == h - 1 ||
            !m[y * w + x - 1] || !m[y * w + x + 1] || !m[(y - 1) * w + x] || !m[(y + 1) * w + x]

    /** Normales suaves: promedio de las normales de las caras que tocan cada vértice. */
    private fun normales(p: FloatArray, ind: IntArray): FloatArray {
        val nor = FloatArray(p.size)
        for (t in ind.indices step 3) {
            val a = ind[t] * 3; val b = ind[t + 1] * 3; val c = ind[t + 2] * 3
            val ux = p[b] - p[a]; val uy = p[b + 1] - p[a + 1]; val uz = p[b + 2] - p[a + 2]
            val vx = p[c] - p[a]; val vy = p[c + 1] - p[a + 1]; val vz = p[c + 2] - p[a + 2]
            val nx = uy * vz - uz * vy; val ny = uz * vx - ux * vz; val nz = ux * vy - uy * vx
            for (v in intArrayOf(a, b, c)) { nor[v] += nx; nor[v + 1] += ny; nor[v + 2] += nz }
        }
        for (v in nor.indices step 3) {
            val l = sqrt(nor[v] * nor[v] + nor[v + 1] * nor[v + 1] + nor[v + 2] * nor[v + 2])
            if (l > 1e-12f) { nor[v] /= l; nor[v + 1] /= l; nor[v + 2] /= l }
            else { nor[v] = 0f; nor[v + 1] = 0f; nor[v + 2] = if (p[v + 2] >= 0f) 1f else -1f }
        }
        return nor
    }

    internal fun minMax(p: FloatArray): Pair<FloatArray, FloatArray> {
        val mn = floatArrayOf(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE)
        val mx = floatArrayOf(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)
        for (i in p.indices) { val e = i % 3; mn[e] = min(mn[e], p[i]); mx[e] = max(mx[e], p[i]) }
        return mn to mx
    }
}
