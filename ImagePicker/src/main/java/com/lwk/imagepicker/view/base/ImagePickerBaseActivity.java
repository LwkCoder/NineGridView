package com.lwk.imagepicker.view.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.lwk.imagepicker.utils.ImageActivityManager;

/**
 * Function:图片选择相关activity的基类
 */
public abstract class ImagePickerBaseActivity extends Activity implements View.OnClickListener
{
    protected View mRootLayout;
    protected Handler mMainHanlder;

    protected void beforeOnCreate(Bundle savedInstanceState)
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        beforeOnCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        ImageActivityManager.getInstance().addActivityStack(this);
        mMainHanlder = new Handler(getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                onHandlerMessage(msg);
            }
        };
        beforeSetContentView();
        if (mRootLayout == null)
            mRootLayout = getLayoutInflater().inflate(setContentViewId(), null);
        setContentView(mRootLayout);
        beforeInitUI(savedInstanceState);
        initUI();
        initData();
    }

    protected void beforeSetContentView()
    {
    }

    protected abstract int setContentViewId();

    protected void beforeInitUI(Bundle savedInstanceState)
    {
    }

    protected abstract void initUI();

    protected void initData()
    {
    }

    protected void onHandlerMessage(Message msg)
    {
    }

    /**
     * 查找View
     */
    protected <T extends View> T findView(int resId)
    {
        return (T) mRootLayout.findViewById(resId);
    }

    /**
     * 添加点击监听到onClick()中
     */
    protected void addClick(View view)
    {
        if (view != null)
            view.setOnClickListener(this);
    }

    /**
     * 添加点击监听到onClick()中
     */
    protected void addClick(int id)
    {
        View view = findViewById(id);
        addClick(view);
    }

    /**
     * 弹出Toast
     */
    protected void showToast(final int resId)
    {
        showToast(getResources().getString(resId));
    }

    /**
     * 弹出Toast
     */
    protected void showToast(final String s)
    {
        mMainHanlder.post(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(ImagePickerBaseActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        onClick(v.getId(), v);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ImageActivityManager.getInstance().removeActivityStack(this);
    }

    protected abstract void onClick(int id, View v);
}
