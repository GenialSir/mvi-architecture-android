package com.genialeast.mviarchitecture.core.data.usecase

import com.genialeast.mviarchitecture.core.data.repository.AuthRepository
import com.genialeast.mviarchitecture.core.data.repository.FavoriteRepository
import javax.inject.Inject

/**
 * 退出登录。一个意图要同时清会话和收藏，编排放在用例里，不写进界面。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke() {
        favoriteRepository.clear()
        authRepository.logout()
    }
}
