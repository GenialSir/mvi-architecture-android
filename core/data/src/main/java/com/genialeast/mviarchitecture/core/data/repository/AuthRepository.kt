package com.genialeast.mviarchitecture.core.data.repository

import com.genialeast.mviarchitecture.core.common.result.AppResult
import com.genialeast.mviarchitecture.core.data.local.LocalTestAccounts
import com.genialeast.mviarchitecture.core.data.local.SessionLocalDataSource
import com.genialeast.mviarchitecture.core.data.model.UserSession
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 鉴权仓库。对上只暴露登录、登出和读取会话。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
interface AuthRepository {
    suspend fun login(username: String, password: String): AppResult<UserSession>
    suspend fun logout()
    suspend fun currentSession(): UserSession?
}

/**
 * 本地鉴权实现。短延迟只为了让登录按钮的加载态在界面上看得见。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val sessionLocalDataSource: SessionLocalDataSource,
) : AuthRepository {

    override suspend fun login(username: String, password: String): AppResult<UserSession> {
        val account = username.trim()
        if (account.isEmpty()) {
            return AppResult.Error("请输入账号")
        }
        if (password.isEmpty()) {
            return AppResult.Error("请输入密码")
        }
        delay(LOADING_DELAY_MS)
        val session = LocalTestAccounts.match(account, password)
            ?: return AppResult.Error("账号或密码不正确")
        sessionLocalDataSource.save(session)
        return AppResult.Success(session)
    }

    override suspend fun logout() {
        sessionLocalDataSource.clear()
    }

    override suspend fun currentSession(): UserSession? = sessionLocalDataSource.readSession()

    private companion object {
        const val LOADING_DELAY_MS = 450L
    }
}
