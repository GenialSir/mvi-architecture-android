package com.genialeast.mviarchitecture.core.data.model

/**
 * 资讯数据来源。网络和本地是两条独立数据源，仓库决定读哪一条。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
enum class NewsSource(val label: String) {
    REMOTE("网络"),
    LOCAL("本地"),
}
