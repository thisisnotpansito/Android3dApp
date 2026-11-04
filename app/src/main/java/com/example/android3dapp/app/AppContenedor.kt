package com.example.android3dapp.app

import com.example.android3dapp.data.ModeloRepository
import com.example.android3dapp.data.RepositorioSimulado

/**
 * Composición de la app: aquí, y solo aquí, se decide qué implementación del
 * contrato se usa. Pasar a la API real en el entregable 2 es cambiar esta línea.
 */
object AppContenedor {
    val repositorio: ModeloRepository = RepositorioSimulado()
}
