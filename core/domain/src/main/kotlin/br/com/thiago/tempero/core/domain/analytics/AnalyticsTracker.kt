package br.com.thiago.tempero.core.domain.analytics

/** Registra eventos de analytics; a implementação de produção usa o Firebase. */
interface AnalyticsTracker {
    fun track(event: AnalyticsEvent)
}
