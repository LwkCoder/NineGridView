package com.lwk.imagepicker.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:Activity管理类
 */
public class ImageActivityManager
{
    private ImageActivityManager()
    {
    }

    private static final class ImageActivityManagerHolder
    {
        private static ImageActivityManager instance = new ImageActivityManager();
    }

    public static ImageActivityManager getInstance()
    {
        return ImageActivityManagerHolder.instance;
    }

    private List<Activity> mActivityStack = new ArrayList<>();

    public List<Activity> getAllActivity()
    {
        return mActivityStack;
    }

    /**
     * 添加activity栈引用
     */
    public void addActivityStack(Activity activity)
    {
        mActivityStack.add(activity);
    }

    /**
     * 移除activity栈引用
     */
    public void removeActivityStack(Activity activity)
    {
        mActivityStack.remove(activity);
    }

    /**
     * 关闭所有activity
     */
    public void finishAllActivity()
    {
        for (Activity activity : mActivityStack)
        {
            activity.finish();
        }
    }
}
