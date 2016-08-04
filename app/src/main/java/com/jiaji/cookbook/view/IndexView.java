package com.jiaji.cookbook.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 右侧索引列
 */
public class IndexView extends View {
    private Context context;
    //纵向显示的所有字符
    public static final String[] indexStrs = {
            "○○○○○○",
            "菜式菜品　─",
            "菜　　系　─",
            "时令食材　─",
            "功　　效　─",
            "场　　景　─",
            "工艺口味　─",
            "菜　　肴　─",
            "主　　食　─",
            "西　　点　─",
            "汤羹饮品　─",
            "其他菜品　─",
            "人　　群　─",
            "疾　　病　─",
            "畜肉　类　─",
            "禽蛋　类　─",
            "水产　类　─",
            "蔬菜　类　─",
            "水果　类　─",
            "米面豆乳　─",
            "日　　常　─",
            "节　　日　─",
            "节　　气　─",
            "基本工艺　─",
            "其他工艺　─",
            "基本口味　─",
            "多元口味　─",
            "水果　味　─",
            "调味　料　─",
            "○○○○○○"};
    public static final String[] indexStrs2 = {
            "○○○○○○",
            ">菜式菜品　─",
            ">菜　　系　─",
            ">时令食材　─",
            ">功　　效　─",
            ">场　　景　─",
            ">工艺口味　─",
            ">菜　　肴　─",
            ">主　　食　─",
            ">西　　点　─",
            ">汤羹饮品　─",
            ">其他菜品　─",
            ">人　　群　─",
            ">疾　　病　─",
            ">畜肉　类　─",
            ">禽蛋　类　─",
            ">水产　类　─",
            ">蔬菜　类　─",
            ">水果　类　─",
            ">米面豆乳　─",
            ">日　　常　─",
            ">节　　日　─",
            ">节　　气　─",
            ">基本工艺　─",
            ">其他工艺　─",
            ">基本口味　─",
            ">多元口味　─",
            ">水果　味　─",
            ">调味　料　─",
            "○○○○○○"};
    /**
     * #>选中的字颜色
     */
    public static final int TEXT_COLOR_SELECTED = 0xffff5e00;
    /**
     * 没有触碰状态下的字颜色
     */
    public static final int TEXT_COLOR_UNTOUCH = 0xffa3a3a3;
    private int width;//控件宽度
    private int height;//控件高度
    private int indexHeight;//每个索引的高度,右边每个菜名类别的高度
    private Paint paint;
    private int selectedIndex = 1;//#>被选中字母的下标

    public IndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setTextSize(28);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width == 0 || height == 0) {
            width = getWidth();
            height = getHeight();
            indexHeight = height / indexStrs.length;
        }
        for (int i = 0, length = indexStrs.length; i < length; i++) {
            if (selectedIndex == i)//#>设置被选中的字母颜色
            {
                paint.setColor(TEXT_COLOR_SELECTED);
            } else {
                paint.setColor(TEXT_COLOR_UNTOUCH);
            }
            float x = (width - paint.measureText(indexStrs[0])) / 2;
            float y = indexHeight * i + indexHeight - paint.measureText(indexStrs[0].charAt(0) + "") / 2;
            if (selectedIndex == i) {
                canvas.drawText(indexStrs2[i], x, y, paint);
            } else {
                canvas.drawText(indexStrs[i], x, y, paint);
            }
        }
    }

    private float y;//#>点击的y坐标
    private int lastSelectedIndex = -1;//#>记录上一次的位置

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        y = event.getY();//#>获取当前触摸时的y坐标
        selectedIndex = (int) (y / indexHeight);//#>计算出当前触碰到的字母下标
        if (selectedIndex <= 0) selectedIndex = 1;//#>如果下标处于0将，下标改为1，不让下标为0的☆产生监听
        if (selectedIndex >= indexStrs.length - 1)
            selectedIndex = indexStrs.length - 2;//#>如果下标处于最后将，下标改为letters.length() - 2，不让最下面的#产生监听
        if (selectedIndex != lastSelectedIndex) {//#>如果触摸的地方不是上一次的y轴位置，重绘，调用回调中间显示字母
            if (letterChangeListener != null) {
                letterChangeListener.onLetterChange(selectedIndex);
            }
            invalidate();
            lastSelectedIndex = selectedIndex;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                if (letterChangeListener != null) {

                    letterChangeListener.onClickUp();//
                }
                break;
        }
        invalidate();
        return true;
    }

    /**
     * #>回调接口，处理字母的点击事件
     */
    //#>回调接口(当前是哪一栏，则字就显示哪一个)
    public interface OnLetterChangeListener {
        void onLetterChange(int selectedIndex);//#>当位置发生改变时调用

        void onClickUp();//#>当触摸后，放开时调用
    }

    private OnLetterChangeListener letterChangeListener;

    public void setOnLetterChangeListener(OnLetterChangeListener letterChangeListener) {
        this.letterChangeListener = letterChangeListener;
    }

    //设置当前那个字被选中
    public void setSelected(int section) {
        this.selectedIndex = section;
        invalidate();
    }
}
