package br.com.thiago.tempero.di

import android.content.Context
import br.com.thiago.tempero.core.analytics.di.AnalyticsModule
import br.com.thiago.tempero.core.data.di.ConnectivityModule
import br.com.thiago.tempero.core.data.di.DataSourceModule
import br.com.thiago.tempero.core.database.di.DatabaseModule
import androidx.room.Room
import br.com.thiago.tempero.core.database.MealLocalDataSource
import br.com.thiago.tempero.core.database.MealLocalDataSourceImpl
import br.com.thiago.tempero.core.database.TemperoDatabase
import br.com.thiago.tempero.core.database.dao.FavoriteMealDao
import br.com.thiago.tempero.core.network.MealRemoteDataSource
import br.com.thiago.tempero.core.domain.analytics.AnalyticsTracker
import br.com.thiago.tempero.core.domain.connectivity.ConnectivityObserver
import br.com.thiago.tempero.core.testing.fake.FakeAnalyticsTracker
import br.com.thiago.tempero.core.testing.fake.FakeConnectivityObserver
import br.com.thiago.tempero.core.testing.fake.FakeMealRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.testing.TestInstallIn
import jakarta.inject.Singleton

/**
 * Módulos que trocam só as bordas do app (Firebase, rede, conectividade e arquivo do Room).
 * `DispatcherModule` e `RepositoryModule` ficam os reais, então a DI de produção também é exercitada.
 */

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [AnalyticsModule::class])
object TestAnalyticsModule {
    @Provides
    @Singleton
    fun provideFakeAnalytics(): FakeAnalyticsTracker = FakeAnalyticsTracker()

    @Provides
    fun provideAnalyticsTracker(fake: FakeAnalyticsTracker): AnalyticsTracker = fake
}

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
object TestDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TemperoDatabase =
        Room.inMemoryDatabaseBuilder(context, TemperoDatabase::class.java).build()

    @Provides
    fun provideFavoriteMealDao(database: TemperoDatabase): FavoriteMealDao = database.favoriteMealDao()
}

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DataSourceModule::class])
object TestDataSourceModule {
    @Provides
    @Singleton
    fun provideFakeRemote(): FakeMealRemoteDataSource = FakeMealRemoteDataSource()

    @Provides
    fun provideRemote(fake: FakeMealRemoteDataSource): MealRemoteDataSource = fake

    @Provides
    fun provideLocal(dao: FavoriteMealDao): MealLocalDataSource = MealLocalDataSourceImpl(dao)
}

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [ConnectivityModule::class])
object TestConnectivityModule {
    @Provides
    @Singleton
    fun provideFakeConnectivity(): FakeConnectivityObserver = FakeConnectivityObserver(online = true)

    @Provides
    fun provideConnectivity(fake: FakeConnectivityObserver): ConnectivityObserver = fake
}
