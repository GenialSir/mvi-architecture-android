package com.genialeast.mviarchitecture.feature.login

import androidx.compose.runtime.Immutable
import com.genialeast.mviarchitecture.core.data.local.LocalTestAccounts

/**
 * 登录页展示用的测试账号提示。密码明文只因为这是教学用本地账号。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Immutable
data class TestAccountHint(
    val username: String,
    val password: String,
    val nickname: String,
    val role: String,
)

/**
 * 登录页唯一状态。输入框、加载、错误都从这里渲染。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Immutable
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val testAccounts: List<TestAccountHint> = emptyList(),
)

/**
 * 登录页意图。每一次点击和输入都落成一个不可变对象，再交给 ViewModel。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
sealed interface LoginIntent {
    data class UsernameChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object TogglePasswordVisible : LoginIntent
    data class FillTestAccount(val username: String, val password: String) : LoginIntent
    data object Submit : LoginIntent
}

/**
 * 登录页一次性副作用。成功提示和跳转不进 [LoginUiState]。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
sealed interface LoginEffect {
    data object NavigateToMain : LoginEffect
    data class ShowMessage(val message: String) : LoginEffect
}

/** 把本地账号名单转成界面提示，避免界面直接拼装数据源。 */
fun LocalTestAccounts.toHints(): List<TestAccountHint> = all.map { account ->
    TestAccountHint(
        username = account.username,
        password = account.password,
        nickname = account.nickname,
        role = account.role,
    )
}
