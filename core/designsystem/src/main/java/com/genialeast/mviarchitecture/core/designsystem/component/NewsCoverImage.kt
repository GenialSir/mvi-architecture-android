package com.genialeast.mviarchitecture.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.genialeast.mviarchitecture.core.designsystem.R

/**
 * 资讯封面。Coil 同时接受 drawable 资源 id 和 http 地址，业务换远程图时不用改这个组件。
 *
 * 加载中的占位和失败图留在组件内部。这是视图细节，不进页面的 UiState。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Composable
fun NewsCoverImage(
    model: Any?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(model)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        placeholder = painterResource(R.drawable.cover_placeholder),
        error = painterResource(R.drawable.cover_placeholder),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    )
}
