package com.genialeast.mviarchitecture.core.common.constant

/**
 * 全工程共用常量。网络超时和占位地址放在这里，避免各模块各写一份。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
object AppConstant {
    /**
     * 与 MvvmAndroidArchitecture 相同的公开测试地址。
     * 新闻网络数据源请求这个地址下的 recipes.json。
     */
    const val API_BASE_URL = "https://hf-android-app.s3-eu-west-1.amazonaws.com/android-test/"
    const val API_TIMEOUT_MS = 15_000L
}
