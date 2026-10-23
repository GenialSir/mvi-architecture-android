package com.genialeast.mviarchitecture.feature.main

import androidx.lifecycle.viewModelScope
import com.genialeast.mviarchitecture.core.common.mvi.MviViewModel
import com.genialeast.mviarchitecture.core.data.repository.AuthRepository
import com.genialeast.mviarchitecture.core.data.repository.FavoriteRepository
import com.genialeast.mviarchitecture.core.data.repository.NewsRepository
import com.genialeast.mviarchitecture.core.data.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 「我的」ViewModel。会话、收藏列表和退出登录都从这里分发。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@HiltViewModel
class MineViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val newsRepository: NewsRepository,
    private val favoriteRepository: FavoriteRepository,
    private val logoutUseCase: LogoutUseCase,
) : MviViewModel<MineUiState, MineIntent, MineEffect>(MineUiState()) {

    init {
        viewModelScope.launch {
            val session = authRepository.currentSession()
            favoriteRepository.observeIds().collect { ids ->
                val favorites = newsRepository.articlesByIds(ids)
                setState {
                    copy(
                        nickname = session?.nickname.orEmpty(),
                        username = session?.username.orEmpty(),
                        role = session?.role.orEmpty(),
                        favorites = favorites,
                    )
                }
            }
        }
    }

    override fun handleIntent(intent: MineIntent) {
        when (intent) {
            MineIntent.Logout -> logout()
            MineIntent.OpenWatermark -> sendEffect(MineEffect.NavigateToWatermark)
            is MineIntent.OpenArticle -> sendEffect(MineEffect.NavigateToDetail(intent.id))
            is MineIntent.RemoveFavorite -> {
                viewModelScope.launch { favoriteRepository.toggle(intent.id) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            sendEffect(MineEffect.NavigateToLogin)
        }
    }
}
