package br.com.thiago.tempero.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Árvore Timber usada em release: envia ao Crashlytics apenas WARN e ERROR.
 * Mensagens de WARN/ERROR viram breadcrumbs (log) e, quando há [Throwable],
 * ele é registrado como exceção não fatal.
 */
class CrashlyticsTree : Timber.Tree() {

    private val crashlytics get() = FirebaseCrashlytics.getInstance()

    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.WARN

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val level = if (priority == Log.ERROR) "E" else "W"
        crashlytics.log("$level/${tag.orEmpty()}: $message")
        t?.let(crashlytics::recordException)
    }
}
