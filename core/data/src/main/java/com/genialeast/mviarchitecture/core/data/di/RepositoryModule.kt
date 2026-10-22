package com.genialeast.mviarchitecture.core.data.di

import com.genialeast.mviarchitecture.core.data.repository.AuthRepository
import com.genialeast.mviarchitecture.core.data.repository.AuthRepositoryImpl
import com.genialeast.mviarchitecture.core.data.repository.FavoriteRepository
import com.genialeast.mviarchitecture.core.data.repository.FavoriteRepositoryImpl
import com.genialeast.mviarchitecture.core.data.repository.NewsRepository
import com.genialeast.mviarchitecture.core.data.repository.NewsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 把仓库接口绑定到当前实现。页面和 ViewModel 只依赖接口。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository
}
