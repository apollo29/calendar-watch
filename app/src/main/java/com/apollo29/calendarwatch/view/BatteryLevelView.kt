package com.apollo29.calendarwatch.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.internal.view.SupportMenu
import com.apollo29.calendarwatch.R

/* loaded from: classes.dex */
class BatteryLevelView : View {
    private var mArcChargingPaint: Paint? = null
    private var mArcDisabledPaint: Paint? = null
    private var mArcPaint: Paint? = null
    private var mBatteryLevelTextPaint: TextPaint? = null
    private var mPercentTextPaint: TextPaint? = null
    private var mTextPaint: TextPaint? = null
    private var mViewType = 0
    private var ARC_STROKE_WIDTH = 0.0f
    private var mBatteryLevel = 100
    private var mConnectionStatus = true

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    @SuppressLint("RestrictedApi")
    private fun init(attrs: AttributeSet?, defStyle: Int) {
        ARC_STROKE_WIDTH = resources.getDimension(R.dimen.battery_level_stroke_width)
        mBatteryLevelTextPaint = TextPaint()
        mBatteryLevelTextPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mBatteryLevelTextPaint!!.textAlign = Paint.Align.LEFT
        mBatteryLevelTextPaint!!.textSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 89.0f, resources.displayMetrics)
        mBatteryLevelTextPaint!!.color = resources.getColor(R.color.battery_level_text_color, null)
        mBatteryLevelTextPaint!!.typeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)
        mTextPaint = TextPaint()
        mTextPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mTextPaint!!.textAlign = Paint.Align.LEFT
        mTextPaint!!.textSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.0f, resources.displayMetrics)
        mTextPaint!!.color = resources.getColor(R.color.battery_level_text_color, null)
        mTextPaint!!.typeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)
        mPercentTextPaint = TextPaint()
        mPercentTextPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mPercentTextPaint!!.textAlign = Paint.Align.LEFT
        mPercentTextPaint!!.textSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30.0f, resources.displayMetrics)
        mPercentTextPaint!!.color = resources.getColor(R.color.battery_level_text_color, null)
        mPercentTextPaint!!.typeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)
        mArcPaint = Paint()
        mArcPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mArcPaint!!.style = Paint.Style.STROKE
        mArcPaint!!.strokeWidth = ARC_STROKE_WIDTH
        mArcPaint!!.color = SupportMenu.CATEGORY_MASK
        mArcPaint!!.strokeCap = Paint.Cap.ROUND
        mArcPaint!!.setShadowLayer(
            resources.getDimension(R.dimen.battery_level_shadow_size),
            0.0f,
            0.0f,
            -1711276033
        )
        setLayerType(LAYER_TYPE_SOFTWARE, mArcPaint)
        mArcDisabledPaint = Paint()
        mArcDisabledPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mArcDisabledPaint!!.style = Paint.Style.STROKE
        mArcDisabledPaint!!.strokeWidth = ARC_STROKE_WIDTH
        mArcDisabledPaint!!.color = resources.getColor(R.color.battery_level_disable, null)
        mArcChargingPaint = Paint()
        mArcChargingPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mArcChargingPaint!!.style = Paint.Style.STROKE
        mArcChargingPaint!!.strokeWidth = ARC_STROKE_WIDTH
        mArcChargingPaint!!.color = resources.getColor(R.color.battery_level_charging, null)
        mArcChargingPaint!!.setShadowLayer(
            resources.getDimension(R.dimen.battery_level_shadow_size),
            0.0f,
            0.0f,
            1157627903
        )
        setLayerType(LAYER_TYPE_SOFTWARE, mArcChargingPaint)
        setWillNotDraw(false)
    }

    fun setBatteryLevel(level: Int, connected: Boolean) {
        mBatteryLevel = level
        mConnectionStatus = connected
        invalidate()
    }

    fun setViewType(viewType: Int) {
        mViewType = viewType
        invalidate()
    }

    // android.view.View
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val bTextWidth: Float
        val grad: SweepGradient
        super.onDraw(canvas)
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom
        if (mViewType == 0 || mViewType == 1) {
            val batteryLevel = mBatteryLevel.toString()
            val levelTextWidth = mBatteryLevelTextPaint!!.measureText(batteryLevel)
            val levelTextHeight = mBatteryLevelTextPaint!!.fontMetrics.bottom
            val psTextWidth = mPercentTextPaint!!.measureText("%")
            bTextWidth = if (mConnectionStatus) {
                mTextPaint!!.measureText("BATTERY LEVEL")
            } else {
                mTextPaint!!.measureText("NOT CONNECTED")
            }
            val levelTextX =
                paddingLeft + (contentWidth - levelTextWidth - psTextWidth - 0.0f) / 2.0f
            val levelTextY =
                paddingTop + (contentHeight + levelTextHeight) / 2.0f + levelTextHeight / 2.0f
            val bTextX = paddingLeft + (contentWidth - bTextWidth) / 2.0f
            val bTextY = levelTextY + mTextPaint!!.fontMetrics.bottom * 5.0f
            canvas.drawText(batteryLevel, levelTextX, levelTextY, mBatteryLevelTextPaint!!)
            canvas.drawText(
                "%",
                levelTextX + levelTextWidth + 0.0f,
                levelTextY,
                mPercentTextPaint!!
            )
            if (mConnectionStatus) {
                canvas.drawText("BATTERY LEVEL", bTextX, bTextY, mTextPaint!!)
            } else {
                canvas.drawText("NOT CONNECTED", bTextX, bTextY, mTextPaint!!)
            }
            var startAngle = -90.0f + 3.6f * (100.0f - mBatteryLevel)
            val colors = intArrayOf(
                resources.getColor(R.color.battery_level_end, null),
                resources.getColor(R.color.battery_level_middle, null),
                resources.getColor(R.color.battery_level_start, null)
            )
            grad = if (mBatteryLevel < 5) {
                SweepGradient(
                    (width shr 1).toFloat(),
                    (width shr 1).toFloat(),
                    resources.getColor(R.color.battery_level_start, null),
                    resources.getColor(R.color.battery_level_start, null)
                )
            } else {
                SweepGradient(
                    (width shr 1).toFloat(),
                    (width shr 1).toFloat(),
                    colors,
                    null as FloatArray?
                )
            }
            val gradientMatrix = Matrix()
            grad.getLocalMatrix(gradientMatrix)
            gradientMatrix.preRotate(-90.0f, (width shr 1).toFloat(), (width shr 1).toFloat())
            grad.setLocalMatrix(gradientMatrix)
            mArcPaint!!.shader = grad
            canvas.drawArc(
                RectF(
                    paddingLeft + ARC_STROKE_WIDTH * 2.0f,
                    paddingTop + ARC_STROKE_WIDTH * 2.0f,
                    width - paddingRight - ARC_STROKE_WIDTH * 2.0f,
                    height - paddingBottom - ARC_STROKE_WIDTH * 2.0f
                ), 0.0f, 360.0f, false, mArcDisabledPaint!!
            )
            if (mBatteryLevel == 0) {
                canvas.drawArc(
                    RectF(
                        paddingLeft + ARC_STROKE_WIDTH * 2.0f,
                        paddingTop + ARC_STROKE_WIDTH * 2.0f,
                        width - paddingRight - ARC_STROKE_WIDTH * 2.0f,
                        height - paddingBottom - ARC_STROKE_WIDTH * 2.0f
                    ), startAngle, 270.0f - startAngle, false, mArcPaint!!
                )
                return
            }
            if (mBatteryLevel == 1) {
                startAngle = 262.8f
            }
            canvas.drawArc(
                RectF(
                    paddingLeft + ARC_STROKE_WIDTH * 2.0f,
                    paddingTop + ARC_STROKE_WIDTH * 2.0f,
                    width - paddingRight - ARC_STROKE_WIDTH * 2.0f,
                    height - paddingBottom - ARC_STROKE_WIDTH * 2.0f
                ), startAngle + 5.0f, 270.0f - startAngle - 10.0f, false, mArcPaint!!
            )
            return
        }
        canvas.drawArc(
            RectF(
                paddingLeft + ARC_STROKE_WIDTH * 2.0f,
                paddingTop + ARC_STROKE_WIDTH * 2.0f,
                width - paddingRight - ARC_STROKE_WIDTH * 2.0f,
                height - paddingBottom - ARC_STROKE_WIDTH * 2.0f
            ), 0.0f, 360.0f, false, mArcChargingPaint!!
        )
    }

    companion object {
        const val TYPE_CHARGING = 2
        const val TYPE_DISABLE = 1
        const val TYPE_ENABLE = 0
    }
}