package com.genialeast.mviarchitecture.core.network

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

/**
 * 给 Application 取 OkHttpClient 用的入口。Application 不能用构造注入。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface NetworkEntryPoint {
    fun okHttpClient(): OkHttpClient
}
