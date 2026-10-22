package com.genialeast.mviarchitecture.feature.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.genialeast.mviarchitecture.core.data.model.NewsSource

/**
 * 新闻列表路由：收集本页状态，把打开详情和提示交给外层导航。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Composable
fun NewsRoute(
    onOpenArticle: (String) -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NewsEffect.NavigateToDetail -> onOpenArticle(effect.id)
                is NewsEffect.ShowMessage -> onShowMessage(effect.message)
            }
        }
    }

    NewsScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

/**
 * 新闻列表。搜索、分类、刷新结果都来自 [NewsUiState]，界面不做筛选计算。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    state: NewsUiState,
    onIntent: (NewsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(NewsSource.entries) { source ->
                FilterChip(
                    selected = state.source == source,
                    onClick = { onIntent(NewsIntent.SelectSource(source)) },
                    enabled = !state.isLoading,
                    label = { Text(source.label) },
                )
            }
        }
        Text(
            text = when {
                state.statusMessage.isNotBlank() -> state.statusMessage
                state.isLoading -> "正在读取${state.source.label}数据…"
                else -> "当前展示：${state.loadedFrom.label}"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        OutlinedTextField(
            value = state.query,
            onValueChange = { onIntent(NewsIntent.QueryChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            singleLine = true,
            label = { Text("搜索") },
            placeholder = { Text("标题或摘要") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.categories) { category ->
                FilterChip(
                    selected = state.selectedCategory == category,
                    onClick = { onIntent(NewsIntent.SelectCategory(category)) },
                    label = { Text(category) },
                )
            }
        }
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(NewsIntent.Refresh) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (state.isLoading && state.articles.isEmpty()) {
                    item {
                        Text(
                            text = "正在读取${state.source.label}数据…",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 24.dp),
                        )
                    }
                } else if (state.articles.isEmpty()) {
                    item {
                        Text(
                            text = "没有符合条件的资讯",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 24.dp),
                        )
                    }
                }
                items(state.articles, key = { it.id }) { article ->
                    NewsFeedCard(
                        article = article,
                        favorite = article.id in state.favoriteIds,
                        onClick = { onIntent(NewsIntent.OpenArticle(article.id)) },
                        onToggleFavorite = { onIntent(NewsIntent.ToggleFavorite(article.id)) },
                    )
                }
            }
        }
    }
}
