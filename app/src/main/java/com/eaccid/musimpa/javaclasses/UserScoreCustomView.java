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
 * View shows score in a circle (%)
 */

public class UserScoreCustomView extends View {

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String START_ANGLE = "start_angle";
    private static final String STROKE_WIDTH = "stroke_width";
    private static final String SCORE = "score";
    private static final double ANGLE_EPSILON = 1;
    private static final int DEFAULT_SHIFT_ANGLE = 200;
    private static final int DEFAULT_START_ANGLE = 0;
    private static final int DEFAULT_STROKE_WIDTH = 8;
    private static final int DEFAULT_PROGRESS = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_TEXT_SIZE = 50;

    final private RectF unfinishedScoreProgressRect = new RectF();
    final private RectF scoreProgressColor = new RectF();
    final private Paint paintScoreProgress = new Paint();
    final private Paint paintUnfinishedScoreProgress = new Paint();
    final private Paint paintInnerCircle = new Paint();
    final private Paint textPaint = new Paint();
    private ColorStateList backgroundColorStateList;
    private ColorStateList unfinishedProgrssStrokeColorStateList;
    private ColorStateList scoreProgressStrokeColorStateList;
    private ColorStateList textColorStateList;

    private int maxScore; //%
    private int startAngle;
    private int strokeWidth;
    private int score;
    private int textSize;
    private boolean displayScore;

    public UserScoreCustomView(Context context) {
        this(context, null);
    }

    public UserScoreCustomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserScoreCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromObtainedStyledAttributes(context, attrs);
        initPainters();
    }

    private void initFromObtainedStyledAttributes(Context context, AttributeSet attrs) {
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.UserScoreCustomView);
        setStrokeWidth(array.getInt(R.styleable.UserScoreCustomView_user_score_value_stroke_width, DEFAULT_STROKE_WIDTH));
        setTextSize(array.getDimensionPixelSize(R.styleable.UserScoreCustomView_user_score_text_size, DEFAULT_TEXT_SIZE));
        maxScore = DEFAULT_MAX_VALUE;

        setScore(array.getInt(R.styleable.UserScoreCustomView_user_score_value, DEFAULT_PROGRESS));
        setStartAngle(array.getInt(R.styleable.UserScoreCustomView_user_score_starting_angle, DEFAULT_START_ANGLE));

        backgroundColorStateList = array.getColorStateList(R.styleable.UserScoreCustomView_user_score_background_color);
        scoreProgressStrokeColorStateList = array.getColorStateList(R.styleable.UserScoreCustomView_user_score_progress_color);
        unfinishedProgrssStrokeColorStateList = array.getColorStateList(R.styleable.UserScoreCustomView_user_score_color);
        textColorStateList = array.getColorStateList(R.styleable.UserScoreCustomView_user_score_text_color);

        displayScore = (array.getInt(R.styleable.UserScoreCustomView_user_score_display_value, 1) == 1);
        array.recycle();
    }

    protected void initPainters() {
        paintInnerCircle.setAntiAlias(true);
        paintInnerCircle.setColor(getColor(backgroundColorStateList, Color.WHITE));

        paintScoreProgress.setColor(getColor(scoreProgressStrokeColorStateList, Color.BLACK));
        paintScoreProgress.setAntiAlias(true);
        paintScoreProgress.setStyle(Paint.Style.STROKE);
        paintScoreProgress.setStrokeWidth(strokeWidth);

        paintUnfinishedScoreProgress.setAntiAlias(true);
        paintUnfinishedScoreProgress.setColor(getColor(unfinishedProgrssStrokeColorStateList, Color.GRAY));
        paintUnfinishedScoreProgress.setStyle(Paint.Style.STROKE);
        paintUnfinishedScoreProgress.setStrokeWidth(strokeWidth);

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
        unfinishedScoreProgressRect.set(strokeWidth, strokeWidth, getWidth() - strokeWidth, getHeight() - strokeWidth);
        if (displayScore) {
            canvas.drawArc(unfinishedScoreProgressRect, 0f, 360f, false, paintUnfinishedScoreProgress);
        }

        //noinspection SuspiciousNameCombination
        scoreProgressColor.set(strokeWidth, strokeWidth, getWidth() - strokeWidth, getHeight() - strokeWidth);

        float startAngle = getStartAngle();
        final int sweepAngle = (int) getMeasuredAngle();
        if (sweepAngle < ANGLE_EPSILON) {
            startAngle = 0f;
        }
        if (displayScore) {
            canvas.drawArc(scoreProgressColor, startAngle, sweepAngle, false, paintScoreProgress);
        }

        if (displayScore) {
            //it's better to center text with Paint.getTextBounds(), so todo
            textPaint.setTextSize(textSize);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            int xPos = (getWidth() / 2);
            int yPos = (int) ((getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
            canvas.drawText(String.valueOf(score), xPos, yPos, textPaint);

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
        bundle.putInt(SCORE, getScore());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            setStartAngle(bundle.getInt(START_ANGLE));
            setStrokeWidth(bundle.getInt(STROKE_WIDTH));
            setScore(bundle.getInt(SCORE));
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
        return getScore() / (float) maxScore * 360f;
    }

    public void setScore(int score) {
        this.score = score;
        if (score > maxScore)
            this.score %= maxScore;
        invalidate();
    }

    public int getScore() {
        return this.score;
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        paintInnerCircle.reset();
        paintScoreProgress.reset();
        paintUnfinishedScoreProgress.reset();
        textPaint.reset();
        unfinishedScoreProgressRect.setEmpty();
        scoreProgressColor.setEmpty();
        backgroundColorStateList = null;
        unfinishedProgrssStrokeColorStateList = null;
        scoreProgressStrokeColorStateList = null;
        textColorStateList = null;
    }
}

