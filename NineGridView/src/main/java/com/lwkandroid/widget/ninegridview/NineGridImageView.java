package com.lwkandroid.widget.ninegridview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * ImageView which has click effect
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
