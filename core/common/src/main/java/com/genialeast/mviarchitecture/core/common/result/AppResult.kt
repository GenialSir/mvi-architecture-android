package com.genialeast.mviarchitecture.core.common.result

/**
 * 仓库层统一结果。成功带数据，失败只带界面能直接展示的文案。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val message: String) : AppResult<Nothing>()
}
