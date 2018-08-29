package com.fuj.hangcity.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.fuj.hangcity.R;
import com.fuj.hangcity.cache.ConCache;
import com.fuj.hangcity.model.weather.Hourly;
import com.fuj.hangcity.model.weather.WeatherEntity;
import com.fuj.hangcity.utils.DensityUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuj
 */
public class HourlyCurveView extends ScrollView {
    private Context context;
    private Paint paint = null;
    private List<Integer> xCoordinateList = new ArrayList<>();
    private int columnWidth;//每一列的列宽
    private int viewHeigh;
    private float miniHeight;
    private List<Hourly> hourlylist;
    private List<String> datas;

    public HourlyCurveView(Context context) {
        super(context);
        this.context = context;
    }

    public HourlyCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ConCache cache = ConCache.getInstance();
        columnWidth = cache.getInt(context, ConCache.WEATHER_HOUR_COLUMN_WIDTH);
        viewHeigh = cache.getInt(context, ConCache.WEATHER_HOUR_HEIGHT);
        miniHeight = cache.getFloat(context, ConCache.WEATHER_HOUR_MINI_HEIGHT);
        initDatas();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = columnWidth * (hourlylist != null ? hourlylist.size() : 24); //自定义视图view宽度
        setMeasuredDimension(viewWidth, viewHeigh); //重写onMeasure方法,重新设置自定义view宽高,单位为px
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth(); //获取自定义view宽度,单位px

        drawLine(0, 0, width, 0, canvas); //画最上方的横线
        drawLine(0, viewHeigh - 2, width, viewHeigh - 2, canvas); //画最下方的横线

        if(hourlylist != null) {
            for(int i = 0; i < hourlylist.size(); i++) {
                int x = columnWidth / 2 + columnWidth * i;
                xCoordinateList.add(x);
                drawText(hourlylist.get(i).date, x, miniHeight * 9, canvas, 12); //绘制最底层的时间
            }
            drawHourlyCurve(miniHeight, canvas); //绘制中间的曲线图
        } else {
            drawRect(width, viewHeigh, canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        getParent().getParent().requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void drawPoint(PointF[] points, Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        for(PointF pointF : points) {
            canvas.drawCircle(pointF.x, pointF.y, columnWidth / 20, paint);
        }
    }

    private void drawLineByPoint(PointF[] ps, Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((columnWidth / 20) / 2);
        PointF startp;
        PointF endp;
        for(int i = 0; i < ps.length - 1; i++) {
            startp = ps[i];
            endp = ps[i + 1];
            canvas.drawLine(startp.x, startp.y, endp.x, endp.y, paint);
        }
    }

    private void drawText(String text, float x, float y, Canvas canvas, int textsize) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(DensityUtils.sp2px(context, textsize));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, x, y, paint);
    }

    private void drawRect(float width, float height, Canvas canvas) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setTextSize(DensityUtils.sp2px(context, 16));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("无数据", width / 3, height / 2, paint);
    }

    private void drawLine(float startX, float strtY, float endX, float endY, Canvas canvas) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(startX, strtY, endX, endY, paint);
    }

    private void drawBitmap(int resId, float x, float y, Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        canvas.drawBitmap(bitmap, x - bitmap.getWidth() / 2, y, paint);
    }

    private void drawHourlyCurve(float miniHeight, Canvas canvas) {
        float maxTemp = Float.MIN_VALUE;
        float minTemp = Float.MAX_VALUE;
        for(int i = 0; i < hourlylist.size(); i++) {
            float temp = hourlylist.get(i).tmp;
            if(temp > maxTemp) {
                maxTemp = temp;
            }

            if(temp < minTemp) {
                minTemp = temp;
            }
        }
        float rangTemp = maxTemp - minTemp;
        if(rangTemp == 0) {
            rangTemp = 10f;
        }

        float relativeZeroDegreeY = miniHeight * 5;
        float middleTemp = (maxTemp + minTemp) / 2;
        float heightPerRangTemp = (miniHeight * 4) / rangTemp;
        PointF yPoints[] = new PointF[hourlylist.size()];
        for(int i = 0; i < hourlylist.size(); i++) {
            float tempY = relativeZeroDegreeY + (middleTemp - hourlylist.get(i).tmp) * heightPerRangTemp;
            yPoints[i] = new PointF(xCoordinateList.get(i), tempY);
        }
        drawLineByPoint(yPoints, canvas, paint);
        drawPoint(yPoints, canvas);
    }

    public void refresh(WeatherEntity weatherEntity) {
        if(hourlylist != null) {
            hourlylist.clear();
        } else {
            hourlylist = new ArrayList<>();
        }

        for (int j = 0; j < weatherEntity.hourly_forecast.size(); j++) {
            weatherEntity.hourly_forecast.get(j).date = weatherEntity.hourly_forecast.get(j).date.split(" ")[1];
            for (int i = 0; i < datas.size(); i++) {
                if (datas.get(i).equals(weatherEntity.hourly_forecast.get(j).date)) {
                    hourlylist.add(weatherEntity.hourly_forecast.get(j));
                    weatherEntity.hourly_forecast.remove(0);
                    datas.remove(i);
                    j = 0;
                    break;
                } else {
                    Hourly temp = new Hourly();
                    temp.date = datas.get(i);
                    temp.tmp = weatherEntity.hourly_forecast.get(j).tmp;
                    hourlylist.add(temp);
                    datas.remove(0);
                    i = -1;
                }
            }
        }
        invalidate();
    }

    private void initDatas() {
        datas = new ArrayList<>();
        DecimalFormat format = new DecimalFormat("00");
        for(int i = 0; i <= 24; i++) {
            datas.add(format.format(i) + ":00");
        }
    }

    private Integer getResId(int flag) {
        int i = 0;
        switch(flag) {
            case 0: i = R.mipmap.weather00; break;
        }
        return i;
    }
}
