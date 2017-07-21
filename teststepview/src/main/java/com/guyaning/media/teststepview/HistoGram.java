package com.guyaning.media.teststepview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.text.NumberFormat;

/**
 * Created by LST on 2017/7/20.
 */

public class HistoGram extends View implements Runnable {

    private Handler handler = new Handler(); // 用于延时更新，实现动画
    private float animHeight; // 进度条动画高度
    private Paint axisLinePaint; // 坐标轴画笔
    private Paint hLinePaint; // 内部水平虚线画笔
    private Paint textPaint; // 绘制文本的画笔
    private Paint recPaint; // 绘制柱状图阴影背景的画笔
    private Paint dataPaint; // 绘制柱状图的画笔
    private Paint textPaint2; // 绘制白色文本的画笔
    private Paint textPaint3; // 绘制坐标的画笔
    private Paint textPaint4; // 绘制x轴上的白色竖线的画笔
    private String[] xTitleString; // x轴刻度
    private String[] yTitleString; // y轴刻度
    private String[] data; // 接口返回的indicatordata，用于计算柱子高度
    NumberFormat numberFormat; //用于格式化数字
    private float currentHeight; // 当前柱状图应有的高度，应由计算得来
    private int num = -1; // 画多少条柱子，因为存在刚开机数据不足24条的情况
    private float mRelativePxInHeight;
    private float mRelativePxInWidth;
    private OnChartClickListener listener;
    private int mDist;
    private RectF dataRectF;


    public void setNum(int num) {
        this.num = num;
        invalidate();
    }

    public void setData(String[] data) {
        this.data = data;
        invalidate();
    }

    public void setxTitleString(String[] title) {
        this.xTitleString = title;
        invalidate();
    }

    public HistoGram(Context context) {
        this(context, null);
    }

    public HistoGram(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setTitle(String[] title) {
        this.xTitleString = title;
    }

    public HistoGram(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 进行相关初始化操作
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        axisLinePaint = new Paint();
        hLinePaint = new Paint();
        textPaint = new Paint();
        recPaint = new Paint();
        dataPaint = new Paint();
        textPaint2 = new Paint();
        textPaint3 = new Paint();
        textPaint4 = new Paint();
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(3); //设置打印时保留三位小数
        axisLinePaint.setColor(Color.parseColor("#dbdde4")); //设置坐标轴的颜色为白色
        hLinePaint.setARGB(51, 255, 255, 255);
        textPaint.setColor(Color.parseColor("#8593a1"));
//    textPaint.setTextSize(29);
        textPaint.setTextSize(ViewUtils.dp2px(getContext(), 12));
        recPaint.setColor(Color.parseColor("#f2f5fc"));
        dataPaint.setColor(Color.CYAN);
        textPaint2.setColor(Color.WHITE);
        textPaint2.setTextSize(ViewUtils.dp2px(getContext(), 12));
        textPaint3.setColor(Color.parseColor("#000000"));
        textPaint3.setTextSize(ViewUtils.dp2px(getContext(), 9));
        textPaint4.setColor(Color.parseColor("#8593a1"));
        textPaint4.setTextSize(ViewUtils.dp2px(getContext(), 6));
        axisLinePaint.setAntiAlias(true);
        hLinePaint.setAntiAlias(true);
        textPaint.setAntiAlias(true);
        recPaint.setAntiAlias(true);
        dataPaint.setAntiAlias(true);
        textPaint2.setAntiAlias(true);
        textPaint3.setAntiAlias(true);
        textPaint4.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || xTitleString == null || num < 0) {
            return;
        }
        //绘制y轴刻度
        Paint.FontMetrics metrics = textPaint3.getFontMetrics();
        int decent = (int) metrics.descent;
        float width = getWidth();
        float height = getHeight();
        //根据原型图得出，图中每px高度在实际中的相对尺寸
        mRelativePxInHeight = height / 470;
        //根据原型图得出，图中每px宽度在实际中的相对尺寸
        mRelativePxInWidth = width / 690;
        textPaint3.setTextAlign(Paint.Align.RIGHT);
        //绘制纵坐标
        yTitleString = new String[6];
        yTitleString[5] = "0";
        yTitleString[4] = "20";
        yTitleString[3] = "40";
        yTitleString[2] = "60";
        yTitleString[1] = "80";
        yTitleString[0] = "100";
        for (int i = 0; i < yTitleString.length; i++) {
            // canvas.drawText(yTitleString[i], 88 * mRelativePxInWidth, (72 + i * 56) * mRelativePxInHeight + decent, textPaint3);
        }
        //绘制x轴刻度
        textPaint3.setTextAlign(Paint.Align.CENTER);
        textPaint4.setTextAlign(Paint.Align.CENTER);
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.parseColor("#000000"));
        textPaint.setTextSize(ViewUtils.dp2px(getContext(), 9));
        //计算柱子之间的间隔
        //最左侧位置100 * mRelativePxInWidth,最右侧位置630 ePxInWidth,
        float totalWidth = 630 - 100;
        // 柱子与之子之间的间隔
        mDist = (int) (totalWidth / (xTitleString.length + 1));
        for (int i = 0; i < xTitleString.length; i++) {
            //绘制白色竖线
            //canvas.drawLine((100 + (i+1) * mDist) * mRelativePxInWidth, 348 * mRelativePxInHeight, (100 + (i+1) * mDist) * mRelativePxInWidth, 352 * mRelativePxInHeight, axisLinePaint);
            //绘制x轴文字
            canvas.drawText(xTitleString[i], (100 + (i + 1) * mDist) * mRelativePxInWidth, 370 * mRelativePxInHeight, textPaint3);
        }
//    绘制矩形阴影
        for (int i = 0; i < num; i++) {
            RectF rectF = new RectF();
//      rectF.left = 111 * relativePxInWidth + i * 22 * relativePxInWidth;
//      rectF.right = 121 * relativePxInWidth + i * 22 * relativePxInWidth;
//            rectF.left = 75 * mRelativePxInWidth + (i + 1) * mDist * mRelativePxInWidth;
//            rectF.right = 120 * mRelativePxInWidth + (i + 1) * mDist * mRelativePxInWidth;
            rectF.top = 70 * mRelativePxInHeight;
            rectF.bottom = 338 * mRelativePxInHeight;
            // canvas.drawRoundRect(rectF, 10, 10, recPaint);
        }


        //    绘制x轴坐标线
        // for (int i = 0; i < 6; i++) {
        canvas.drawLine(100 * mRelativePxInWidth, (66 + 5 * 56) * mRelativePxInHeight + decent, 630 * mRelativePxInWidth, (66 + 5 * 56) * mRelativePxInHeight + decent, axisLinePaint);
        // }


//    延时绘制，实现动画效果。数字越大，延时越久，动画效果就会越慢
        handler.postDelayed(this, 1);
        for (int i = 0; i < num; i++) {
            dataRectF = new RectF();
            dataRectF.left = 95 * mRelativePxInWidth + (i + 1) * mDist * mRelativePxInWidth;
            dataRectF.right = 110 * mRelativePxInWidth + (i + 1) * mDist * mRelativePxInWidth;
            //dataPaint.setColor(Color.parseColor("#0BC9BE"));
            dataPaint.setShader(new LinearGradient(0, 0, 15, 346 * mRelativePxInHeight, Color.parseColor("#18BE81"), Color.parseColor("#4CF7B7"), Shader.TileMode.CLAMP));
            //获取柱子高度
            currentHeight = Float.parseFloat(data[num - 1 - i]);
            /*if (currentHeight>60&&currentHeight<=100) {
                dataPaint.setColor(Color.parseColor("#18BE81"));
            }else{
                dataPaint.setColor(Color.parseColor("#4CF7B7"));
            }*/

            if (currentHeight == 0) {
                dataRectF.top = 346 * mRelativePxInHeight + 7;
            } else if (currentHeight == 100) {
                dataRectF.top = 50 * mRelativePxInHeight;
            } else {
                if (animHeight >= currentHeight) {
                    dataRectF.top = 346 * mRelativePxInHeight - currentHeight / 100 * 296 * mRelativePxInHeight;
                } else {
                    dataRectF.top = 346 * mRelativePxInHeight - 296 * mRelativePxInHeight * (animHeight / 100);
                }
            }
            dataRectF.bottom = 346 * mRelativePxInHeight + 7;
//        限制最高高度
            if (dataRectF.top < 50 * mRelativePxInHeight) {
                dataRectF.top = 50 * mRelativePxInHeight;
            }
            drawViewRect(canvas);


        }

    }

    private float cornerRadius = 10;

    protected int corners;

    public static final int CORNER_TOP_LEFT = 1;
    public static final int CORNER_TOP_RIGHT = 1 << 1;
    public static final int CORNER_BOTTOM_LEFT = 1 << 2;
    public static final int CORNER_BOTTOM_RIGHT = 1 << 3;
    //    public static final int CORNER_ALL = CORNER_TOP_LEFT | CORNER_TOP_RIGHT | CORNER_BOTTOM_LEFT | CORNER_BOTTOM_RIGHT;
    public static final int CORNER_ALL = CORNER_BOTTOM_LEFT | CORNER_BOTTOM_RIGHT;

    private void drawViewRect(Canvas canvas) {
        canvas.drawRoundRect(dataRectF, cornerRadius, cornerRadius, dataPaint);

        int notRoundedCorners =  CORNER_ALL;
        //哪个角不是圆角我再把你用矩形画出来
//        if ((notRoundedCorners & CORNER_TOP_LEFT) != 0) {
//            canvas.drawRect(0, 0, cornerRadius, cornerRadius, dataPaint);
//        }
//        if ((notRoundedCorners & CORNER_TOP_RIGHT) != 0) {
//            canvas.drawRect(dataRectF.right - cornerRadius, 0, dataRectF.right, cornerRadius, dataPaint);
//        }
        if ((notRoundedCorners & CORNER_BOTTOM_LEFT) != 0) {
            canvas.drawRect(dataRectF.left - cornerRadius, dataRectF.bottom - cornerRadius, cornerRadius, dataRectF.bottom, dataPaint);
        }
        if ((notRoundedCorners & CORNER_BOTTOM_RIGHT) != 0) {
            canvas.drawRect(dataRectF.right - cornerRadius, dataRectF.bottom - cornerRadius, dataRectF.right, dataRectF.bottom, dataPaint);
        }
    }


    //实现柱子增长的动画效果
    @Override
    public void run() {
        animHeight += 1;
        if (animHeight >= 276 * mRelativePxInHeight) {
            return;
        } else {
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //获取点击坐标
                float x = event.getX();
                float y = event.getY();
                //判断点击点的位置
                float leftx = 0;
                float rightx = 0;
                for (int i = 0; i < num; i++) {
                    leftx = 95 * mRelativePxInWidth + (i + 1) * mDist * mRelativePxInWidth - mDist / 2 * mRelativePxInWidth;
                    rightx = 105 * mRelativePxInWidth + (i + 1) * mDist * mRelativePxInWidth + mDist / 2 * mRelativePxInWidth;
                    if (x < leftx) {
                        continue;
                    }
                    if (leftx <= x && x <= rightx) {
                        //获取点击的柱子区域的y值
                        float top = 346 * mRelativePxInHeight - Float.parseFloat(data[num - 1 - i]) / 100 * 276 * mRelativePxInHeight;
                        float bottom = 346 * mRelativePxInHeight;
                        if (y >= top && y <= bottom) {
                            //判断是否设置监听
                            //将点击的第几条柱子，点击柱子顶部的坐值，用于弹出dialog提示数据，还要返回百分比currentHeidht = Float.parseFloat(data[num - 1 - i])
                            if (listener != null) {
                                Log.e("ss", "x" + x + ";y:" + y);
                                listener.onClick(i + 1, leftx + mDist / 2, top, Float.parseFloat(data[num - 1 - i]));
                            }
                            break;
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
                Log.e("touch", "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("touch", "ACTION_UP");
                break;
        }
        return true;
    }

    /**
     * 柱子点击时的监听接口
     */
    public interface OnChartClickListener {
        void onClick(int num, float x, float y, float value);
    }

    /**
     * 设置柱子点击监听的方法
     *
     * @param listener
     */
    public void setOnChartClickListener(OnChartClickListener listener) {
        this.listener = listener;
    }
}
