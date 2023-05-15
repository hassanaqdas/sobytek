package com.sobytek.erpsobytek.utils

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.core.ViewFinderView
import me.dm7.barcodescanner.zxing.ZXingScannerView


class CustomScannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ZXingScannerView(context, attrs) {

    companion object {
        var mFramingRect: Rect? = null
        val PORTRAIT_WIDTH_RATIO = 0.75f
        val PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f
        val LANDSCAPE_HEIGHT_RATIO = 0.625f
        val LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f
        val MIN_DIMENSION_DIFF = 50
        val DEFAULT_SQUARE_DIMENSION_RATIO = 0.625f
        val SCANNER_ALPHA = intArrayOf(0, 64, 128, 192, 255, 192, 128, 64)
        var scannerAlpha = 0
        val POINT_SIZE = 10
        val ANIMATION_DELAY = 10L
        private var cntr = 0
        private var goingup = false
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context, attrs) {
    }

    override fun createViewFinderView(context: Context): IViewFinder {
        return CustomViewFinderView(context)
    }

    class CustomViewFinderView(context: Context) : ViewFinderView(context) {


        override fun drawLaser(canvas: Canvas) {
            mFramingRect = framingRect
            // Draw a red "laser scanner" line through the middle to show decoding is active
            mLaserPaint.alpha = SCANNER_ALPHA[scannerAlpha]
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.size
            if (mFramingRect != null) {
                var middle = mFramingRect!!.height() / 2 + mFramingRect!!.top
                middle = middle + cntr
                if (cntr < 210 && !goingup) {
                    canvas.drawRect(
                        (mFramingRect!!.left + 2).toFloat(),
                        (middle - 1).toFloat(),
                        (mFramingRect!!.right - 1).toFloat(),
                        (middle + 2).toFloat(),
                        mLaserPaint
                    )
                    cntr = cntr + 4
                }
                if (cntr >= 210 && !goingup) {
                    goingup = true
                }
                if (cntr > -210 && goingup) {
                    canvas.drawRect(
                        (mFramingRect!!.left + 2).toFloat(),
                        (middle - 1).toFloat(),
                        (mFramingRect!!.right - 1).toFloat(),
                        (middle + 2).toFloat(),
                        mLaserPaint
                    )
                    cntr = cntr - 4
                }
                if (cntr <= -210 && goingup) {
                    goingup = false
                }
                postInvalidateDelayed(
                    ANIMATION_DELAY,
                    mFramingRect!!.left - POINT_SIZE,
                    mFramingRect!!.top - POINT_SIZE,
                    mFramingRect!!.right + POINT_SIZE,
                    mFramingRect!!.bottom + POINT_SIZE
                )
            }
        }

        init {
            Log.d("TAG19", "CustomViewFinderView")
            setSquareViewFinder(true)
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            // DEFAULT SQUARE DIMENSION RATIO in ViewFinderView is 0.625
            // get appropriate Dimension ratio otherwise
            val width = displayMetrics.widthPixels * 0.610f
            setBorderLineLength(width.toInt())
        }
    }

}