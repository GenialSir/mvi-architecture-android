package com.genialeast.mviarchitecture.feature.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.genialeast.mviarchitecture.feature.news.NewsRoute

/**
 * 主壳路由。三个页面各自收集自己的状态，主壳只决定哪一页在前台。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Composable
fun MainRoute(
    onOpenArticle: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onOpenWatermark: () -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MainScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        onOpenArticle = onOpenArticle,
        onNavigateToLogin = onNavigateToLogin,
        onOpenWatermark = onOpenWatermark,
        onShowMessage = onShowMessage,
    )
}

/**
 * 主壳界面。底部选中项只读 [MainUiState.currentTab]，点击时发出 [MainIntent.SelectTab]。
 *
 * 切走的页面会离开组合，但它的 ViewModel 仍挂在这个导航目的地上，搜索词和分类不会丢。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainUiState,
    onIntent: (MainIntent) -> Unit,
    onOpenArticle: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onOpenWatermark: () -> Unit,
    onShowMessage: (String) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(state.currentTab.label) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = state.currentTab == tab,
                        onClick = { onIntent(MainIntent.SelectTab(tab)) },
                        icon = { Icon(tab.icon(), contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        when (state.currentTab) {
            MainTab.HOME -> HomeRoute(
                onOpenArticle = onOpenArticle,
                modifier = Modifier.padding(innerPadding),
            )
            MainTab.NEWS -> NewsRoute(
                onOpenArticle = onOpenArticle,
                onShowMessage = onShowMessage,
                modifier = Modifier.padding(innerPadding),
            )
            MainTab.MINE -> MineRoute(
                onNavigateToLogin = onNavigateToLogin,
                onOpenArticle = onOpenArticle,
                onOpenWatermark = onOpenWatermark,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

/** Tab 图标是展示细节，不进入状态。 */
private fun MainTab.icon(): ImageVector = when (this) {
    MainTab.HOME -> Icons.Outlined.Home
    MainTab.NEWS -> Icons.AutoMirrored.Outlined.Article
    MainTab.MINE -> Icons.Outlined.Person
}
