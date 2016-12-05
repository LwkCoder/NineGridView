package com.lwk.imagepicker.view.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lwk.imagepicker.R;


/**
 * Function:通用自定义actionbar
 */
public class ImagePickerActionBar extends RelativeLayout
{
    private View mLayoutLeftContainer;
    private TextView mTvTitle;
    private View mLayoutRight;
    private TextView mTvRight;

    public ImagePickerActionBar(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public ImagePickerActionBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr)
    {
        inflate(context, R.layout.layout_imagepicker_actionbar, this);
        mLayoutLeftContainer = findViewById(R.id.ll_imagepicker_actionbar_left_container);
        mTvTitle = (TextView) findViewById(R.id.tv_imagepicker_actionbar_title);
        mLayoutRight = findViewById(R.id.fl_imagepicker_actionbar_right);
        mTvRight = (TextView) findViewById(R.id.tv_imagepicker_actionbar_right);
        setWillNotDraw(false);
    }

    /**
     * 设置左侧布局为返回功能但是没有文字
     *
     * @param activity
     */
    public void setLeftLayoutAsBack(final Activity activity)
    {
        mLayoutLeftContainer.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (activity != null)
                    activity.finish();
            }
        });
    }

    /**
     * 设置标题
     *
     * @param resId 资源id
     */
    public void setTitleText(int resId)
    {
        mTvTitle.setText(getResources().getText(resId));
    }

    /**
     * 设置标题
     *
     * @param s 字符串
     */
    public void setTitleText(String s)
    {
        mTvTitle.setText(s);
    }

    /**
     * 设置是否显示右边部分【默认不可见】
     *
     * @param visibility
     */
    public void setRightLayoutVisibility(int visibility)
    {
        mLayoutRight.setVisibility(visibility);
    }

    /**
     * 设置右边布局点击事件
     *
     * @param listener
     */
    public void setRightLayoutClickListener(OnClickListener listener)
    {
        mLayoutRight.setOnClickListener(listener);
    }

    /**
     * 设置右边部分文字
     *
     * @param resId 资源id
     */
    public void setRightTvText(int resId)
    {
        setRightLayoutVisibility(VISIBLE);
        mTvRight.setVisibility(VISIBLE);
        mTvRight.setText(getResources().getText(resId));
    }

    /**
     * 设置右边部分文字
     *
     * @param s 字符串
     */
    public void setRightTvText(String s)
    {
        setRightLayoutVisibility(VISIBLE);
        mTvRight.setVisibility(VISIBLE);
        mTvRight.setText(s);
    }
}
