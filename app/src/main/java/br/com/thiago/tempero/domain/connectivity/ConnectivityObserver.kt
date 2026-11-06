package br.com.thiago.tempero.domain.connectivity

import kotlinx.coroutines.flow.Flow

/** Observa se o dispositivo tem acesso à internet. */
interface ConnectivityObserver {
    val isOnline: Flow<Boolean>
}
