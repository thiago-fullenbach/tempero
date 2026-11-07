package br.com.thiago.tempero.domain.analytics

/** Registra eventos de analytics; a implementação de produção usa o Firebase. */
interface AnalyticsTracker {
    fun track(event: AnalyticsEvent)
}
