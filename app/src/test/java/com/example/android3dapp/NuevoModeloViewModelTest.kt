package com.example.android3dapp

import com.example.android3dapp.data.ErrorDeGuardado
import com.example.android3dapp.data.RepositorioSimulado
import com.example.android3dapp.data.fotos.FotoPreparada
import com.example.android3dapp.data.generacion.ErrorDeGeneracion
import com.example.android3dapp.data.generacion.GeneradorModelo3D
import com.example.android3dapp.data.generacion.ModeloGenerado
import com.example.android3dapp.domain.Modelo3D
import com.example.android3dapp.domain.NuevoModelo
import com.example.android3dapp.ui.detalle.DetalleModeloViewModel
import com.example.android3dapp.ui.detalle.DetalleUiState
import com.example.android3dapp.ui.nuevo.Fase
import com.example.android3dapp.ui.nuevo.NuevoModeloViewModel
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

/** Escenarios de HU-07, HU-02, HU-08, HU-14 y HU-10 como pruebas automáticas. */
@OptIn(ExperimentalCoroutinesApi::class)
class NuevoModeloViewModelTest {

    private val despachador = StandardTestDispatcher()
    @Before fun antes() = Dispatchers.setMain(despachador)
    @After fun despues() = Dispatchers.resetMain()

    private val foto = FotoPreparada(byteArrayOf(1, 2, 3), 1200, 900)
    private val glbFalso = "glTF".toByteArray()

    private class GeneradorFalso(var falla: Boolean = false) : GeneradorModelo3D {
        var llamadas = 0
        override suspend fun generar(fotoJpeg: ByteArray): ModeloGenerado {
            llamadas++
            if (falla) throw ErrorDeGeneracion("foto ilegible")
            return ModeloGenerado("glTF".toByteArray(), 4_200, "falso", true)
        }
    }

    @Test
    fun `con foto y nombre genera, guarda y queda listo`() = runTest(despachador) {
        val repo = RepositorioSimulado(esperaMillis = 0)
        val vm = NuevoModeloViewModel(GeneradorFalso(), repo)
        vm.elegirFoto { foto }; advanceUntilIdle()
        vm.cambiarNombre("Taza azul")
        vm.generar(); advanceUntilIdle()

        val fase = vm.ui.value.fase
        assertTrue(fase is Fase.Listo)
        val creado = repo.obtenerPorId((fase as Fase.Listo).id)!!
        assertEquals("Taza azul", creado.nombre)
        assertEquals(4_200, creado.poligonos)
        assertTrue(creado.tieneArchivo3D)
    }

    @Test
    fun `sin foto dice que falta y no genera`() = runTest(despachador) {
        val gen = GeneradorFalso()
        val vm = NuevoModeloViewModel(gen, RepositorioSimulado(esperaMillis = 0))
        vm.cambiarNombre("Algo")
        vm.generar(); advanceUntilIdle()
        assertEquals("Falta la foto", vm.ui.value.falta)
        assertEquals(0, gen.llamadas)
    }

    @Test
    fun `si la generacion falla muestra un mensaje llano`() = runTest(despachador) {
        val vm = NuevoModeloViewModel(GeneradorFalso(falla = true), RepositorioSimulado(esperaMillis = 0))
        vm.elegirFoto { foto }; advanceUntilIdle()
        vm.cambiarNombre("Taza")
        vm.generar(); advanceUntilIdle()
        assertTrue(vm.ui.value.fase is Fase.Error)
    }

    @Test
    fun `si falla el guardado, reintentar no vuelve a generar`() = runTest(despachador) {
        val gen = GeneradorFalso()
        var fallar = true
        val repo = object : com.example.android3dapp.data.ModeloRepository by RepositorioSimulado(esperaMillis = 0) {
            override suspend fun crear(nuevo: NuevoModelo): Modelo3D =
                if (fallar) throw ErrorDeGuardado("sin red") else Modelo3D("x-1", nuevo.nombre, 0, com.example.android3dapp.domain.EstadoModelo.LISTO, nuevo.poligonos, true)
        }
        val vm = NuevoModeloViewModel(gen, repo)
        vm.elegirFoto { foto }; advanceUntilIdle()
        vm.cambiarNombre("Taza")
        vm.generar(); advanceUntilIdle()
        assertTrue(vm.ui.value.fase is Fase.Error)

        fallar = false
        vm.generar(); advanceUntilIdle()
        assertEquals(Fase.Listo("x-1"), vm.ui.value.fase)
        assertEquals("generó una sola vez", 1, gen.llamadas)
    }

    @Test
    fun `borrar un modelo propio lo saca del repositorio`() = runTest(despachador) {
        val repo = RepositorioSimulado(esperaMillis = 0)
        val creado = repo.crear(NuevoModelo("Lámpara", byteArrayOf(1), 10, 10, glbFalso, 100, "falso"))
        val vm = DetalleModeloViewModel(creado.id, repo); advanceUntilIdle()
        vm.borrar(); advanceUntilIdle()
        assertEquals(DetalleUiState.Borrado, vm.estado.value)
        assertEquals(null, repo.obtenerPorId(creado.id))
    }
}
