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

    fun getCircularBitmapFromPath(context: Context, path: String?, placeholderResId: Int, sizeDp: Int = 48): Bitmap {
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        
        // 1. Try load from path
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

        // 2. Fallback to placeholder if failed
        if (originalBitmap == null) {
            val drawable: Drawable? = ContextCompat.getDrawable(context, placeholderResId)
            originalBitmap = drawable?.toBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        }

        // 3. Resize and Circle Crop
        val scaledBitmap = originalBitmap?.let { 
            Bitmap.createScaledBitmap(it, sizePx, sizePx, false) 
        } ?: return Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)

        val circularBitmap = getCircularBitmap(scaledBitmap)
        return addTrianglePointer(circularBitmap)
    }

    private fun addTrianglePointer(bitmap: Bitmap): Bitmap {
        val pointerHeight = bitmap.height / 4
        val totalHeight = bitmap.height + pointerHeight
        val output = Bitmap.createBitmap(bitmap.width, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        // Draw the circular image at the top
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        // Draw the triangle pointer at the bottom
        val paint = Paint()
        paint.color = Color.WHITE // Match border color
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true

        val path = android.graphics.Path()
        val centerX = bitmap.width / 2f
        val bottomY = bitmap.height.toFloat()
        
        // Triangle points
        path.moveTo(centerX - (pointerHeight / 1.5f), bottomY - 5f) // Top Left of triangle (overlapped slightly)
        path.lineTo(centerX + (pointerHeight / 1.5f), bottomY - 5f) // Top Right of triangle
        path.lineTo(centerX, totalHeight.toFloat()) // Bottom Tip
        path.close()

        canvas.drawPath(path, paint)

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
        
        // Optional: Add Border
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
