package com.genialeast.mviarchitecture.feature.news

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.genialeast.mviarchitecture.core.common.mvi.MviViewModel
import com.genialeast.mviarchitecture.core.data.repository.FavoriteRepository
import com.genialeast.mviarchitecture.core.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 资讯详情 ViewModel。文章 id 来自导航参数，收藏状态订阅仓库流。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val newsRepository: NewsRepository,
    private val favoriteRepository: FavoriteRepository,
) : MviViewModel<NewsDetailUiState, NewsDetailIntent, Nothing>(NewsDetailUiState()) {

    private val articleId: String = savedStateHandle.get<String>(ARTICLE_ID).orEmpty()

    init {
        viewModelScope.launch {
            val article = newsRepository.article(articleId)
            if (article == null) {
                setState { copy(notFound = true) }
                return@launch
            }
            setState { copy(article = article) }
            favoriteRepository.observeIds().collect { ids ->
                setState { copy(isFavorite = articleId in ids) }
            }
        }
    }

    override fun handleIntent(intent: NewsDetailIntent) {
        when (intent) {
            NewsDetailIntent.ToggleFavorite -> {
                if (articleId.isBlank() || currentState.notFound) return
                viewModelScope.launch { favoriteRepository.toggle(articleId) }
            }
        }
    }

    companion object {
        const val ARTICLE_ID = "articleId"
    }
}
