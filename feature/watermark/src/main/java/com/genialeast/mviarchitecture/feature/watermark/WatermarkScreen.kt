package com.genialeast.mviarchitecture.feature.watermark

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

/**
 * 水印页路由。相册选择留在界面，选中结果再变成 Intent。
 *
 * @author GenialSir
 * @date 2026-10-23
 */
@Composable
fun WatermarkRoute(
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: WatermarkViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            viewModel.onIntent(WatermarkIntent.ImageSelected(uri.toString()))
        }
    }
    val requestWrite = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            viewModel.onIntent(WatermarkIntent.SaveGranted)
        } else {
            onShowMessage("需要存储权限才能保存到相册")
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                WatermarkEffect.RequestWritePermission -> {
                    requestWrite.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                is WatermarkEffect.ShowMessage -> onShowMessage(effect.message)
            }
        }
    }

    WatermarkScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        onPickImage = {
            pickImage.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
    )
}

/**
 * 水印页。先选相片，再输入文字，生成后可以保存到相册。
 *
 * @author GenialSir
 * @date 2026-10-23
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatermarkScreen(
    state: WatermarkUiState,
    onIntent: (WatermarkIntent) -> Unit,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("添加水印") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            OutlinedButton(
                onClick = onPickImage,
                enabled = !state.isProcessing,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(if (state.sourceUri == null) "从相册选择相片" else "重新选择相片")
            }
            if (state.preview != null) {
                Image(
                    bitmap = state.preview,
                    contentDescription = "加上水印后的相片",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                )
            } else if (state.sourceUri != null) {
                AsyncImage(
                    model = state.sourceUri,
                    contentDescription = "选中的相片",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                )
            }
            OutlinedTextField(
                value = state.text,
                onValueChange = { onIntent(WatermarkIntent.TextChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = !state.isProcessing,
                singleLine = true,
                label = { Text("水印文字") },
                placeholder = { Text("输入要印在相片上的字") },
            )
            if (state.errorMessage.isNotBlank()) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Button(
                onClick = { onIntent(WatermarkIntent.Apply) },
                enabled = !state.isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                if (state.isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.heightIn(max = 22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("添加到相片")
                }
            }
            if (state.preview != null) {
                OutlinedButton(
                    onClick = { onIntent(WatermarkIntent.Save) },
                    enabled = !state.isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("保存到相册")
                }
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                Text(
                    text = "Android 9 及以下保存时会申请存储权限。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}
