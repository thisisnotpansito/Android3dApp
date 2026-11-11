package com.example.android3dapp.app

import android.content.Context
import com.example.android3dapp.BuildConfig
import com.example.android3dapp.data.AlmacenLocal
import com.example.android3dapp.data.ModeloRepository
import com.example.android3dapp.data.RepositorioRemoto
import com.example.android3dapp.data.RepositorioSimulado
import com.example.android3dapp.data.generacion.GeneradorInflado
import com.example.android3dapp.data.generacion.GeneradorModelo3D

/**
 * Composición de la app: aquí, y solo aquí, se decide qué implementación de cada
 * contrato se usa.
 *
 * - Con `creative3d.apiUrl` en gradle.properties, los modelos se guardan en Neon a
 *   través de la API (TEC-05).
 * - Sin ella, se guardan en el teléfono ([AlmacenLocal]) junto con dos modelos de
 *   demostración que vienen en el APK.
 * - El modelo 3D se genera en el teléfono (HU-02). Un servicio de IA (INV-05) sería
 *   otra implementación de [GeneradorModelo3D].
 */
object AppContenedor {
    val usaApi: Boolean = BuildConfig.API_URL.isNotBlank()

    lateinit var repositorio: ModeloRepository
        private set

    val generador: GeneradorModelo3D = GeneradorInflado()

    /** Se llama una vez, al abrir la app (MainActivity). */
    fun iniciar(context: Context) {
        if (::repositorio.isInitialized) return
        repositorio = if (usaApi) {
            RepositorioRemoto(BuildConfig.API_URL)
        } else {
            val almacen = AlmacenLocal(context.filesDir)
            RepositorioSimulado(almacen = almacen, demos = almacen.sembrarDemos(context.assets))
        }
    }
}
