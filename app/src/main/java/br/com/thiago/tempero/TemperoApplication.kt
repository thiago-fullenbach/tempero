package br.com.thiago.tempero

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TemperoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Release fica sem árvore de propósito: todas as chamadas Timber.* viram no-op.
        // Se um dia houver crash reporting (Crashlytics etc.), plantar a árvore dele aqui.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()          // leaks de Activity, Closeable não fechado, etc.
                    .penaltyLog()
                    .build()
            )
        }
    }
}
