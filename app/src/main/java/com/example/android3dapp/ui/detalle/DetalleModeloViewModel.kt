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
    data class Listo(val modelo: Modelo3D) : DetalleUiState
}

/** HU-04 · Carga un modelo por su id para la pantalla de detalle. */
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
}
