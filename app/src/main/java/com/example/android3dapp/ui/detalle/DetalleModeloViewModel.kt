package com.example.android3dapp.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android3dapp.data.ModeloRepository
import com.example.android3dapp.domain.Modelo3D
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data object NoEncontrado : DetalleUiState
    data object Error : DetalleUiState
    data class Listo(val modelo: Modelo3D, val borrando: Boolean = false, val errorAlBorrar: Boolean = false) : DetalleUiState
    /** HU-10 · Se borró: la pantalla vuelve a la lista. */
    data object Borrado : DetalleUiState
}

/** HU-04 · Carga un modelo por su id para la pantalla de detalle. HU-10 · lo borra. */
class DetalleModeloViewModel(
    private val id: String,
    private val repositorio: ModeloRepository,
) : ViewModel() {

    private val _estado = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val estado: StateFlow<DetalleUiState> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _estado.value = DetalleUiState.Cargando
        viewModelScope.launch {
            _estado.value = try {
                repositorio.obtenerPorId(id)?.let { DetalleUiState.Listo(it) } ?: DetalleUiState.NoEncontrado
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                DetalleUiState.Error
            }
        }
    }

    fun borrar() {
        val actual = _estado.value as? DetalleUiState.Listo ?: return
        if (actual.borrando) return // doble toque
        _estado.value = actual.copy(borrando = true, errorAlBorrar = false)
        viewModelScope.launch {
            _estado.value = try {
                repositorio.eliminar(id)
                DetalleUiState.Borrado
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                actual.copy(borrando = false, errorAlBorrar = true)
            }
        }
    }
}
