package br.com.thiago.tempero.core.testing.fake

import br.com.thiago.tempero.core.domain.connectivity.ConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeConnectivityObserver(online: Boolean = true) : ConnectivityObserver {
    val online = MutableStateFlow(online)
    override val isOnline: Flow<Boolean> = this.online
}
