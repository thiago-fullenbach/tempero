package br.com.thiago.tempero.di

import br.com.thiago.tempero.data.FavoriteRepositoryImpl
import br.com.thiago.tempero.data.MealRepositoryImpl
import br.com.thiago.tempero.domain.repository.FavoriteRepository
import br.com.thiago.tempero.domain.repository.MealRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMealRepository(impl: MealRepositoryImpl): MealRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository
}