package com.lwk.ninegridview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by LWK
 * TODO 自定义ImageView，模拟点击效果
 * 2016/12/1
 */
public class NineGridImageView extends ImageView
{
    public NineGridImageView(Context context)
    {
        super(context);
        setClickable(true);
    }

    public NineGridImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setClickable(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                setAlpha(0.7f);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setAlpha(1.0f);
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
