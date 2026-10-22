package com.genialeast.mviarchitecture.core.data.local

import com.genialeast.mviarchitecture.core.data.mapper.toArticle
import com.genialeast.mviarchitecture.core.data.model.NewsArticle
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 本地资讯数据源。不访问网络，直接把内置目录映射成资讯模型。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Singleton
class LocalNewsDataSource @Inject constructor() {

    fun load(): List<NewsArticle> = LocalNewsCatalog.dtos.map { it.toArticle() }
}
