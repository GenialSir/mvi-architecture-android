package com.genialeast.mviarchitecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.genialeast.mviarchitecture.core.designsystem.theme.NewsTheme
import com.genialeast.mviarchitecture.navigation.NewsNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * 应用唯一 Activity。界面入口交给 [NewsNavHost]，自身不持有页面状态。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsTheme {
                NewsNavHost()
            }
        }
    }
}
