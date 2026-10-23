package com.genialeast.mviarchitecture.navigation

import com.genialeast.mviarchitecture.feature.news.NewsDetailViewModel

/**
 * 应用级路由。底部 Tab 不在这里，详情页才是独立路由。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
object AppRoutes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val WATERMARK = "watermark"
    const val NEWS_DETAIL = "news/{${NewsDetailViewModel.ARTICLE_ID}}"

    fun newsDetail(articleId: String): String = "news/$articleId"
}
