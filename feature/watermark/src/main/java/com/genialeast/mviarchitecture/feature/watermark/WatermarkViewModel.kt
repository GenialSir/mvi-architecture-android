package com.genialeast.mviarchitecture.feature.watermark

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.genialeast.mviarchitecture.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 水印页 ViewModel。选中的相片和文字留在状态里，生成与保存交给 [WatermarkEditor]。
 *
 * @author GenialSir
 * @date 2026-10-23
 */
@HiltViewModel
class WatermarkViewModel @Inject constructor(
    private val watermarkEditor: WatermarkEditor,
) : MviViewModel<WatermarkUiState, WatermarkIntent, WatermarkEffect>(WatermarkUiState()) {

    /** 最近一次生成的位图，只用于保存，不暴露给界面直接改。 */
    private var rendered: Bitmap? = null

    override fun handleIntent(intent: WatermarkIntent) {
        when (intent) {
            is WatermarkIntent.ImageSelected -> {
                rendered = null
                setState {
                    copy(
                        sourceUri = intent.uri,
                        preview = null,
                        errorMessage = "",
                    )
                }
            }
            is WatermarkIntent.TextChanged -> setState {
                copy(text = intent.value, errorMessage = "")
            }
            WatermarkIntent.Apply -> apply()
            WatermarkIntent.Save -> requestSave()
            WatermarkIntent.SaveGranted -> save()
        }
    }

    private fun apply() {
        val uri = currentState.sourceUri
        val text = currentState.text.trim()
        if (uri.isNullOrBlank()) {
            setState { copy(errorMessage = "请先从相册选择一张相片") }
            return
        }
        if (text.isEmpty()) {
            setState { copy(errorMessage = "请输入要加到相片上的文字") }
            return
        }
        if (currentState.isProcessing) return
        setState { copy(isProcessing = true, errorMessage = "") }
        viewModelScope.launch {
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    watermarkEditor.render(Uri.parse(uri), text)
                }
            }
            result.onSuccess { bitmap ->
                rendered = bitmap
                setState {
                    copy(
                        isProcessing = false,
                        preview = bitmap.asImageBitmap(),
                    )
                }
            }.onFailure { error ->
                setState {
                    copy(
                        isProcessing = false,
                        errorMessage = error.message ?: "生成水印失败",
                    )
                }
            }
        }
    }

    private fun requestSave() {
        if (rendered == null) {
            setState { copy(errorMessage = "请先把文字添加到相片上") }
            return
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            sendEffect(WatermarkEffect.RequestWritePermission)
            return
        }
        save()
    }

    private fun save() {
        val bitmap = rendered ?: return
        if (currentState.isProcessing) return
        setState { copy(isProcessing = true, errorMessage = "") }
        viewModelScope.launch {
            val result = runCatching {
                withContext(Dispatchers.IO) { watermarkEditor.save(bitmap) }
            }
            result.onSuccess { message ->
                setState { copy(isProcessing = false) }
                sendEffect(WatermarkEffect.ShowMessage(message))
            }.onFailure { error ->
                setState {
                    copy(
                        isProcessing = false,
                        errorMessage = error.message ?: "保存失败",
                    )
                }
            }
        }
    }
}
