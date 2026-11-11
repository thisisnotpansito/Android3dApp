package com.example.android3dapp.ui.visor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android3dapp.data.ModeloRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed interface VisorUiState {
    data object Cargando : VisorUiState
    data object Error : VisorUiState
    /** El .glb ya está en el teléfono, listo para el visor. */
    data class Listo(val archivo: File) : VisorUiState
}

/** HU-01 · Baja el .glb del modelo (o lo toma de la caché) para girarlo en 360°. */
class VisorViewModel(
    private val id: String,
    private val repositorio: ModeloRepository,
    private val cache: File,
) : ViewModel() {

    private val _estado = MutableStateFlow<VisorUiState>(VisorUiState.Cargando)
    val estado: StateFlow<VisorUiState> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _estado.value = VisorUiState.Cargando
        viewModelScope.launch {
            _estado.value = try {
                val archivo = File(cache, "modelos/$id.glb")
                if (!archivo.exists()) {
                    val bytes = repositorio.obtenerArchivo3D(id)
                    withContext(Dispatchers.IO) { archivo.parentFile?.mkdirs(); archivo.writeBytes(bytes) }
                }
                VisorUiState.Listo(archivo)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                VisorUiState.Error
            }
        }
    }
}
