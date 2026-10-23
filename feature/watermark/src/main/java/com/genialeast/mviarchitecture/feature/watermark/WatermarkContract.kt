package com.genialeast.mviarchitecture.feature.watermark

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * 水印页状态。原图地址、输入文字和生成结果都从这里渲染。
 *
 * @author GenialSir
 * @date 2026-10-23
 */
@Immutable
data class WatermarkUiState(
    val sourceUri: String? = null,
    val text: String = "",
    val preview: ImageBitmap? = null,
    val isProcessing: Boolean = false,
    val errorMessage: String = "",
)

/**
 * 水印页意图。选图、改字、生成和保存都从这里进入 ViewModel。
 *
 * @author GenialSir
 * @date 2026-10-23
 */
sealed interface WatermarkIntent {
    data class ImageSelected(val uri: String) : WatermarkIntent
    data class TextChanged(val value: String) : WatermarkIntent
    data object Apply : WatermarkIntent
    data object Save : WatermarkIntent
    /** Android 9 及以下拿到存储权限后再保存。 */
    data object SaveGranted : WatermarkIntent
}

/**
 * 水印页一次性副作用。权限申请和保存结果提示不写进状态。
 *
 * @author GenialSir
 * @date 2026-10-23
 */
sealed interface WatermarkEffect {
    data object RequestWritePermission : WatermarkEffect
    data class ShowMessage(val message: String) : WatermarkEffect
}
