package br.com.thiago.tempero.data.analytics

import android.os.Bundle
import br.com.thiago.tempero.core.domain.analytics.AnalyticsEvent
import br.com.thiago.tempero.core.domain.analytics.AnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics
import jakarta.inject.Inject
import jakarta.inject.Singleton
import timber.log.Timber

/** Envia os [AnalyticsEvent] ao Firebase Analytics, convertendo os parâmetros em [Bundle]. */
@Singleton
class FirebaseAnalyticsTracker @Inject constructor(
    private val analytics: FirebaseAnalytics
) : AnalyticsTracker {

    override fun track(event: AnalyticsEvent) {
        Timber.d("Analytics: %s %s", event.name, event.params)
        analytics.logEvent(event.name, event.params.toBundle())
    }

    private fun Map<String, Any>.toBundle(): Bundle = Bundle().also { bundle ->
        forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Long -> bundle.putLong(key, value)
                is Int -> bundle.putLong(key, value.toLong())
                is Double -> bundle.putDouble(key, value)
                else -> bundle.putString(key, value.toString())
            }
        }
    }
}
