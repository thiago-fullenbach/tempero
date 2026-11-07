package br.com.thiago.tempero.core.database.di

import android.content.Context
import androidx.room.Room
import br.com.thiago.tempero.core.database.TemperoDatabase
import br.com.thiago.tempero.core.database.dao.FavoriteMealDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TemperoDatabase =
        Room.databaseBuilder(context, TemperoDatabase::class.java, "tempero.db").build()

    @Provides
    fun provideFavoriteMealDao(database: TemperoDatabase): FavoriteMealDao =
        database.favoriteMealDao()
}
