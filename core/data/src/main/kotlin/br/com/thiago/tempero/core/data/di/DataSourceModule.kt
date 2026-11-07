package br.com.thiago.tempero.core.data.di

import br.com.thiago.tempero.core.database.MealLocalDataSource
import br.com.thiago.tempero.core.database.MealLocalDataSourceImpl
import br.com.thiago.tempero.core.network.MealRemoteDataSource
import br.com.thiago.tempero.core.network.MealRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindRemoteDataSource(impl: MealRemoteDataSourceImpl): MealRemoteDataSource

    @Binds
    abstract fun bindLocalDataSource(impl: MealLocalDataSourceImpl): MealLocalDataSource
}
