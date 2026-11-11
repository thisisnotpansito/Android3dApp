package com.example.android3dapp.ui.modelos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android3dapp.data.Escenario
import com.example.android3dapp.data.ModeloRepository
import com.example.android3dapp.data.RepositorioSimulado
import com.example.android3dapp.domain.Modelo3D
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Las cuatro situaciones de «Mis Modelos 3D». La pantalla hace un `when` sobre ellas. */
sealed interface MisModelosUiState {
    data object Cargando : MisModelosUiState
    data object Vacio : MisModelosUiState
    data object Error : MisModelosUiState
    data class ConDatos(val modelos: List<Modelo3D>) : MisModelosUiState
}

/**
 * HU-05 · Decide en qué situación está la lista. La pantalla solo lee [estado] y
 * llama a [cargar]; nunca habla con el repositorio.
 *
 * Aquí se decide que la lista está **vacía** (se leyó bien y no hay nada) y no en
 * **error** (no se pudo leer).
 */
class MisModelosViewModel(
    private val repositorio: ModeloRepository,
) : ViewModel() {

    private val _estado = MutableStateFlow<MisModelosUiState>(MisModelosUiState.Cargando)
    val estado: StateFlow<MisModelosUiState> = _estado.asStateFlow()

    /** El selector de escenarios solo existe si los datos son simulados. */
    val admiteEscenarios: Boolean get() = repositorio is RepositorioSimulado

    private var carga: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        carga?.cancel()
        _estado.value = MisModelosUiState.Cargando
        carga = viewModelScope.launch {
            _estado.value = try {
                val modelos = repositorio.obtenerTodos().sortedByDescending { it.creadoEnMillis }
                if (modelos.isEmpty()) MisModelosUiState.Vacio else MisModelosUiState.ConDatos(modelos)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                MisModelosUiState.Error
            }
        }
    }

    /**
     * Al volver a la lista (después de crear o borrar un modelo) se recarga sin mostrar
     * «cargando» si ya había datos: la lista no parpadea.
     */
    fun refrescar() {
        if (carga?.isActive == true) return
        val anterior = _estado.value as? MisModelosUiState.ConDatos ?: return cargar()
        carga = viewModelScope.launch {
            _estado.value = try {
                val modelos = repositorio.obtenerTodos().sortedByDescending { it.creadoEnMillis }
                if (modelos.isEmpty()) MisModelosUiState.Vacio else MisModelosUiState.ConDatos(modelos)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                anterior
            }
        }
    }

    fun elegirEscenario(escenario: Escenario) {
        (repositorio as? RepositorioSimulado)?.escenario = escenario
        cargar()
    }
}
