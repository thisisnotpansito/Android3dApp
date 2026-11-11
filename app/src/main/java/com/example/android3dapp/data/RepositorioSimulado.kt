package com.example.android3dapp.data

import com.example.android3dapp.domain.EstadoModelo
import com.example.android3dapp.domain.Modelo3D
import com.example.android3dapp.domain.NuevoModelo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID

/** Las cuatro situaciones de toda lista (examen del Sprint 1, paso 7.1). */
enum class Escenario { CON_DATOS, VACIO, FALLA, CARGANDO }

/**
 * INV-04 / TEC-02 · Repositorio sin red. Mezcla tres fuentes:
 *  - los modelos que crea la persona (guardados en el teléfono si hay [almacen]);
 *  - los modelos de demostración con su .glb real ([demos], vienen en el APK);
 *  - seis datos de ejemplo sin archivo, que muestran los estados procesando, error y borrador.
 * El selector «Escenario» (solo en depuración) fuerza las cuatro situaciones de la lista.
 */
class RepositorioSimulado(
    private val esperaMillis: Long = 1_500,
    private val reloj: () -> Long = System::currentTimeMillis,
    private val almacen: AlmacenLocal? = null,
    private val demos: List<Modelo3D> = emptyList(),
) : ModeloRepository {

    var escenario: Escenario = Escenario.CON_DATOS

    private val creados = (almacen?.cargar() ?: emptyList()).toMutableList()
    private val archivos = mutableMapOf<String, ByteArray>()

    private fun todos() = creados + demos + EJEMPLOS

    override suspend fun obtenerTodos(): List<Modelo3D> {
        if (escenario == Escenario.CARGANDO) awaitCancellation() // nunca termina
        delay(esperaMillis)
        return when (escenario) {
            Escenario.FALLA -> throw ErrorDeLectura("Falla simulada al leer los modelos")
            Escenario.VACIO -> emptyList()
            else -> todos()
        }
    }

    override suspend fun obtenerPorId(id: String): Modelo3D? {
        delay(esperaMillis / 3)
        if (escenario == Escenario.FALLA) throw ErrorDeLectura("Falla simulada al leer el modelo $id")
        return todos().firstOrNull { it.id == id }
    }

    override suspend fun crear(nuevo: NuevoModelo): Modelo3D {
        delay(esperaMillis / 3)
        if (escenario == Escenario.FALLA) throw ErrorDeGuardado("Falla simulada al guardar")
        val modelo = Modelo3D(
            id = "local-" + UUID.randomUUID().toString().take(8),
            nombre = nuevo.nombre,
            creadoEnMillis = reloj(),
            estado = EstadoModelo.LISTO,
            poligonos = nuevo.poligonos,
            tieneArchivo3D = true,
        )
        creados.add(0, modelo)
        if (almacen != null) withContext(Dispatchers.IO) {
            almacen.guardarArchivo(modelo.id, nuevo.glb)
            almacen.guardarLista(creados)
        } else archivos[modelo.id] = nuevo.glb
        return modelo
    }

    override suspend fun obtenerArchivo3D(id: String): ByteArray =
        archivos[id]
            ?: almacen?.let { withContext(Dispatchers.IO) { it.leerArchivo(id) } }
            ?: throw ErrorDeLectura("Este modelo no tiene archivo 3D")

    override suspend fun eliminar(id: String) {
        delay(esperaMillis / 3)
        if (!creados.removeAll { it.id == id }) throw ErrorDeGuardado("Los modelos de ejemplo no se pueden borrar")
        archivos.remove(id)
        if (almacen != null) withContext(Dispatchers.IO) {
            almacen.borrarArchivo(id)
            almacen.guardarLista(creados)
        }
    }

    companion object {
        private const val DIA = 86_400_000L
        private const val BASE = 1_793_491_200_000L // 1 nov 2026, 00:00 UTC

        /** Datos de ejemplo sin archivo 3D: muestran cómo se ven los demás estados. */
        val EJEMPLOS: List<Modelo3D> = listOf(
            Modelo3D("m-001", "Taza de cerámica", BASE - 9 * DIA, EstadoModelo.LISTO, 4_820),
            Modelo3D("m-002", "Zapatilla deportiva", BASE - 6 * DIA, EstadoModelo.LISTO, 12_340),
            Modelo3D("m-003", "Lámpara de escritorio", BASE - 4 * DIA, EstadoModelo.LISTO, 7_960),
            Modelo3D("m-004", "Maceta con suculenta", BASE - 2 * DIA, EstadoModelo.PROCESANDO, null),
            Modelo3D("m-005", "Audífonos inalámbricos", BASE - DIA, EstadoModelo.ERROR, null, mensajeError = "La foto está demasiado oscura"),
            Modelo3D("m-006", "Silla minimalista", BASE, EstadoModelo.BORRADOR, null),
        )
    }
}
