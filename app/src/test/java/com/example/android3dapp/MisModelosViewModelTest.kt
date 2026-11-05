package com.example.android3dapp

import com.example.android3dapp.data.ErrorDeLectura
import com.example.android3dapp.data.Escenario
import com.example.android3dapp.data.ModeloRepository
import com.example.android3dapp.data.RepositorioSimulado
import com.example.android3dapp.domain.Modelo3D
import com.example.android3dapp.ui.detalle.DetalleModeloViewModel
import com.example.android3dapp.ui.detalle.DetalleUiState
import com.example.android3dapp.ui.modelos.MisModelosUiState
import com.example.android3dapp.ui.modelos.MisModelosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Los escenarios Gherkin de HU-05 y HU-04 como pruebas automáticas. */
@OptIn(ExperimentalCoroutinesApi::class)
class MisModelosViewModelTest {

    private val despachador = StandardTestDispatcher()

    @Before fun antes() = Dispatchers.setMain(despachador)
    @After fun despues() = Dispatchers.resetMain()

    @Test
    fun `con datos - los modelos se ven del mas reciente al mas antiguo`() = runTest(despachador) {
        val vm = MisModelosViewModel(RepositorioSimulado())
        advanceUntilIdle()
        val estado = vm.estado.value
        assertTrue(estado is MisModelosUiState.ConDatos)
        val fechas = (estado as MisModelosUiState.ConDatos).modelos.map { it.creadoEnMillis }
        assertEquals(fechas.sortedDescending(), fechas)
    }

    @Test
    fun `primera instalacion - sin modelos la lista queda vacia y no en error`() = runTest(despachador) {
        val repo = RepositorioSimulado().apply { escenario = Escenario.VACIO }
        val vm = MisModelosViewModel(repo)
        advanceUntilIdle()
        assertEquals(MisModelosUiState.Vacio, vm.estado.value)
    }

    @Test
    fun `falla al leer - muestra error y reintentar vuelve a cargar`() = runTest(despachador) {
        val repo = RepositorioSimulado().apply { escenario = Escenario.FALLA }
        val vm = MisModelosViewModel(repo)
        advanceUntilIdle()
        assertEquals(MisModelosUiState.Error, vm.estado.value)

        repo.escenario = Escenario.CON_DATOS
        vm.cargar()
        assertEquals(MisModelosUiState.Cargando, vm.estado.value)
        advanceUntilIdle()
        assertTrue(vm.estado.value is MisModelosUiState.ConDatos)
    }

    @Test
    fun `detalle - abre el modelo por su id`() = runTest(despachador) {
        val vm = DetalleModeloViewModel("m-002", RepositorioSimulado())
        advanceUntilIdle()
        val estado = vm.estado.value as DetalleUiState.Listo
        assertEquals("Zapatilla deportiva", estado.modelo.nombre)
    }

    @Test
    fun `detalle - un id que no existe no es un error`() = runTest(despachador) {
        val vm = DetalleModeloViewModel("no-existe", RepositorioSimulado())
        advanceUntilIdle()
        assertEquals(DetalleUiState.NoEncontrado, vm.estado.value)
    }

    @Test
    fun `cualquier implementacion del contrato sirve`() = runTest(despachador) {
        val falso = object : ModeloRepository {
            override suspend fun obtenerTodos(): List<Modelo3D> = throw ErrorDeLectura("sin red")
            override suspend fun obtenerPorId(id: String): Modelo3D? = null
        }
        val vm = MisModelosViewModel(falso)
        advanceUntilIdle()
        assertEquals(MisModelosUiState.Error, vm.estado.value)
    }
}
