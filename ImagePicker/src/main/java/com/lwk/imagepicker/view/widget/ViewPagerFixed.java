package com.lwk.imagepicker.view.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 兼容图片缩放控件的ViewPager
 */
public class ViewPagerFixed extends ViewPager
{
    public ViewPagerFixed(Context context)
    {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        try
        {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        try
        {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}
