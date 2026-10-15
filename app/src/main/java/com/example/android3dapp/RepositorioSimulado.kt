package com.example.android3dapp

import kotlinx.coroutines.delay

// Las 4 situaciones obligatorias del examen
enum class Escenario { LISTO, VACIO, ERROR, CARGANDO }

// El modelo de datos de tu app
data class Modelo3D(val id: String, val nombre: String, val fecha: String, val poligonos: Int)

class RepositorioSimulado {
    var escenarioActual = Escenario.LISTO

    private val misModelos = listOf(
        Modelo3D("1", "Silla Minimalista", "10 Oct 2026", 120),
        Modelo3D("2", "Engranaje Motor", "11 Oct 2026", 450),
        Modelo3D("3", "Boceto de Taza", "12 Oct 2026", 80),
        Modelo3D("4", "Cubo Base", "13 Oct 2026", 12),
        Modelo3D("5", "Estructura Arquitectónica", "14 Oct 2026", 1200)
    )

    suspend fun obtenerModelos(): List<Modelo3D> {
        if (escenarioActual == Escenario.CARGANDO) {
            delay(Long.MAX_VALUE) // Simula carga infinita
        }

        delay(1500) // Obligatorio: 1.5s de espera según la rúbrica

        return when (escenarioActual) {
            Escenario.ERROR -> throw Exception("Falla simulada al leer memoria")
            Escenario.VACIO -> emptyList()
            Escenario.LISTO -> misModelos
            else -> emptyList()
        }
    }
}