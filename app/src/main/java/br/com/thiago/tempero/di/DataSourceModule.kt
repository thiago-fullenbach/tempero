package br.com.thiago.tempero.di

import br.com.thiago.tempero.data.local.MealLocalDataSource
import br.com.thiago.tempero.data.local.MealLocalDataSourceImpl
import br.com.thiago.tempero.data.remote.MealRemoteDataSource
import br.com.thiago.tempero.data.remote.MealRemoteDataSourceImpl
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