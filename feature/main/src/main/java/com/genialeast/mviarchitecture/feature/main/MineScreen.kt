package com.genialeast.mviarchitecture.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.genialeast.mviarchitecture.feature.news.NewsFeedCard

/**
 * 「我的」路由。退出登录和打开详情都是副作用。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Composable
fun MineRoute(
    onNavigateToLogin: () -> Unit,
    onOpenArticle: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MineViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MineEffect.NavigateToLogin -> onNavigateToLogin()
                is MineEffect.NavigateToDetail -> onOpenArticle(effect.id)
            }
        }
    }

    MineScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

/**
 * 「我的」。会话摘要和收藏列表都从 [MineUiState] 渲染。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Composable
fun MineScreen(
    state: MineUiState,
    onIntent: (MineIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = state.nickname.ifBlank { "未命名用户" },
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = "账号  ${state.username}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Text(
                        text = "身份  ${state.role}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Text(
                        text = "收藏  ${state.favorites.size}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
        item {
            Text(
                text = "退出会同时清掉会话和收藏，避免两个测试账号串数据。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            Button(
                onClick = { onIntent(MineIntent.Logout) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("退出登录")
            }
        }
        item {
            Text(
                text = "我的收藏",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        if (state.favorites.isEmpty()) {
            item {
                Text(
                    text = "还没有收藏。在新闻列表或详情里点一下心形图标。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(state.favorites, key = { it.id }) { article ->
            NewsFeedCard(
                article = article,
                favorite = true,
                onClick = { onIntent(MineIntent.OpenArticle(article.id)) },
                onToggleFavorite = { onIntent(MineIntent.RemoveFavorite(article.id)) },
            )
        }
    }
}
