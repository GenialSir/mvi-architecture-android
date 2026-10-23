package com.genialeast.mviarchitecture.feature.main

import androidx.compose.runtime.Immutable
import com.genialeast.mviarchitecture.core.data.model.NewsArticle
import com.genialeast.mviarchitecture.core.data.model.NewsSource

/**
 * 底部三个入口。当前选中项是主壳状态的一部分，不另开导航栈。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
enum class MainTab(val label: String) {
    HOME("首页"),
    NEWS("新闻"),
    MINE("我的"),
}

/**
 * 主壳唯一状态。只记录当前 Tab，列表和会话分别由各页自己的状态持有。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Immutable
data class MainUiState(
    val currentTab: MainTab = MainTab.HOME,
)

/**
 * 主壳意图。退出登录属于「我的」，不放在这里。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
sealed interface MainIntent {
    data class SelectTab(val tab: MainTab) : MainIntent
}

/**
 * 首页状态：问候语和精选封面。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Immutable
data class HomeUiState(
    val nickname: String = "",
    val headlines: List<NewsArticle> = emptyList(),
    val loadedFrom: NewsSource = NewsSource.REMOTE,
    val isLoading: Boolean = true,
)

/**
 * 首页意图。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
sealed interface HomeIntent {
    data class OpenArticle(val id: String) : HomeIntent
}

/**
 * 首页一次性副作用。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
sealed interface HomeEffect {
    data class NavigateToDetail(val id: String) : HomeEffect
}

/**
 * 「我的」状态：当前会话和已收藏的资讯。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Immutable
data class MineUiState(
    val nickname: String = "",
    val username: String = "",
    val role: String = "",
    val favorites: List<NewsArticle> = emptyList(),
)

/**
 * 「我的」意图。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
sealed interface MineIntent {
    data object Logout : MineIntent
    data object OpenWatermark : MineIntent
    data class OpenArticle(val id: String) : MineIntent
    data class RemoveFavorite(val id: String) : MineIntent
}

/**
 * 「我的」一次性副作用。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
sealed interface MineEffect {
    data object NavigateToLogin : MineEffect
    data object NavigateToWatermark : MineEffect
    data class NavigateToDetail(val id: String) : MineEffect
}
