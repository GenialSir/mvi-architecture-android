package com.genialeast.mviarchitecture.core.data.local

import com.genialeast.mviarchitecture.core.network.model.NewsDto

/**
 * 本地资讯目录。网络数据源失败，或用户主动选择本地时，仓库读这份目录。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
object LocalNewsCatalog {

    val dtos: List<NewsDto> = listOf(
        NewsDto(
            id = "n1",
            title = "单向数据流：界面为什么变得可预测",
            summary = "用户操作先变成 Intent，ViewModel 算出下一份 State，Compose 只负责把状态画出来。",
            content = "点击、输入、下拉刷新都不是直接改界面。它们先成为 Intent，进入对应页面的 ViewModel。Reducer 用上一份不可变状态算出下一份，StateFlow 再通知 Compose 重组。因此同一份状态无论谁来读，画面都一样。",
            category = "架构",
            source = "演练编辑部",
            publishedAt = "今天 08:30",
            coverKey = "flow",
        ),
        NewsDto(
            id = "n2",
            title = "状态和副作用为什么要分开",
            summary = "列表、加载中、错误文案可以重放；Toast 和页面跳转只能消费一次，所以放进 Effect。",
            content = "收藏与否、当前分类、搜索词都属于状态，旋转屏幕后还应该在。打开详情、弹出「已更新」只该发生一次，所以走 Effect。把跳转写进状态，返回页面时会再次触发。",
            category = "架构",
            source = "演练编辑部",
            publishedAt = "今天 09:10",
            coverKey = "effect",
        ),
        NewsDto(
            id = "n3",
            title = "城市清晨的阅读节奏",
            summary = "首页放精选封面，新闻页放完整列表。两个入口读的是同一份本地目录。",
            content = "首页只取目录前三条，新闻页拿全部，再按分类和关键字筛。封面由 Coil 加载：现在是本地 drawable，模型里的 imageUrl 一旦有值，同一组件就会去拉网络图。",
            category = "城市",
            source = "晨报样本",
            publishedAt = "今天 07:20",
            coverKey = "city",
        ),
        NewsDto(
            id = "n4",
            title = "本地测试账号如何撑起登录演练",
            summary = "reader 与 editor 写在本地名单里，校验通过后把会话放进 DataStore。",
            content = "登录仓库不访问网络。它核对内置账号，成功后写入会话 DataStore。冷启动只问仓库还有没有会话，从而决定进入登录页还是主壳。两个账号切换时会清空收藏，避免串数据。",
            category = "专栏",
            source = "演练编辑部",
            publishedAt = "昨天 18:00",
            coverKey = "account",
        ),
        NewsDto(
            id = "n5",
            title = "底部三个入口各自负责什么",
            summary = "首页看精选，新闻看列表，我的看当前会话和收藏。Tab 本身也是一份状态。",
            content = "底部选中项放在主壳的 UiState 里，而不是三条导航路由。切走再切回来，新闻页自己的搜索词和分类仍由它的 ViewModel 保留，因为 ViewModel 挂在主壳这个导航目的地上。",
            category = "互动",
            source = "读者来信",
            publishedAt = "昨天 15:40",
            coverKey = "tabs",
        ),
        NewsDto(
            id = "n6",
            title = "网络层已经接好，数据仍走本地",
            summary = "OkHttp、Retrofit、Moshi 按 RobotGo 的方式放在 core:network。列表当前仍映射本地 DTO。",
            content = "网络数据来自公开测试接口 recipes.json。这条是本地目录里的样本，网络失败或手动切到本地时才会看到。",
            category = "预告",
            source = "演练编辑部",
            publishedAt = "昨天 11:05",
            coverKey = "remote",
        ),
    )
}
