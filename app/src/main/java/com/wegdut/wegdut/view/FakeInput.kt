package com.wegdut.wegdut.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.wegdut.wegdut.R

class FakeInput(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint: Paint
    var text: String = ""
        set(value) {
            field = value
            invalidate()
        }
    private val drawable: Drawable
    private val textBounds = Rect()

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FakeInput)
        val color = ta.getColor(R.styleable.FakeInput_color, 0)
        val size = ta.getDimensionPixelSize(R.styleable.FakeInput_size, 0);
        text = ta.getString(R.styleable.FakeInput_text)!!
        drawable = ta.getDrawable(R.styleable.FakeInput_icon)!!
        val iconSize = ta.getDimensionPixelSize(R.styleable.FakeInput_iconSize, size)
        ta.recycle()
        val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        drawable.colorFilter = colorFilter
        var w = drawable.intrinsicWidth
        var h = drawable.intrinsicHeight
        w = w * iconSize / h
        h = iconSize
        drawable.setBounds(0, 0, w, h)
        paint = Paint()
        paint.isAntiAlias = true
        paint.color = color
        paint.textSize = size.toFloat()
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        val dr = drawable.bounds
        val dt = (height - dr.height()) / 2f
        canvas.translate(paddingLeft.toFloat(), dt)
        drawable.draw(canvas)
        canvas.restore()
        paint.getTextBounds(text, 0, text.length, textBounds)
        val tl = paddingLeft + dr.width() + paint.textSize * 0.5f
        canvas.drawText(text, tl, (height + textBounds.height()) / 2f - textBounds.bottom, paint)
    }
}