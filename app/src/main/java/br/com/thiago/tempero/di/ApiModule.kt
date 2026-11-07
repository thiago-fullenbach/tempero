package br.com.thiago.tempero.di

import br.com.thiago.tempero.BuildConfig
import br.com.thiago.tempero.data.remote.api.THE_MEAL_DB_API_BASE_URL
import br.com.thiago.tempero.data.remote.api.TheMealDBAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }.apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()

    @Provides
    @Singleton
    fun provideRetrofitTheMealDBAPI(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(THE_MEAL_DB_API_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideTheMealDbApi(retrofit: Retrofit): TheMealDBAPI =
        retrofit.create(TheMealDBAPI::class.java)
}