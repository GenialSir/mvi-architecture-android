package com.genialeast.mviarchitecture.feature.news

import androidx.lifecycle.viewModelScope
import com.genialeast.mviarchitecture.core.common.mvi.MviViewModel
import com.genialeast.mviarchitecture.core.data.model.NewsArticle
import com.genialeast.mviarchitecture.core.data.model.NewsSource
import com.genialeast.mviarchitecture.core.data.repository.FavoriteRepository
import com.genialeast.mviarchitecture.core.data.repository.NewsLoad
import com.genialeast.mviarchitecture.core.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 新闻列表 ViewModel。持有完整目录，对外只暴露筛过的列表。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val favoriteRepository: FavoriteRepository,
) : MviViewModel<NewsUiState, NewsIntent, NewsEffect>(NewsUiState()) {

    /** 当前数据源返回的全量列表。筛选只发生在 [publish]。 */
    private var catalog: List<NewsArticle> = emptyList()

    init {
        viewModelScope.launch {
            apply(newsRepository.load(NewsSource.REMOTE))
        }
        viewModelScope.launch {
            favoriteRepository.observeIds().collect { ids ->
                setState { copy(favoriteIds = ids) }
            }
        }
    }

    override fun handleIntent(intent: NewsIntent) {
        when (intent) {
            is NewsIntent.QueryChanged -> {
                setState { copy(query = intent.value) }
                publish()
            }
            is NewsIntent.SelectCategory -> {
                setState { copy(selectedCategory = intent.category) }
                publish()
            }
            is NewsIntent.SelectSource -> selectSource(intent.source)
            NewsIntent.Refresh -> refresh()
            is NewsIntent.OpenArticle -> sendEffect(NewsEffect.NavigateToDetail(intent.id))
            is NewsIntent.ToggleFavorite -> toggleFavorite(intent.id)
        }
    }

    private fun refresh() {
        if (currentState.isRefreshing || currentState.isLoading) return
        val requested = currentState.source
        setState { copy(isRefreshing = true) }
        viewModelScope.launch {
            val page = newsRepository.load(requested, force = true)
            apply(page)
            val notice = page.message.ifBlank { "已从${page.loadedFrom.label}更新" }
            sendEffect(NewsEffect.ShowMessage(notice))
        }
    }

    private fun selectSource(source: NewsSource) {
        if (currentState.source == source || currentState.isLoading) return
        setState { copy(source = source, isLoading = true, statusMessage = "") }
        viewModelScope.launch {
            apply(newsRepository.load(source))
        }
    }

    private fun apply(page: NewsLoad) {
        catalog = page.articles
        val categories = listOf(NEWS_CATEGORY_ALL) + catalog.map { it.category }.distinct()
        val selected = currentState.selectedCategory.takeIf { it in categories } ?: NEWS_CATEGORY_ALL
        setState {
            copy(
                source = page.requested,
                loadedFrom = page.loadedFrom,
                categories = categories,
                selectedCategory = selected,
                statusMessage = page.message,
                isLoading = false,
                isRefreshing = false,
            )
        }
        publish()
    }

    private fun toggleFavorite(id: String) {
        viewModelScope.launch {
            favoriteRepository.toggle(id)
        }
    }

    /** 用当前搜索词和分类，从全量目录算出可见列表。 */
    private fun publish() {
        val state = currentState
        val query = state.query.trim()
        val visible = catalog.filter { article ->
            val categoryOk = state.selectedCategory == NEWS_CATEGORY_ALL ||
                article.category == state.selectedCategory
            val queryOk = query.isEmpty() ||
                article.title.contains(query) ||
                article.summary.contains(query)
            categoryOk && queryOk
        }
        setState { copy(articles = visible) }
    }
}
