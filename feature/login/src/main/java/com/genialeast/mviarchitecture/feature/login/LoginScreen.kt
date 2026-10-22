package com.genialeast.mviarchitecture.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.genialeast.mviarchitecture.core.designsystem.theme.NewsTheme

/**
 * 登录路由：收集状态与副作用，把意图原样交给 ViewModel。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Composable
fun LoginRoute(
    onNavigateToMain: () -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 副作用用 collect 逐条消费。collectLatest 会在下一条到来时取消上一条。
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToMain -> onNavigateToMain()
                is LoginEffect.ShowMessage -> onShowMessage(effect.message)
            }
        }
    }

    LoginScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
    )
}

/**
 * 登录页纯渲染。不读取仓库，也不发起导航。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    state: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        Text(
            text = "资讯演练",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "本地测试账号登录，当前没有网络接口",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        Spacer(modifier = Modifier.height(28.dp))
        OutlinedTextField(
            value = state.username,
            onValueChange = { onIntent(LoginIntent.UsernameChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading,
            label = { Text("账号") },
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            singleLine = true,
            enabled = !state.isLoading,
            label = { Text("密码") },
            visualTransformation = if (state.passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { onIntent(LoginIntent.TogglePasswordVisible) }) {
                    Icon(
                        imageVector = if (state.passwordVisible) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.Visibility
                        },
                        contentDescription = if (state.passwordVisible) "隐藏密码" else "显示密码",
                    )
                }
            },
        )
        if (state.errorMessage.isNotBlank()) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Button(
            onClick = { onIntent(LoginIntent.Submit) },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text("登录")
            }
        }
        Text(
            text = "点一下测试账号，会把账号密码填进上面的输入框",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 28.dp),
        )
        FlowRow(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.testAccounts.forEach { account ->
                SuggestionChip(
                    onClick = {
                        onIntent(
                            LoginIntent.FillTestAccount(
                                username = account.username,
                                password = account.password,
                            ),
                        )
                    },
                    enabled = !state.isLoading,
                    label = {
                        Text("${account.nickname}  ${account.username} / ${account.password}")
                    },
                )
            }
        }
    }
}

/**
 * 登录页预览，使用一份固定状态，不启动 ViewModel。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    NewsTheme {
        LoginScreen(
            state = LoginUiState(
                testAccounts = listOf(
                    TestAccountHint("reader", "123456", "读者小明", "读者"),
                    TestAccountHint("editor", "123456", "编辑小林", "编辑"),
                ),
            ),
            onIntent = {},
        )
    }
}
