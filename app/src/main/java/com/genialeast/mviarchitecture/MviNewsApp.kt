package com.genialeast.mviarchitecture

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.genialeast.mviarchitecture.core.network.NetworkEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient

/**
 * 资讯演练应用入口。Hilt 管依赖图，Coil 复用网络层的 OkHttpClient 加载封面。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@HiltAndroidApp
class MviNewsApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        // 首次请求图片时才创建。这时 Hilt 已经就绪，从入口取出同一份 OkHttpClient。
        val okHttpClient: OkHttpClient = EntryPointAccessors.fromApplication(
            this,
            NetworkEntryPoint::class.java,
        ).okHttpClient()
        return ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .crossfade(true)
            .build()
    }
}
