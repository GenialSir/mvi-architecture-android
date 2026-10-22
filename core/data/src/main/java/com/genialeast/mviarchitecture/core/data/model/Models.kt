package com.genialeast.mviarchitecture.core.data.model

import androidx.annotation.DrawableRes

/**
 * 本地登录会话。当前没有登录接口，字段只来自内置测试账号。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
data class UserSession(
    val username: String,
    val nickname: String,
    val role: String,
)

/**
 * 界面使用的资讯模型。封面资源在映射时从传输对象转进来，界面不再认识接口字段。
 *
 * [imageUrl] 不为空时，封面组件优先加载网络图；为空时使用 [coverRes]。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val category: String,
    val source: String,
    val timeLabel: String,
    @DrawableRes val coverRes: Int,
    val imageUrl: String = "",
) {
    /** Coil 的加载模型：有地址用地址，否则用本地封面。 */
    val coverModel: Any
        get() = imageUrl.ifBlank { coverRes }
}
