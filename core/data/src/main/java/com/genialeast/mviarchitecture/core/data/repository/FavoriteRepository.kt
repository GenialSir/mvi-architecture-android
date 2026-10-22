package com.genialeast.mviarchitecture.core.data.repository

import com.genialeast.mviarchitecture.core.data.local.FavoriteLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 收藏仓库。对外是资讯 id 流，对内是 DataStore。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
interface FavoriteRepository {
    fun observeIds(): Flow<Set<String>>
    suspend fun toggle(id: String)
    suspend fun clear()
}

/**
 * 收藏仓库实现。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
) : FavoriteRepository {

    override fun observeIds(): Flow<Set<String>> = favoriteLocalDataSource.observeIds()

    override suspend fun toggle(id: String) {
        favoriteLocalDataSource.toggle(id)
    }

    override suspend fun clear() {
        favoriteLocalDataSource.clear()
    }
}
