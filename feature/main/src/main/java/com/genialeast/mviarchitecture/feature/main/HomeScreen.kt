package com.genialeast.mviarchitecture.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.genialeast.mviarchitecture.core.data.model.NewsSource
import com.genialeast.mviarchitecture.feature.news.NewsFeedCard

/**
 * 首页路由。打开详情通过副作用交给导航宿主。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Composable
fun HomeRoute(
    onOpenArticle: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToDetail -> onOpenArticle(effect.id)
            }
        }
    }

    HomeScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

/**
 * 首页。精选封面来自 [HomeUiState]，卡片不在这里计算列表。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column {
                Text(
                    text = if (state.nickname.isBlank()) "早上好" else "${state.nickname}，早上好",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = when {
                        state.isLoading -> "正在读取网络测试接口…"
                        state.loadedFrom == NewsSource.REMOTE -> "精选来自网络测试接口。新闻页可以改读本地目录。"
                        else -> "网络不可用，精选来自本地目录。"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp, bottom = 8.dp),
                )
            }
        }
        items(state.headlines, key = { it.id }) { article ->
            NewsFeedCard(
                article = article,
                onClick = { onIntent(HomeIntent.OpenArticle(article.id)) },
            )
        }
    }
}
