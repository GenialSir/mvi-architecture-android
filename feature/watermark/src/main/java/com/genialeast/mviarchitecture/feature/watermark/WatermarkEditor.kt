package com.genialeast.mviarchitecture.feature.watermark

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 把相册相片解码、铺上文字水印，并保存到系统相册。
 *
 * @author GenialSir
 * @date 2026-10-23
 */
@Singleton
class WatermarkEditor @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /**
     * 读取相片并铺上斜向重复文字。文字为空时不绘制。
     */
    fun render(source: Uri, text: String): Bitmap {
        val photo = decode(source)
        val watermark = text.trim()
        if (watermark.isEmpty()) return photo
        val output = photo.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 150
            textAlign = Paint.Align.CENTER
            textSize = output.width.coerceAtMost(output.height) / 14f
            setShadowLayer(6f, 0f, 2f, Color.argb(180, 0, 0, 0))
        }
        canvas.save()
        // 绕画面中心旋转，让文字沿对角线重复，看起来是水印而不是一行标题。
        canvas.rotate(-28f, output.width / 2f, output.height / 2f)
        val stepX = paint.measureText(watermark) + paint.textSize * 2f
        val stepY = paint.textSize * 3.2f
        var y = -output.height.toFloat()
        while (y < output.height * 2f) {
            var x = -output.width.toFloat()
            while (x < output.width * 2f) {
                canvas.drawText(watermark, x, y, paint)
                x += stepX
            }
            y += stepY
        }
        canvas.restore()
        if (output != photo) {
            photo.recycle()
        }
        return output
    }

    /** 写入系统相册 Pictures/资讯演练。返回可展示的文案。 */
    fun save(bitmap: Bitmap): String {
        val resolver = context.contentResolver
        val name = "watermark_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/资讯演练",
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: throw IOException("无法写入相册")
        resolver.openOutputStream(uri)?.use { stream ->
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 92, stream)) {
                throw IOException("图片压缩失败")
            }
        } ?: throw IOException("无法打开相册输出")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
        return "已保存到相册 / 资讯演练"
    }

    private fun decode(source: Uri): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(source)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, bounds)
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw IOException("无法读取这张相片")
        }
        val sample = calculateSampleSize(bounds.outWidth, bounds.outHeight, MAX_EDGE)
        val options = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val decoded = context.contentResolver.openInputStream(source)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        } ?: throw IOException("无法读取这张相片")
        val orientation = context.contentResolver.openInputStream(source)?.use { stream ->
            ExifInterface(stream).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL,
            )
        } ?: ExifInterface.ORIENTATION_NORMAL
        return rotate(decoded, orientation)
    }

    private fun rotate(bitmap: Bitmap, orientation: Int): Bitmap {
        val degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> return bitmap
        }
        val matrix = Matrix().apply { postRotate(degrees) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated != bitmap) bitmap.recycle()
        return rotated
    }

    private fun calculateSampleSize(width: Int, height: Int, maxEdge: Int): Int {
        var sample = 1
        while (width / sample > maxEdge || height / sample > maxEdge) {
            sample *= 2
        }
        return sample
    }

    private companion object {
        const val MAX_EDGE = 2048
    }
}
