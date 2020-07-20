package com.example.coordinatorapp.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import com.example.coordinatorapp.R
import com.example.coordinatorapp.extensions.dpToPx

class AvatarImageViewMask @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?  = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

    private val defaultWidth: Float
        get() = context.dpToPx(DEFAULT_BORDER_WIDTH)

    @Px
    var borderWidth = defaultWidth
    @ColorInt
    private var borderColor: Int = Color.WHITE
    private var initials: String = "??"

    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()
    private lateinit var resultBt: Bitmap
    private lateinit var maskBt: Bitmap
    private lateinit var srcBt: Bitmap

    init {
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageViewMask)
            borderWidth = ta.getDimension(
                R.styleable.AvatarImageViewMask_aivm_borderWidth, defaultWidth
            )
            borderColor = ta.getColor(
                R.styleable.AvatarImageViewMask_aivm_borderColor, DEFAULT_BORDER_COLOR
            )
            initials = ta.getString(R.styleable.AvatarImageViewMask_aivm_initials) ?: "??"
            ta.recycle()
        }

        scaleType = ScaleType.CENTER_CROP
        setup()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(initSize, initSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e("AvatarImageView", "onSizeChanged")
        if (w == 0)return
        with(viewRect) {
            left = 0
            top = 0
            right = w
            bottom = h
        }
        prepareBitmaps(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        Log.e("AvatarImageView", "onDraw")
        canvas.drawBitmap(resultBt, viewRect, viewRect, null)

        val half = (borderWidth / 2).toInt()
        viewRect.inset(half, half)

        canvas.drawOval(viewRect.toRectF(), borderPaint)
    }

    private fun setup() {
        with(maskPaint) {
            color = Color.RED
            style = Paint.Style.FILL
        }
        with(borderPaint) {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderColor
        }
    }

    private fun prepareBitmaps(w: Int, h: Int) {
        maskBt = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
        resultBt = maskBt.copy(Bitmap.Config.ARGB_8888, true)
        val maskCanvas = Canvas(maskBt)
        maskCanvas.drawOval(viewRect.toRectF(), maskPaint)
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        srcBt = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)

        val resultCanvas = Canvas(resultBt)
        resultCanvas.drawBitmap(maskBt, viewRect, viewRect, null)
        resultCanvas.drawBitmap(srcBt, viewRect, viewRect, maskPaint)
    }

    private fun resolveDefaultSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> defaultWidth.toInt()
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }
}