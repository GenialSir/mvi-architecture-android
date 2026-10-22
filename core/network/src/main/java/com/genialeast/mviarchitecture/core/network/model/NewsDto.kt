package com.genialeast.mviarchitecture.core.network.model

import com.squareup.moshi.JsonClass

/**
 * 资讯接口的传输对象。字段按接口原文命名，不直接拿给界面渲染。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@JsonClass(generateAdapter = true)
data class NewsDto(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val category: String,
    val source: String,
    val publishedAt: String,
    val coverKey: String,
    val imageUrl: String = "",
)
