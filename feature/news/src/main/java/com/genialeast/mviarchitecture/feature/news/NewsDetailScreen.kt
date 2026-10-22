package com.genialeast.mviarchitecture.feature.news

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.genialeast.mviarchitecture.core.designsystem.component.NewsCoverImage

/**
 * 资讯详情路由。返回交给导航宿主，收藏仍走 Intent。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Composable
fun NewsDetailRoute(
    onBack: () -> Unit,
    viewModel: NewsDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NewsDetailScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        onBack = onBack,
    )
}

/**
 * 资讯详情。大图、正文和收藏都从 [NewsDetailUiState] 渲染。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    state: NewsDetailUiState,
    onIntent: (NewsDetailIntent) -> Unit,
    onBack: () -> Unit,
) {
    val article = state.article
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("资讯详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                        )
                    }
                },
                actions = {
                    if (article != null) {
                        IconButton(onClick = { onIntent(NewsDetailIntent.ToggleFavorite) }) {
                            Icon(
                                imageVector = if (state.isFavorite) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                },
                                contentDescription = if (state.isFavorite) "取消收藏" else "收藏",
                                tint = if (state.isFavorite) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            state.notFound -> {
                Text(
                    text = "这篇资讯不存在",
                    modifier = Modifier.padding(innerPadding).padding(20.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            article != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
                ) {
                    NewsCoverImage(
                        model = article.coverModel,
                        contentDescription = article.title,
                        height = 220.dp,
                    )
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = article.category,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                        Text(
                            text = "${article.source} · ${article.timeLabel}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                        Text(
                            text = article.content,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp),
                        )
                    }
                }
            }
        }
    }
}
