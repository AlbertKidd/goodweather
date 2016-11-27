package com.goodweather.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by niuwa on 2016/11/27.
 */

public class AqiView extends View {

    private Paint mPaint;
    private RectF mRectF;
    private float aqi;

    public AqiView(Context context){
        this(context, null);
    }

    public AqiView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public AqiView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();
        float lineSize = w / 10f;

        if (aqi == 0){
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(lineSize * 1.25f);
            mPaint.setColor(0xaaffffff);
            canvas.drawText("暂无数据", w / 2f, h / 2f, mPaint);
            return;
        }

        float aqiPercent = aqi / 500;
        if (aqiPercent > 1)
            aqiPercent = 1f;

        canvas.save();

        canvas.translate(w / 2, h / 2);

        float startAngle = -210f;
        float sweepAngle = 240f;
        float radius = lineSize * 4f;
        mRectF.set(-radius, -radius, radius, radius);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineSize);
        mPaint.setColor(0x55ffffff);
        canvas.drawArc(mRectF, startAngle + sweepAngle * aqiPercent, sweepAngle * (1 - aqiPercent), false, mPaint);

        mPaint.setColor(0x99ffffff);
        canvas.drawArc(mRectF, startAngle, sweepAngle * aqiPercent, false, mPaint);

        mPaint.setColor(0xffffffff);
        mPaint.setStrokeWidth(lineSize / 8f);
        canvas.drawCircle(0, 0, lineSize / 3f, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(lineSize * 1.2f);
        canvas.drawText("aqi : " + (int)aqi, 0, lineSize * 2.5f, mPaint);

        canvas.rotate(startAngle + sweepAngle * aqiPercent);
        canvas.drawLine(lineSize / 3f, 0, radius, 0, mPaint);

        canvas.restore();
    }

    public void setAqi(float aqi){
        this.aqi = aqi;
        invalidate();
    }
}
