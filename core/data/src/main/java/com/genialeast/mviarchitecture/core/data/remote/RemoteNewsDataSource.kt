package com.genialeast.mviarchitecture.core.data.remote

import com.genialeast.mviarchitecture.core.data.mapper.toArticle
import com.genialeast.mviarchitecture.core.data.model.NewsArticle
import com.genialeast.mviarchitecture.core.network.api.RecipesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 网络资讯数据源。请求与 Mvvm 演练工程相同的 recipes.json，再映射成资讯模型。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Singleton
class RemoteNewsDataSource @Inject constructor(
    private val recipesApi: RecipesApi,
) {

    suspend fun load(): List<NewsArticle> = withContext(Dispatchers.IO) {
        recipesApi.fetchRecipes()
            .map { it.toArticle() }
            .filter { it.id.isNotBlank() && it.title.isNotBlank() }
    }
}
