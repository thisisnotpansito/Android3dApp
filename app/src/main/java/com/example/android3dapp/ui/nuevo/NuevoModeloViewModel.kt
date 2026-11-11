package com.example.android3dapp.ui.nuevo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android3dapp.data.ModeloRepository
import com.example.android3dapp.data.fotos.FotoPreparada
import com.example.android3dapp.data.generacion.GeneradorModelo3D
import com.example.android3dapp.data.generacion.ModeloGenerado
import com.example.android3dapp.domain.NuevoModelo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** HU-08 · En qué va la creación del modelo. */
sealed interface Fase {
    data object Editando : Fase
    data object LeyendoFoto : Fase
    data object Generando : Fase
    data object Guardando : Fase
    /** HU-14 · Algo falló: el mensaje dice qué y se puede reintentar sin empezar de nuevo. */
    data class Error(val mensaje: String) : Fase
    data class Listo(val id: String) : Fase
}

data class NuevoModeloUi(
    val nombre: String = "",
    val foto: FotoPreparada? = null,
    val fase: Fase = Fase.Editando,
    /** Qué falta para poder generar (nombre o foto); null si no falta nada. */
    val falta: String? = null,
) {
    val ocupado: Boolean get() = fase is Fase.LeyendoFoto || fase is Fase.Generando || fase is Fase.Guardando
}

/**
 * HU-07 + HU-02 + HU-08 + HU-14 · Formulario «Nuevo modelo»: elegir la foto, generar el
 * 3D y guardarlo. Decide el estado; la pantalla solo lo muestra.
 */
class NuevoModeloViewModel(
    private val generador: GeneradorModelo3D,
    private val repositorio: ModeloRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(NuevoModeloUi())
    val ui: StateFlow<NuevoModeloUi> = _ui.asStateFlow()

    /** Si ya se generó y solo falló el guardado, reintentar no vuelve a generar. */
    private var generado: ModeloGenerado? = null

    fun cambiarNombre(nombre: String) {
        if (nombre.length <= MAX_NOMBRE) _ui.update { it.copy(nombre = nombre, falta = null) }
    }

    /** HU-07 · La pantalla entrega cómo leer la foto (cámara o galería). */
    fun elegirFoto(leer: suspend () -> FotoPreparada) {
        if (_ui.value.ocupado) return
        _ui.update { it.copy(fase = Fase.LeyendoFoto, falta = null) }
        viewModelScope.launch {
            try {
                val foto = leer()
                generado = null
                _ui.update { it.copy(foto = foto, fase = Fase.Editando) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _ui.update { it.copy(fase = Fase.Error("No pudimos leer esa foto. Prueba con otra.")) }
            }
        }
    }

    fun generar() {
        val actual = _ui.value
        if (actual.ocupado) return // doble toque
        val foto = actual.foto
        val nombre = actual.nombre.trim()
        val falta = when {
            foto == null && nombre.isEmpty() -> "Falta la foto y el nombre"
            foto == null -> "Falta la foto"
            nombre.isEmpty() -> "Falta el nombre"
            else -> null
        }
        if (falta != null || foto == null) { _ui.update { it.copy(falta = falta) }; return }

        viewModelScope.launch {
            val modelo = generado ?: run {
                _ui.update { it.copy(fase = Fase.Generando) }
                try {
                    generador.generar(foto.jpeg).also { generado = it }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _ui.update { it.copy(fase = Fase.Error("No pudimos convertir la foto en 3D. Prueba con el objeto sobre un fondo liso.")) }
                    return@launch
                }
            }
            _ui.update { it.copy(fase = Fase.Guardando) }
            try {
                val creado = repositorio.crear(
                    NuevoModelo(nombre, foto.jpeg, foto.ancho, foto.alto, modelo.glb, modelo.poligonos, modelo.proveedor),
                )
                _ui.update { it.copy(fase = Fase.Listo(creado.id)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _ui.update { it.copy(fase = Fase.Error("El modelo se generó, pero no se pudo guardar. Revisa tu conexión y reintenta.")) }
            }
        }
    }

    companion object {
        const val MAX_NOMBRE = 80
    }
}
