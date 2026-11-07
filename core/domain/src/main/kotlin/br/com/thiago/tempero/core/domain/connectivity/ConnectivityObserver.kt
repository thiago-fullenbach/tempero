package br.com.thiago.tempero.core.domain.connectivity

import kotlinx.coroutines.flow.Flow

/** Observa se o dispositivo tem acesso à internet. */
interface ConnectivityObserver {
    val isOnline: Flow<Boolean>
}
