package br.com.thiago.tempero.fake

import br.com.thiago.tempero.domain.analytics.AnalyticsEvent
import br.com.thiago.tempero.domain.analytics.AnalyticsTracker
import java.util.concurrent.CopyOnWriteArrayList

class FakeAnalyticsTracker : AnalyticsTracker {
    val events = CopyOnWriteArrayList<AnalyticsEvent>()

    override fun track(event: AnalyticsEvent) {
        events += event
    }

    inline fun <reified T : AnalyticsEvent> eventsOf(): List<T> = events.filterIsInstance<T>()
}
