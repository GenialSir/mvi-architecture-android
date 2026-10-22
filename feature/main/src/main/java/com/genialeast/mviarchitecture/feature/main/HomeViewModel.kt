package com.genialeast.mviarchitecture.feature.main

import androidx.lifecycle.viewModelScope
import com.genialeast.mviarchitecture.core.common.mvi.MviViewModel
import com.genialeast.mviarchitecture.core.data.model.NewsSource
import com.genialeast.mviarchitecture.core.data.repository.AuthRepository
import com.genialeast.mviarchitecture.core.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页 ViewModel。精选列表来自资讯仓库，昵称来自当前会话。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val newsRepository: NewsRepository,
) : MviViewModel<HomeUiState, HomeIntent, HomeEffect>(HomeUiState()) {

    init {
        viewModelScope.launch {
            val session = authRepository.currentSession()
            val page = newsRepository.load(NewsSource.REMOTE)
            setState {
                copy(
                    nickname = session?.nickname.orEmpty(),
                    headlines = page.articles.take(HEADLINE_COUNT),
                    loadedFrom = page.loadedFrom,
                    isLoading = false,
                )
            }
        }
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OpenArticle -> sendEffect(HomeEffect.NavigateToDetail(intent.id))
        }
    }

    private companion object {
        const val HEADLINE_COUNT = 3
    }
}
