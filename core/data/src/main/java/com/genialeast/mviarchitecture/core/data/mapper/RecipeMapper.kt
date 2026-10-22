package com.genialeast.mviarchitecture.core.data.mapper

import com.genialeast.mviarchitecture.core.data.R
import com.genialeast.mviarchitecture.core.data.model.NewsArticle
import com.genialeast.mviarchitecture.core.network.model.RecipeDto

/**
 * 把测试接口的食谱条目映射成资讯模型。封面优先用网络图，本地图只作加载失败时的底图。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
fun RecipeDto.toArticle(): NewsArticle {
    val title = name.trim().ifBlank { headline.trim() }
    val summary = headline.trim().ifBlank { description.trim().take(80) }
    val duration = time.toReadableDuration()
    val body = buildString {
        append(description.trim())
        if (calories.isNotBlank()) {
            append("\n\n热量  ").append(calories.trim())
        }
        if (duration.isNotBlank()) {
            append("\n用时  ").append(duration)
        }
    }
    return NewsArticle(
        id = id.trim(),
        title = title,
        summary = summary,
        content = body.ifBlank { summary },
        category = difficulty.toDifficultyLabel(),
        source = "网络测试接口",
        timeLabel = duration.ifBlank { "网络" },
        coverRes = R.drawable.cover_remote,
        imageUrl = image.trim().ifBlank { thumb.trim() },
    )
}

private fun Int.toDifficultyLabel(): String = when (this) {
    0 -> "入门"
    1 -> "简单"
    2 -> "中等"
    else -> "进阶"
}

/** 接口里的用时是 ISO-8601 时长，例如 PT35M、PT1H20M。 */
private fun String.toReadableDuration(): String {
    if (!startsWith("PT")) return trim()
    val hours = HOURS.find(this)?.groupValues?.getOrNull(1)?.toIntOrNull()
    val minutes = MINUTES.find(this)?.groupValues?.getOrNull(1)?.toIntOrNull()
    return buildString {
        if (hours != null && hours > 0) append(hours).append(" 小时")
        if (minutes != null && minutes > 0) {
            if (isNotEmpty()) append(" ")
            append(minutes).append(" 分钟")
        }
    }.ifBlank { trim() }
}

private val HOURS = Regex("(\\d+)H")
private val MINUTES = Regex("(\\d+)M")
