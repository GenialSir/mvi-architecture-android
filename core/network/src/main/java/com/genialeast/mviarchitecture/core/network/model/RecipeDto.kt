package com.genialeast.mviarchitecture.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 测试接口 recipes.json 的条目。只保留资讯展示要用的字段，其余 JSON 由 Moshi 忽略。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@JsonClass(generateAdapter = false)
data class RecipeDto(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "headline") val headline: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "image") val image: String = "",
    @Json(name = "thumb") val thumb: String = "",
    @Json(name = "time") val time: String = "",
    @Json(name = "calories") val calories: String = "",
    @Json(name = "difficulty") val difficulty: Int = 0,
)
