package com.janad.zerodrop.data.api

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.janad.zerodrop.data.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
// Provides network-related dependencies

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // After forwarding your port on your server machine (e.g., using VS Code's port forwarding feature),
    // replace the example URL below with the full public URL provided by the forwarding service.
    // Example format: "https://example-3007.euw.devtunnels.ms/"
    // Ensure that the forwarded port's visibility is set to public.
    private const val BASE_URL = "https://example-3007.euw.devtunnels.ms/"


    // Provides Json instance for serialization/deserialization
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // Provides OkHttpClient instance with auth interceptor and timeouts
    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val prefs = UserPreferences(context)
        return OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor {
                    // Synchronously fetch token from DataStore
                    runBlocking { prefs.getToken() }
                }
            )

            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }



    // Provides Retrofit instance configured with base URL, OkHttpClient, and Json converter
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // Provides ApiService instance for making network requests
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    // Provides UserPreferences instance for accessing user preferences
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}