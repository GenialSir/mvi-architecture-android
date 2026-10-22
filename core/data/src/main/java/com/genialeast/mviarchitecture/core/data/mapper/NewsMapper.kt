package com.genialeast.mviarchitecture.core.data.mapper

import com.genialeast.mviarchitecture.core.data.R
import com.genialeast.mviarchitecture.core.data.model.NewsArticle
import com.genialeast.mviarchitecture.core.network.model.NewsDto

/**
 * 把接口传输对象映射成界面模型。封面 key 在这里换成 drawable，不把资源表泄漏给网络层。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
fun NewsDto.toArticle(): NewsArticle = NewsArticle(
    id = id,
    title = title,
    summary = summary,
    content = content,
    category = category,
    source = source,
    timeLabel = publishedAt,
    coverRes = coverKey.toCoverRes(),
    imageUrl = imageUrl,
)

private fun String.toCoverRes(): Int = when (this) {
    "flow" -> R.drawable.cover_flow
    "effect" -> R.drawable.cover_effect
    "city" -> R.drawable.cover_city
    "account" -> R.drawable.cover_account
    "tabs" -> R.drawable.cover_tabs
    "remote" -> R.drawable.cover_remote
    else -> R.drawable.cover_flow
}
