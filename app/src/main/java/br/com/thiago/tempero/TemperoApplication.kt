package br.com.thiago.tempero

import android.app.Application
import android.os.StrictMode
import br.com.thiago.tempero.util.CrashlyticsTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TemperoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Release: apenas WARN/ERROR são enviados ao Crashlytics; demais níveis viram no-op.
        if (!BuildConfig.DEBUG) {
            Timber.plant(CrashlyticsTree())
        } else {
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
