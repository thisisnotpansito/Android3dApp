package com.example.android3dapp.data

import com.example.android3dapp.domain.EstadoModelo
import com.example.android3dapp.domain.Modelo3D
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay

/** Las cuatro situaciones de toda lista (examen del Sprint 1, paso 7.1). */
enum class Escenario { CON_DATOS, VACIO, FALLA, CARGANDO }

/**
 * INV-04 / TEC-02 · Datos de prueba creíbles, con 1,5 s de espera, para revisar
 * los cuatro estados sin base de datos. El escenario se cambia desde el selector
 * que solo aparece en las compilaciones de depuración.
 */
class RepositorioSimulado(
    private val esperaMillis: Long = 1_500,
) : ModeloRepository {

    var escenario: Escenario = Escenario.CON_DATOS

    override suspend fun obtenerTodos(): List<Modelo3D> {
        if (escenario == Escenario.CARGANDO) awaitCancellation() // nunca termina
        delay(esperaMillis)
        return when (escenario) {
            Escenario.FALLA -> throw ErrorDeLectura("Falla simulada al leer los modelos")
            Escenario.VACIO -> emptyList()
            else -> EJEMPLOS
        }
    }

    override suspend fun obtenerPorId(id: String): Modelo3D? {
        delay(esperaMillis / 3)
        if (escenario == Escenario.FALLA) throw ErrorDeLectura("Falla simulada al leer el modelo $id")
        return EJEMPLOS.firstOrNull { it.id == id }
    }

    companion object {
        private const val DIA = 86_400_000L
        private const val BASE = 1_793_491_200_000L // 1 nov 2026, 00:00 UTC

        /** Objetos que un estudiante fotografiaría para pasarlos a 3D. */
        val EJEMPLOS: List<Modelo3D> = listOf(
            Modelo3D("m-001", "Taza de cerámica", BASE - 9 * DIA, EstadoModelo.LISTO, 4_820),
            Modelo3D("m-002", "Zapatilla deportiva", BASE - 6 * DIA, EstadoModelo.LISTO, 12_340),
            Modelo3D("m-003", "Lámpara de escritorio", BASE - 4 * DIA, EstadoModelo.LISTO, 7_960),
            Modelo3D("m-004", "Maceta con suculenta", BASE - 2 * DIA, EstadoModelo.PROCESANDO, null),
            Modelo3D("m-005", "Audífonos inalámbricos", BASE - DIA, EstadoModelo.ERROR, null),
            Modelo3D("m-006", "Silla minimalista", BASE, EstadoModelo.BORRADOR, null),
        )
    }
}
