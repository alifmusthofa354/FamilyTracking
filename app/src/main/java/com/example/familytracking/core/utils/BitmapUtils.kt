package com.example.familytracking.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.File

object BitmapUtils {

    fun getCircularBitmapFromPath(context: Context, path: String?, name: String, sizeDp: Int = 48): Bitmap {
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        
        var originalBitmap: Bitmap? = null
        if (path != null) {
            val file = File(path)
            if (file.exists()) {
                try {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(path, options)
                    options.inSampleSize = calculateInSampleSize(options, sizePx, sizePx)
                    options.inJustDecodeBounds = false
                    originalBitmap = BitmapFactory.decodeFile(path, options)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val baseBitmap = if (originalBitmap == null) {
            getInitialBitmap(context, name, sizeDp)
        } else {
            Bitmap.createScaledBitmap(originalBitmap, sizePx, sizePx, false)
        }

        return createPinMarker(context, baseBitmap)
    }

    fun createMarkerWithText(context: Context, path: String?, name: String, placeholderResId: Int): Bitmap {
        return getCircularBitmapFromPath(context, path, name, sizeDp = 40)
    }

    fun createMarkerWithBitmap(context: Context, bitmap: Bitmap?, name: String, placeholderResId: Int): Bitmap {
        val sizePx = (40 * context.resources.displayMetrics.density).toInt()
        val baseBitmap = if (bitmap != null) {
             Bitmap.createScaledBitmap(bitmap, sizePx, sizePx, false)
        } else {
             getInitialBitmap(context, name, 40)
        }
        return createPinMarker(context, baseBitmap)
    }

    private fun createPinMarker(context: Context, contentBitmap: Bitmap): Bitmap {
        val density = context.resources.displayMetrics.density
        val strokeWidth = 3 * density
        val pointerHeight = contentBitmap.height / 3f
        val totalHeight = contentBitmap.height + pointerHeight + strokeWidth
        val width = contentBitmap.width + (strokeWidth * 2)
        
        val output = Bitmap.createBitmap(width.toInt(), totalHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 1. Draw the "Pin Shell" (Background & Pointer)
        paint.color = Color.WHITE
        val centerX = width / 2f
        val circleCenterY = (contentBitmap.height / 2f) + strokeWidth
        val circleRadius = (contentBitmap.width / 2f) + strokeWidth

        // Draw Circle of the pin
        canvas.drawCircle(centerX, circleCenterY, circleRadius, paint)

        // Draw Triangle pointer (seamlessly attached)
        val path = android.graphics.Path()
        path.moveTo(centerX - (circleRadius * 0.7f), circleCenterY + (circleRadius * 0.6f)) // Left connection
        path.lineTo(centerX + (circleRadius * 0.7f), circleCenterY + (circleRadius * 0.6f)) // Right connection
        path.lineTo(centerX, totalHeight) // Bottom Tip
        path.close()
        canvas.drawPath(path, paint)

        // 2. Draw the Image (Circle Cropped) inside the shell
        val circularImage = getCircularBitmap(contentBitmap)
        canvas.drawBitmap(circularImage, strokeWidth, strokeWidth, null)

        return output
    }

    fun getInitialBitmap(context: Context, name: String, sizeDp: Int): Bitmap {
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        val output = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        val initial = if (name.isNotEmpty()) name.take(1).uppercase() else "?"
        val colors = listOf("#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A6F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722")
        val colorIndex = Math.abs(name.hashCode()) % colors.size
        val bgColor = Color.parseColor(colors[colorIndex])

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = bgColor
        canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, paint)

        paint.color = Color.WHITE
        paint.textSize = sizePx * 0.5f
        paint.textAlign = Paint.Align.CENTER
        
        val textBounds = Rect()
        paint.getTextBounds(initial, 0, initial.length, textBounds)
        canvas.drawText(initial, sizePx / 2f, (sizePx / 2f) + (textBounds.height() / 2f), paint)

        return output
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f, bitmap.width / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}