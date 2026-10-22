package com.genialeast.mviarchitecture.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genialeast.mviarchitecture.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 冷启动分流。只决定第一个页面，不承担登录页或主壳的业务状态。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            _startDestination.value = if (authRepository.currentSession() != null) {
                AppRoutes.MAIN
            } else {
                AppRoutes.LOGIN
            }
        }
    }
}
