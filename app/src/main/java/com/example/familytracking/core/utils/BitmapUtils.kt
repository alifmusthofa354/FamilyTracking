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

        if (originalBitmap == null) {
            return getInitialBitmap(context, name, sizeDp)
        }

        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, sizePx, sizePx, false)
        val circularBitmap = getCircularBitmap(scaledBitmap)
        return addTrianglePointer(circularBitmap)
    }

    fun createMarkerWithText(context: Context, path: String?, name: String, placeholderResId: Int): Bitmap {
        // Note: placeholderResId is kept for signature compatibility but ignored in favor of Initials
        val baseMarker = getCircularBitmapFromPath(context, path, name, sizeDp = 40)
        // BaseMarker already has pointer from getCircularBitmapFromPath -> addTrianglePointer
        // But wait, getCircularBitmapFromPath calls addTrianglePointer at the end.
        // So baseMarker is ALREADY a pin.
        // We just need to add text below it.
        return drawTextOnMarker(baseMarker, name)
    }

    fun createMarkerWithBitmap(context: Context, bitmap: Bitmap?, name: String, placeholderResId: Int): Bitmap {
        val sizePx = (40 * context.resources.displayMetrics.density).toInt()
        val baseBitmap = if (bitmap != null) {
             val scaled = Bitmap.createScaledBitmap(bitmap, sizePx, sizePx, false)
             getCircularBitmap(scaled)
        } else {
             // Fallback to initial bitmap (which is circular)
             getInitialBitmap(context, name, 40)
        }
        val pointerMarker = addTrianglePointer(baseBitmap)
        return drawTextOnMarker(pointerMarker, name)
    }

    fun getInitialBitmap(context: Context, name: String, sizeDp: Int): Bitmap {
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        val output = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        val initial = if (name.isNotEmpty()) name.take(1).uppercase() else "?"
        
        val colors = listOf("#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A6F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722")
        val colorIndex = Math.abs(name.hashCode()) % colors.size
        val bgColor = Color.parseColor(colors[colorIndex])

        val paint = Paint().apply {
            isAntiAlias = true
            color = bgColor
        }

        canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, paint)

        paint.apply {
            color = Color.WHITE
            textSize = sizePx * 0.5f
            textAlign = Paint.Align.CENTER
            // typeface = android.graphics.Typeface.DEFAULT_BOLD // Typeface not imported, use default
        }

        val textBounds = Rect()
        paint.getTextBounds(initial, 0, initial.length, textBounds)
        val textY = (sizePx / 2f) + (textBounds.height() / 2f)
        
        canvas.drawText(initial, sizePx / 2f, textY, paint)

        return output
    }

    private fun addTrianglePointer(bitmap: Bitmap): Bitmap {
        val pointerHeight = bitmap.height / 4
        val totalHeight = bitmap.height + pointerHeight
        val output = Bitmap.createBitmap(bitmap.width, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        canvas.drawBitmap(bitmap, 0f, 0f, null)

        val paint = Paint()
        paint.color = Color.WHITE 
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true

        val path = android.graphics.Path()
        val centerX = bitmap.width / 2f
        val bottomY = bitmap.height.toFloat()
        
        path.moveTo(centerX - (pointerHeight / 1.5f), bottomY - 5f) 
        path.lineTo(centerX + (pointerHeight / 1.5f), bottomY - 5f) 
        path.lineTo(centerX, totalHeight.toFloat()) 
        path.close()

        canvas.drawPath(path, paint)

        return output
    }

    private fun drawTextOnMarker(baseMarker: Bitmap, name: String): Bitmap {
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 30f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
        }

        val textBounds = Rect()
        paint.getTextBounds(name, 0, name.length, textBounds)

        val padding = 10f
        val labelWidth = textBounds.width() + (padding * 2)
        val labelHeight = textBounds.height() + (padding * 2)
        
        val totalWidth = maxOf(baseMarker.width.toFloat(), labelWidth)
        val totalHeight = baseMarker.height + labelHeight + 5f
        
        val output = Bitmap.createBitmap(totalWidth.toInt(), totalHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val markerX = (totalWidth - baseMarker.width) / 2f
        canvas.drawBitmap(baseMarker, markerX, 0f, null)

        val rectPaint = Paint().apply {
            color = Color.WHITE
            alpha = 200
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val labelRect = android.graphics.RectF(
            (totalWidth - labelWidth) / 2f,
            baseMarker.height.toFloat(),
            (totalWidth + labelWidth) / 2f,
            totalHeight
        )
        canvas.drawRoundRect(labelRect, 10f, 10f, rectPaint)

        canvas.drawText(
            name,
            totalWidth / 2f,
            baseMarker.height + padding + textBounds.height(),
            paint
        )

        return output
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f, bitmap.width / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        
        val borderPaint = Paint()
        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = Color.WHITE
        borderPaint.strokeWidth = 4f
        borderPaint.isAntiAlias = true
        canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f, (bitmap.width / 2f) - 2f, borderPaint)

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