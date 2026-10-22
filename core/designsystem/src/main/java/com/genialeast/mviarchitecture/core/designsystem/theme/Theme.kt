package com.genialeast.mviarchitecture.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * 资讯演练主题。关闭动态取色，保证教学界面在不同设备上颜色一致。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
private val LightColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = CardWhite,
    secondary = Vermilion,
    onSecondary = CardWhite,
    background = Paper,
    onBackground = Ink,
    surface = CardWhite,
    onSurface = Ink,
    onSurfaceVariant = InkSoft,
    outline = InkSoft,
)

@Composable
fun NewsTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
