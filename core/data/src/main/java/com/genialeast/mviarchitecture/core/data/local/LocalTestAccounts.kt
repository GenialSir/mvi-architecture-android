package com.genialeast.mviarchitecture.core.data.local

import com.genialeast.mviarchitecture.core.data.model.UserSession

/**
 * 内置测试账号。登录不走网络，用这份名单在本地完成校验。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
object LocalTestAccounts {

    data class Account(
        val username: String,
        val password: String,
        val nickname: String,
        val role: String,
    )

    val all: List<Account> = listOf(
        Account(
            username = "reader",
            password = "123456",
            nickname = "读者小明",
            role = "读者",
        ),
        Account(
            username = "editor",
            password = "123456",
            nickname = "编辑小林",
            role = "编辑",
        ),
    )

    /**
     * 账号密码完全匹配才返回会话。空账号、错密码都返回 null，由仓库转成错误文案。
     */
    fun match(username: String, password: String): UserSession? {
        val account = all.firstOrNull {
            it.username == username.trim() && it.password == password
        } ?: return null
        return UserSession(
            username = account.username,
            nickname = account.nickname,
            role = account.role,
        )
    }
}
