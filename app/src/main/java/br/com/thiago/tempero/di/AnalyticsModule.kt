package br.com.thiago.tempero.di

import android.content.Context
import br.com.thiago.tempero.data.analytics.FirebaseAnalyticsTracker
import br.com.thiago.tempero.domain.analytics.AnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(impl: FirebaseAnalyticsTracker): AnalyticsTracker

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
            FirebaseAnalytics.getInstance(context)
    }
}
