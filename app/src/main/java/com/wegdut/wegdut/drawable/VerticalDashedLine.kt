package com.wegdut.wegdut.drawable

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.wegdut.wegdut.utils.UIUtils

class VerticalDashedLine(
    context: Context,
    @ColorRes color: Int,
    width: Float,
    size: Float,
    gap: Float
) : Drawable() {

    private val paint = Paint()

    init {
        val w = UIUtils.dp2px(context, width).toFloat()
        val s = UIUtils.dp2px(context, size).toFloat()
        val g = UIUtils.dp2px(context, gap).toFloat()
        paint.pathEffect = DashPathEffect(floatArrayOf(s, g), 0f)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = w
        paint.color = ResourcesCompat.getColor(context.resources, color, null)
    }


    override fun draw(canvas: Canvas) {
        val p = Path()
        val w = paint.strokeWidth
        p.moveTo(w / 2f, bounds.top.toFloat())
        p.lineTo(w / 2f, bounds.bottom.toFloat())
        canvas.drawPath(p, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}