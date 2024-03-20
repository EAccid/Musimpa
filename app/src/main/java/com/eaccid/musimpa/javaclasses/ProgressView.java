package com.eaccid.musimpa.javaclasses;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import com.eaccid.musimpa.R;


/**
 * View shows progress (%)
 */

public class ProgressView extends View {

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String START_ANGLE = "start_angle";
    private static final String STROKE_WIDTH = "stroke_width";
    private static final String PROGRESS = "progress";
    private static final double ANGLE_EPSILON = 1;
    private static final int DEFAULT_SHIFT_ANGLE = 200;
    private static final int DEFAULT_START_ANGLE = 0;
    private static final int DEFAULT_STROKE_WIDTH = 8;
    private static final int DEFAULT_PROGRESS = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_TEXT_SIZE = 50;

    final private RectF unfinishedProgressRect = new RectF();
    final private RectF finishedProgressRect = new RectF();
    final private Paint paintFinishedProgress = new Paint();
    final private Paint paintUnfinishedProgress = new Paint();
    final private Paint paintInnerCircle = new Paint();
    final private Paint textPaint = new Paint();
    private ColorStateList backgroundColorStateList;
    private ColorStateList unfinishedStrokeColorStateList;
    private ColorStateList finishedStrokeColorStateList;
    private ColorStateList textColorStateList;

    private int maxValue; //%
    private int startAngle;
    private int strokeWidth;
    private int progress;
    private int textSize;
    private boolean showProgress;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromObtainedStyledAttributes(context, attrs);
        initPainters();
    }

    private void initFromObtainedStyledAttributes(Context context, AttributeSet attrs) {
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        setStrokeWidth(array.getInt(R.styleable.ProgressView_progress_view_progress_stroke_width, DEFAULT_STROKE_WIDTH));
        setTextSize(array.getDimensionPixelSize(R.styleable.ProgressView_progress_view_text_size, DEFAULT_TEXT_SIZE));
        maxValue = DEFAULT_MAX_VALUE;

        setProgress(array.getInt(R.styleable.ProgressView_progress_view_progress, DEFAULT_PROGRESS));
        setStartAngle(array.getInt(R.styleable.ProgressView_progress_view_starting_angle, DEFAULT_START_ANGLE));

        backgroundColorStateList = array.getColorStateList(R.styleable.ProgressView_progress_view_background_color);
        finishedStrokeColorStateList = array.getColorStateList(R.styleable.ProgressView_progress_view_finished_stroke_color);
        unfinishedStrokeColorStateList = array.getColorStateList(R.styleable.ProgressView_progress_view_unfinished_stroke_color);
        textColorStateList = array.getColorStateList(R.styleable.ProgressView_progress_view_text_color);

        showProgress = (array.getInt(R.styleable.ProgressView_progress_view_show_progress, 1) == 1);
        array.recycle();
    }

    protected void initPainters() {
        paintInnerCircle.setAntiAlias(true);
        paintInnerCircle.setColor(getColor(backgroundColorStateList, Color.WHITE));

        paintFinishedProgress.setColor(getColor(finishedStrokeColorStateList, Color.BLACK));
        paintFinishedProgress.setAntiAlias(true);
        paintFinishedProgress.setStyle(Paint.Style.STROKE);
        paintFinishedProgress.setStrokeWidth(strokeWidth);

        paintUnfinishedProgress.setAntiAlias(true);
        paintUnfinishedProgress.setColor(getColor(unfinishedStrokeColorStateList, Color.GRAY));
        paintUnfinishedProgress.setStyle(Paint.Style.STROKE);
        paintUnfinishedProgress.setStrokeWidth(strokeWidth);

        textPaint.setColor(getColor(textColorStateList, Color.BLACK));
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final float xCenterOffset = getWidth() / 2f;
        final float yCenterOffset = getHeight() / 2f;
        final float innerCircleRadius = (getWidth() - strokeWidth) / 2f;
        canvas.drawCircle(xCenterOffset, yCenterOffset, innerCircleRadius, paintInnerCircle);

        //noinspection SuspiciousNameCombination
        unfinishedProgressRect.set(strokeWidth, strokeWidth, getWidth() - strokeWidth, getHeight() - strokeWidth);
        if (showProgress) {
            canvas.drawArc(unfinishedProgressRect, 0f, 360f, false, paintUnfinishedProgress);
        }

        //noinspection SuspiciousNameCombination
        finishedProgressRect.set(strokeWidth, strokeWidth, getWidth() - strokeWidth, getHeight() - strokeWidth);

        float startAngle = getStartAngle();
        final int sweepAngle = (int) getMeasuredAngle();
        if (sweepAngle < ANGLE_EPSILON) {
            startAngle = 0f;
        }
        if (showProgress) {
            canvas.drawArc(finishedProgressRect, startAngle, sweepAngle, false, paintFinishedProgress);
        }

        if (showProgress) {
            //it's better to center text with Paint.getTextBounds(), so todo
            textPaint.setTextSize(textSize);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            int xPos = (getWidth() / 2);
            int yPos = (int) ((getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
            canvas.drawText(String.valueOf(progress), xPos, yPos, textPaint);

            int percentageMeasure = (int) textPaint.measureText("%");
            int percentageTextSize = textSize / 2;
            int percentageXPos = (getWidth() / 2) + percentageMeasure;
            int percentageYPos = (getHeight() / 2);
            textPaint.setTextSize(percentageTextSize);
            canvas.drawText("%", percentageXPos, percentageYPos, textPaint);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(START_ANGLE, getStartAngle());
        bundle.putInt(STROKE_WIDTH, getStrokeWidth());
        bundle.putInt(PROGRESS, getProgress());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            setStartAngle(bundle.getInt(START_ANGLE));
            setStrokeWidth(bundle.getInt(STROKE_WIDTH));
            setProgress(bundle.getInt(PROGRESS));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public int getStartAngle() {
        return this.startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle - 90 + DEFAULT_SHIFT_ANGLE;
        invalidate();
    }

    public float getMeasuredAngle() {
        return getProgress() / (float) maxValue * 360f;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (progress > maxValue)
            this.progress %= maxValue;
        invalidate();
    }

    public int getProgress() {
        return this.progress;
    }

    private int getColor(@Nullable ColorStateList colorStateList, int defaultColor) {
        int color;
        if (colorStateList != null)
            color = colorStateList.getColorForState(getDrawableState(), defaultColor);
        else
            color = defaultColor;
        return color;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = dpToPx(getContext(), strokeWidth);
        invalidate();
    }

    public void setTextSize(int setTextSize) {
        this.textSize = dpToPx(getContext(), setTextSize);
        invalidate();
    }

}



