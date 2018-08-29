package com.fuj.hangcity.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.fuj.hangcity.R;
import com.fuj.hangcity.cache.ConCache;
import com.fuj.hangcity.model.weather.Daily;
import com.fuj.hangcity.model.weather.WeatherEntity;
import com.fuj.hangcity.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuj
 */
public class TempertureCurveView extends View {
    private Context context;
    private Paint paint;
    private List<Integer> xCoordinateList = new ArrayList<>();
    private int columnWidth;//每一列的列宽
    private int viewHeigh;
    private float miniHeight;
    private List<Daily> dailylist;

    public TempertureCurveView(Context context) {
        super(context);
        this.context = context;
    }

    public TempertureCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ConCache cache = ConCache.getInstance();
        columnWidth = cache.getInt(context, ConCache.WEATHER_TEMP_COLUMN_WIDTH);
        viewHeigh = cache.getInt(context, ConCache.WEATHER_TEMP_HEIGHT);
        miniHeight = cache.getFloat(context, ConCache.WEATHER_TEMP_MINI_HEIGHT);
    }

    public TempertureCurveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = columnWidth * (dailylist != null ? dailylist.size() : 6); //自定义视图view宽度
        setMeasuredDimension(viewWidth, viewHeigh); //重写onMeasure方法,重新设置自定义view宽高,单位为px
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth(); //获取自定义view宽度,单位px

        drawLine(0, 0, width, 0, canvas); //画最上方的横线
        drawLine(0, viewHeigh - 2, width, viewHeigh - 2, canvas); //画最下方的横线

        if(dailylist != null) {
            for(int i = 0; i < dailylist.size(); i++) {
                int x = columnWidth / 2 + columnWidth * i;
                xCoordinateList.add(x);
                drawLine(columnWidth * i, 0, columnWidth * i, viewHeigh, canvas);//绘制每列中间的竖线
                drawText("周一", x, miniHeight * 5, canvas, 16);//绘制第一行week信息
                drawText(dailylist.get(i).cond.txt_d, x, miniHeight * 9, canvas, 12);//绘制第二行weather信息
                drawBitmap(getResId(0), x, miniHeight * 10, canvas);//绘制第三行weather icon信息
                drawBitmap(getResId(0), x, miniHeight * 73, canvas);//绘制第六行weather icon信息
                drawText(dailylist.get(i).cond.txt_n, x, miniHeight * 83, canvas, 12);//绘制第七行weather信息
                drawText(dailylist.get(i).date.replace("-", "/").substring(dailylist.get(i).date.indexOf("-") + 1),
                    x, miniHeight * 87, canvas, 12);//绘制第九行date信息
                drawText(dailylist.get(i).wind.dir, x, miniHeight * 91, canvas, 12);//绘制第10行wind信息
                drawText(dailylist.get(i).wind.sc, x, miniHeight * 95, canvas, 12);//绘制第11行风力信息
            }
            drawTempCurve(miniHeight, canvas);//绘制中间的曲线图
        } else {
            drawRect(width, viewHeigh, canvas);
        }
    }

    private void drawPoint(PointF[] points, Canvas canvas, int flag) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        String temp;
        for(int i = 0; i < points.length; i++) {
            canvas.drawCircle(points[i].x, points[i].y, columnWidth / 20, paint);
            if(flag == 0) {
                temp = dailylist.get(i).tmp.min + "°";
                drawText(temp, points[i].x, points[i].y + DensityUtils.dp2px(context, 20), canvas, 12);
            } else {
                temp = dailylist.get(i).tmp.max + "°";
                drawText(temp, points[i].x, points[i].y - DensityUtils.dp2px(context, 14), canvas, 12);
            }
        }
    }

    private void drawLineByPoint(PointF[] ps, Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((columnWidth / 20) / 2);
        paint.setPathEffect(new CornerPathEffect(30));
        Path path = new Path();
        path.moveTo(ps[0].x, ps[0].y);
        for(int i = 1; i < ps.length; i++) {
            path.lineTo(ps[i].x, ps[i].y);
        }
        canvas.drawPath(path, paint);
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

    private List<Integer> getxCoordinateCenterList() {
        return xCoordinateList;
    }

    private void drawTempCurve(float miniHeight, Canvas canvas) {
        float maxTemp = Float.MIN_VALUE;
        float minTemp = Float.MAX_VALUE;
        for(int i = 0; i < dailylist.size(); i++) {
            float mintemp = dailylist.get(i).tmp.min;
            float maxtemp = dailylist.get(i).tmp.max;
            if(maxtemp > maxTemp) {
                maxTemp = maxtemp;
            }
            if(mintemp < minTemp) {
                minTemp = mintemp;
            }
        }
        float rangTemp = maxTemp - minTemp;
        if(rangTemp == 0) {
            rangTemp = 10f;
        }

        float relativeZeroDegreeY = miniHeight * 45;
        float middleTemp = (maxTemp + minTemp) / 2;
        float heightPerRangTemp = (miniHeight * 40) / rangTemp;
        PointF minPoints[] = new PointF[dailylist.size()];//minTemp点集合
        PointF maxPoints[] = new PointF[dailylist.size()];//maxTemp点集合
        for(int i = 0; i < dailylist.size(); i++) {
            float minY = relativeZeroDegreeY + (middleTemp - dailylist.get(i).tmp.min) * heightPerRangTemp;
            float maxY = relativeZeroDegreeY - (dailylist.get(i).tmp.max - middleTemp) * heightPerRangTemp;
            minPoints[i] = new PointF(getxCoordinateCenterList().get(i), minY);
            maxPoints[i] = new PointF(getxCoordinateCenterList().get(i), maxY);
        }

        paint.setColor(context.getResources().getColor(R.color.blue_trans));
        drawLineByPoint(minPoints, canvas);//绘制minTemp点
        paint.setColor(context.getResources().getColor(R.color.green_trans));
        drawLineByPoint(maxPoints, canvas);//绘制maxTemp点
        drawPoint(minPoints, canvas, 0);
        drawPoint(maxPoints, canvas, 1);
    }

    public void refresh(WeatherEntity weatherEntity) {
        if(dailylist != null) {
            dailylist.clear();
        } else {
            dailylist = new ArrayList<>();
        }
        dailylist.addAll(weatherEntity.daily_forecast);
        invalidate();
    }

    private Integer getResId(int flag) {
        int i = 0;
        switch(flag) {
            case 0: i = R.mipmap.weather00; break;
            /*case 1: i = R.mipmap.weather06; break;
            case 2: i = R.mipmap.weather02; break;
            case 3: i = R.mipmap.weather03; break;
            case 4: i = R.mipmap.weather03; break;
            case 5: i = R.mipmap.weather05; break;
            case 6: i = R.mipmap.weather13; break;
            case 7: i = R.mipmap.weather07; break;
            case 8: i = R.mipmap.weather07; break;
            case 9: i = R.mipmap.weather17; break;
            case 10: i = R.mipmap.weather11; break;
            case 11: i = R.mipmap.weather11; break;
            case 12: i = R.mipmap.weather11; break;
            case 13: i = R.mipmap.weather12; break;
            case 14: i = R.mipmap.weather12; break;
            case 15: i = R.mipmap.weather12; break;
            case 16: i = R.mipmap.weather05; break;
            case 17: i = R.mipmap.weather05; break;
            case 18: i = R.mipmap.weather08; break;
            case 19: i = R.mipmap.weather03; break;
            case 20: i = R.mipmap.weather08; break;
            case 21: i = R.mipmap.weather07; break;
            case 22: i = R.mipmap.weather07; break;
            case 23: i = R.mipmap.weather11; break;
            case 24: i = R.mipmap.weather11; break;
            case 25: i = R.mipmap.weather11; break;
            case 26: i = R.mipmap.weather12; break;
            case 27: i = R.mipmap.weather12; break;
            case 28: i = R.mipmap.weather05; break;
            case 29: i = R.mipmap.weather10; break;
            case 30: i = R.mipmap.weather16; break;
            case 31: i = R.mipmap.weather17; break;
            case 53: i = R.mipmap.weather15; break;*/
        }
        return i;
    }
}
