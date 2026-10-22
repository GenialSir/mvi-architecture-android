package com.genialeast.mviarchitecture.feature.news

import androidx.compose.runtime.Immutable
import com.genialeast.mviarchitecture.core.data.model.NewsArticle
import com.genialeast.mviarchitecture.core.data.model.NewsSource

/** 新闻列表里「不过滤分类」的选项文案。 */
const val NEWS_CATEGORY_ALL = "全部"

/**
 * 新闻列表状态。可见列表已经按分类和搜索词筛过，界面不再自己过滤。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Immutable
data class NewsUiState(
    val query: String = "",
    val selectedCategory: String = NEWS_CATEGORY_ALL,
    val categories: List<String> = listOf(NEWS_CATEGORY_ALL),
    val articles: List<NewsArticle> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val source: NewsSource = NewsSource.REMOTE,
    val loadedFrom: NewsSource = NewsSource.REMOTE,
    val statusMessage: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
)

/**
 * 新闻列表意图。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
sealed interface NewsIntent {
    data class QueryChanged(val value: String) : NewsIntent
    data class SelectCategory(val category: String) : NewsIntent
    data class SelectSource(val source: NewsSource) : NewsIntent
    data object Refresh : NewsIntent
    data class OpenArticle(val id: String) : NewsIntent
    data class ToggleFavorite(val id: String) : NewsIntent
}

/**
 * 新闻列表一次性副作用。打开详情不写进列表状态。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
sealed interface NewsEffect {
    data class NavigateToDetail(val id: String) : NewsEffect
    data class ShowMessage(val message: String) : NewsEffect
}

/**
 * 资讯详情状态。找不到文章时 [notFound] 为 true，界面画空态。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Immutable
data class NewsDetailUiState(
    val article: NewsArticle? = null,
    val isFavorite: Boolean = false,
    val notFound: Boolean = false,
)

/**
 * 资讯详情意图。返回由导航宿主处理，不放进这里。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
sealed interface NewsDetailIntent {
    data object ToggleFavorite : NewsDetailIntent
}
