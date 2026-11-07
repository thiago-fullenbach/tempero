package br.com.thiago.tempero.presentation.screen.detail

/**
 * Eventos que a tela Detalhe emite para fora dela. Descrevem o que o usuário fez,
 * não para onde ir; quem decide o destino é o coordinator.
 */
sealed interface DetailEvent {
    data object BackRequested : DetailEvent
}
