package com.genialeast.mviarchitecture.feature.login

import androidx.lifecycle.viewModelScope
import com.genialeast.mviarchitecture.core.common.mvi.MviViewModel
import com.genialeast.mviarchitecture.core.common.result.AppResult
import com.genialeast.mviarchitecture.core.data.local.LocalTestAccounts
import com.genialeast.mviarchitecture.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 登录页 ViewModel。只接收 [LoginIntent]，用 reducer 更新 [LoginUiState]，跳转走 [LoginEffect]。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : MviViewModel<LoginUiState, LoginIntent, LoginEffect>(LoginUiState()) {

    init {
        setState { copy(testAccounts = LocalTestAccounts.toHints()) }
    }

    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UsernameChanged -> setState {
                copy(username = intent.value, errorMessage = "")
            }
            is LoginIntent.PasswordChanged -> setState {
                copy(password = intent.value, errorMessage = "")
            }
            LoginIntent.TogglePasswordVisible -> setState {
                copy(passwordVisible = !passwordVisible)
            }
            is LoginIntent.FillTestAccount -> setState {
                copy(
                    username = intent.username,
                    password = intent.password,
                    errorMessage = "",
                )
            }
            LoginIntent.Submit -> submit()
        }
    }

    private fun submit() {
        if (currentState.isLoading) return
        val username = currentState.username
        val password = currentState.password
        setState { copy(isLoading = true, errorMessage = "") }
        viewModelScope.launch {
            when (val result = authRepository.login(username, password)) {
                is AppResult.Success -> {
                    setState { copy(isLoading = false) }
                    sendEffect(LoginEffect.ShowMessage("欢迎回来，${result.data.nickname}"))
                    sendEffect(LoginEffect.NavigateToMain)
                }
                is AppResult.Error -> {
                    setState { copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }
}
