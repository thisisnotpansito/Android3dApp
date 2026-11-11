package com.example.android3dapp.data

import com.example.android3dapp.domain.Modelo3D
import com.example.android3dapp.domain.NuevoModelo

/**
 * TEC-02 · Contrato de datos. Los ViewModel solo conocen esta interfaz, nunca
 * una implementación concreta.
 *
 * La cumplen [RepositorioSimulado] (datos de prueba, en memoria) y [RepositorioRemoto]
 * (la API del entregable 2). Cuál se usa lo decide una sola línea en `app/AppContenedor.kt`.
 */
interface ModeloRepository {

    /** Todos los modelos del usuario. Lanza [ErrorDeLectura] si no se pueden leer. */
    suspend fun obtenerTodos(): List<Modelo3D>

    /** Un modelo por su id, o `null` si no existe. Lanza [ErrorDeLectura] si falla. */
    suspend fun obtenerPorId(id: String): Modelo3D?

    /** HU-02 · Guarda un modelo generado con su foto. Lanza [ErrorDeGuardado] si falla. */
    suspend fun crear(nuevo: NuevoModelo): Modelo3D

    /** HU-01 · El archivo .glb del modelo. Lanza [ErrorDeLectura] si no se puede bajar. */
    suspend fun obtenerArchivo3D(id: String): ByteArray

    /** HU-10 · Borra el modelo con su foto y su archivo 3D. Lanza [ErrorDeGuardado] si falla. */
    suspend fun eliminar(id: String)
}

/** Los datos no se pudieron leer. El ViewModel lo convierte en el estado «error». */
class ErrorDeLectura(mensaje: String, causa: Throwable? = null) : Exception(mensaje, causa)

/** Los datos no se pudieron guardar o borrar. */
class ErrorDeGuardado(mensaje: String, causa: Throwable? = null) : Exception(mensaje, causa)
