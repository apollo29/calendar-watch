package com.whatcalendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import androidx.core.internal.view.SupportMenu;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.whatcalendar.R;

/* loaded from: classes.dex */
public class BatteryLevelView extends View {
    public static final int TYPE_CHARGING = 2;
    public static final int TYPE_DISABLE = 1;
    public static final int TYPE_ENABLE = 0;
    private Paint mArcChargingPaint;
    private Paint mArcDisabledPaint;
    private Paint mArcPaint;
    private TextPaint mBatteryLevelTextPaint;
    private TextPaint mPercentTextPaint;
    private TextPaint mTextPaint;
    private int mViewType;
    private float ARC_STROKE_WIDTH = 0.0f;
    private int mBatteryLevel = 100;
    private boolean mConnectionStatus = true;

    public BatteryLevelView(Context context) {
        super(context);
        init(null, 0);
    }

    public BatteryLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BatteryLevelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        this.ARC_STROKE_WIDTH = getResources().getDimension(R.dimen.battery_level_stroke_width);
        this.mBatteryLevelTextPaint = new TextPaint();
        this.mBatteryLevelTextPaint.setFlags(1);
        this.mBatteryLevelTextPaint.setTextAlign(Paint.Align.LEFT);
        this.mBatteryLevelTextPaint.setTextSize(TypedValue.applyDimension(1, 89.0f, getResources().getDisplayMetrics()));
        this.mBatteryLevelTextPaint.setColor(getResources().getColor(R.color.battery_level_text_color));
        this.mBatteryLevelTextPaint.setTypeface(Typeface.create("sans-serif-thin", 0));
        this.mTextPaint = new TextPaint();
        this.mTextPaint.setFlags(1);
        this.mTextPaint.setTextAlign(Paint.Align.LEFT);
        this.mTextPaint.setTextSize(TypedValue.applyDimension(1, 20.0f, getResources().getDisplayMetrics()));
        this.mTextPaint.setColor(getResources().getColor(R.color.battery_level_text_color));
        this.mTextPaint.setTypeface(Typeface.create("sans-serif-thin", 0));
        this.mPercentTextPaint = new TextPaint();
        this.mPercentTextPaint.setFlags(1);
        this.mPercentTextPaint.setTextAlign(Paint.Align.LEFT);
        this.mPercentTextPaint.setTextSize(TypedValue.applyDimension(1, 30.0f, getResources().getDisplayMetrics()));
        this.mPercentTextPaint.setColor(getResources().getColor(R.color.battery_level_text_color));
        this.mPercentTextPaint.setTypeface(Typeface.create("sans-serif-thin", 0));
        this.mArcPaint = new Paint();
        this.mArcPaint.setFlags(1);
        this.mArcPaint.setStyle(Paint.Style.STROKE);
        this.mArcPaint.setStrokeWidth(this.ARC_STROKE_WIDTH);
        this.mArcPaint.setColor(SupportMenu.CATEGORY_MASK);
        this.mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mArcPaint.setShadowLayer(getResources().getDimension(R.dimen.battery_level_shadow_size), 0.0f, 0.0f, -1711276033);
        setLayerType(1, this.mArcPaint);
        this.mArcDisabledPaint = new Paint();
        this.mArcDisabledPaint.setFlags(1);
        this.mArcDisabledPaint.setStyle(Paint.Style.STROKE);
        this.mArcDisabledPaint.setStrokeWidth(this.ARC_STROKE_WIDTH);
        this.mArcDisabledPaint.setColor(getResources().getColor(R.color.battery_level_disable));
        this.mArcChargingPaint = new Paint();
        this.mArcChargingPaint.setFlags(1);
        this.mArcChargingPaint.setStyle(Paint.Style.STROKE);
        this.mArcChargingPaint.setStrokeWidth(this.ARC_STROKE_WIDTH);
        this.mArcChargingPaint.setColor(getResources().getColor(R.color.battery_level_charging));
        this.mArcChargingPaint.setShadowLayer(getResources().getDimension(R.dimen.battery_level_shadow_size), 0.0f, 0.0f, 1157627903);
        setLayerType(1, this.mArcChargingPaint);
        setWillNotDraw(false);
    }

    public void setBatteryLevel(int level, boolean connected) {
        this.mBatteryLevel = level;
        this.mConnectionStatus = connected;
        invalidate();
    }

    public void setViewType(int viewType) {
        this.mViewType = viewType;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float bTextWidth;
        SweepGradient grad;
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int contentWidth = (getWidth() - paddingLeft) - paddingRight;
        int contentHeight = (getHeight() - paddingTop) - paddingBottom;
        if (this.mViewType == 0 || this.mViewType == 1) {
            String batteryLevel = String.valueOf(this.mBatteryLevel);
            float levelTextWidth = this.mBatteryLevelTextPaint.measureText(batteryLevel);
            float levelTextHeight = this.mBatteryLevelTextPaint.getFontMetrics().bottom;
            float psTextWidth = this.mPercentTextPaint.measureText("%");
            if (this.mConnectionStatus) {
                bTextWidth = this.mTextPaint.measureText("BATTERY LEVEL");
            } else {
                bTextWidth = this.mTextPaint.measureText("NOT CONNECTED");
            }
            float levelTextX = paddingLeft + ((((contentWidth - levelTextWidth) - psTextWidth) - 0.0f) / 2.0f);
            float levelTextY = paddingTop + ((contentHeight + levelTextHeight) / 2.0f) + (levelTextHeight / 2.0f);
            float bTextX = paddingLeft + ((contentWidth - bTextWidth) / 2.0f);
            float bTextY = levelTextY + (this.mTextPaint.getFontMetrics().bottom * 5.0f);
            canvas.drawText(batteryLevel, levelTextX, levelTextY, this.mBatteryLevelTextPaint);
            canvas.drawText("%", levelTextX + levelTextWidth + 0.0f, levelTextY, this.mPercentTextPaint);
            if (this.mConnectionStatus) {
                canvas.drawText("BATTERY LEVEL", bTextX, bTextY, this.mTextPaint);
            } else {
                canvas.drawText("NOT CONNECTED", bTextX, bTextY, this.mTextPaint);
            }
            float startAngle = (-90.0f) + (3.6f * (100.0f - this.mBatteryLevel));
            int[] colors = {getResources().getColor(R.color.battery_level_end), getResources().getColor(R.color.battery_level_middle), getResources().getColor(R.color.battery_level_start)};
            if (this.mBatteryLevel < 5) {
                grad = new SweepGradient(getWidth() >> 1, getHeight() >> 1, getResources().getColor(R.color.battery_level_start), getResources().getColor(R.color.battery_level_start));
            } else {
                grad = new SweepGradient(getWidth() >> 1, getHeight() >> 1, colors, (float[]) null);
            }
            Matrix gradientMatrix = new Matrix();
            grad.getLocalMatrix(gradientMatrix);
            gradientMatrix.preRotate(-90.0f, getWidth() >> 1, getHeight() >> 1);
            grad.setLocalMatrix(gradientMatrix);
            this.mArcPaint.setShader(grad);
            canvas.drawArc(new RectF(paddingLeft + (this.ARC_STROKE_WIDTH * 2.0f), paddingTop + (this.ARC_STROKE_WIDTH * 2.0f), (getWidth() - paddingRight) - (this.ARC_STROKE_WIDTH * 2.0f), (getHeight() - paddingBottom) - (this.ARC_STROKE_WIDTH * 2.0f)), 0.0f, 360.0f, false, this.mArcDisabledPaint);
            if (this.mBatteryLevel == 0) {
                canvas.drawArc(new RectF(paddingLeft + (this.ARC_STROKE_WIDTH * 2.0f), paddingTop + (this.ARC_STROKE_WIDTH * 2.0f), (getWidth() - paddingRight) - (this.ARC_STROKE_WIDTH * 2.0f), (getHeight() - paddingBottom) - (this.ARC_STROKE_WIDTH * 2.0f)), startAngle, 270.0f - startAngle, false, this.mArcPaint);
                return;
            }
            if (this.mBatteryLevel == 1) {
                startAngle = 262.8f;
            }
            canvas.drawArc(new RectF(paddingLeft + (this.ARC_STROKE_WIDTH * 2.0f), paddingTop + (this.ARC_STROKE_WIDTH * 2.0f), (getWidth() - paddingRight) - (this.ARC_STROKE_WIDTH * 2.0f), (getHeight() - paddingBottom) - (this.ARC_STROKE_WIDTH * 2.0f)), startAngle + 5.0f, (270.0f - startAngle) - 10.0f, false, this.mArcPaint);
            return;
        }
        canvas.drawArc(new RectF(paddingLeft + (this.ARC_STROKE_WIDTH * 2.0f), paddingTop + (this.ARC_STROKE_WIDTH * 2.0f), (getWidth() - paddingRight) - (this.ARC_STROKE_WIDTH * 2.0f), (getHeight() - paddingBottom) - (this.ARC_STROKE_WIDTH * 2.0f)), 0.0f, 360.0f, false, this.mArcChargingPaint);
    }
}
