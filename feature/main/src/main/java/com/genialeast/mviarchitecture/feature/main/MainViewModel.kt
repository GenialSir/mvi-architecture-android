package com.genialeast.mviarchitecture.feature.main

import com.genialeast.mviarchitecture.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 主壳 ViewModel。只处理底部 Tab，不持有新闻或会话。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@HiltViewModel
class MainViewModel @Inject constructor() : MviViewModel<MainUiState, MainIntent, Nothing>(MainUiState()) {

    override fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.SelectTab -> setState { copy(currentTab = intent.tab) }
        }
    }
}
