package com.genialeast.mviarchitecture.core.network

import com.genialeast.mviarchitecture.core.common.constant.AppConstant
import com.genialeast.mviarchitecture.core.network.api.RecipesApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 网络层装配：Moshi、OkHttp、Retrofit，以及测试食谱接口。
 *
 * OkHttp 同时交给 Coil 拉封面。日志只打请求行，避免把图片字节打进 Logcat。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .connectTimeout(AppConstant.API_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .readTimeout(AppConstant.API_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .writeTimeout(AppConstant.API_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(AppConstant.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideRecipesApi(retrofit: Retrofit): RecipesApi = retrofit.create(RecipesApi::class.java)
}
