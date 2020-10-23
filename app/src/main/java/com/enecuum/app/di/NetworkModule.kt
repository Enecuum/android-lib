package com.enecuum.app.di

import com.enecuum.app.BuildConfig
import com.enecuum.app.Config.CONNECT_TIMEOUT_IN_MINUTES
import com.enecuum.app.Config.PING_TIMEOUT_IN_SECONDS
import com.enecuum.app.Config.READ_TIMEOUT_IN_MINUTES
import com.enecuum.app.Config.WRITE_TIMEOUT_IN_MINUTES
import com.enecuum.app.api.ApiRouter
import com.enecuum.app.api.Api
import com.enecuum.app.service.SocketService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    single<OkHttpClient> {

        val httpBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            httpBuilder.addInterceptor(interceptor)
        }

        httpBuilder
            .pingInterval(PING_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT_IN_MINUTES, TimeUnit.MINUTES)
            .readTimeout(READ_TIMEOUT_IN_MINUTES, TimeUnit.MINUTES)
            .writeTimeout(WRITE_TIMEOUT_IN_MINUTES, TimeUnit.MINUTES)
            .build()

    }

    single<Api> {
        Retrofit.Builder()
            .baseUrl(ApiRouter.apiURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(get())
            .build().create(Api::class.java)
    }

    single { SocketService(androidApplication(), get()) }
}