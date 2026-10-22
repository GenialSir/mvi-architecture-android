package com.genialeast.mviarchitecture.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.genialeast.mviarchitecture.feature.login.LoginRoute
import com.genialeast.mviarchitecture.feature.main.MainRoute
import com.genialeast.mviarchitecture.feature.news.NewsDetailRoute
import com.genialeast.mviarchitecture.feature.news.NewsDetailViewModel
import kotlinx.coroutines.launch

/**
 * 全局导航宿主。登录进主壳，详情压在主壳之上，退出登录清空回退栈。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Composable
fun NewsNavHost(
    navController: NavHostController = rememberNavController(),
    sessionViewModel: SessionViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val startDestination by sessionViewModel.startDestination.collectAsStateWithLifecycle()

    val showMessage: (String) -> Unit = remember(snackbarHostState, scope) {
        { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }
    val openArticle: (String) -> Unit = remember(navController) {
        { articleId ->
            navController.navigate(AppRoutes.newsDetail(articleId))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (startDestination == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            NavHost(
                navController = navController,
                startDestination = startDestination!!,
            ) {
                composable(AppRoutes.LOGIN) {
                    LoginRoute(
                        onNavigateToMain = {
                            navController.navigate(AppRoutes.MAIN) {
                                popUpTo(AppRoutes.LOGIN) { inclusive = true }
                            }
                        },
                        onShowMessage = showMessage,
                    )
                }
                composable(AppRoutes.MAIN) {
                    MainRoute(
                        onOpenArticle = openArticle,
                        onNavigateToLogin = {
                            navController.navigate(AppRoutes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onShowMessage = showMessage,
                    )
                }
                composable(
                    route = AppRoutes.NEWS_DETAIL,
                    arguments = listOf(
                        navArgument(NewsDetailViewModel.ARTICLE_ID) { type = NavType.StringType },
                    ),
                ) {
                    NewsDetailRoute(onBack = { navController.popBackStack() })
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .imePadding()
                .padding(bottom = 72.dp),
        )
    }
}
