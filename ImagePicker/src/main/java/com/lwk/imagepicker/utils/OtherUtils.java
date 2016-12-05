package com.lwk.imagepicker.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;

/**
 * Function:帮助类
 */
public class OtherUtils
{
    /**
     * 判断是否相同
     */
    public static boolean isEquals(Object actual, Object expected)
    {
        return actual == expected || (actual == null ? expected == null : actual.equals(expected));
    }

    /**
     * 切换状态栏颜色
     */
    public static void changeStatusBarColor(Activity activity, int color)
    {
        //5.0以上改变状态栏颜色的方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            //5.0以上将状态栏变为全透明
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //改变状态栏颜色
            activity.getWindow().setStatusBarColor(color);
        }
        //4.4改变状态栏颜色的方法
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //向根布局中添加一个和状态栏一样高度的View
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
            statusBarView.setBackgroundColor(color);
            decorView.addView(statusBarView, lp);
        }
    }

    /**
     * 获得状态栏的高度
     */
    public static int getStatusBarHeight(Context context)
    {

        int statusHeight = -1;
        try
        {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取sd卡绝对路径
     */
    public static String getSdPath()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static boolean isEmpty(String str)
    {
        return (str == null || str.length() == 0);
    }

    public static boolean isNotEmpty(String str)
    {
        return (str != null && str.length() != 0);
    }

    public static boolean isEquals(String actual, String expected)
    {
        return actual == expected || (actual == null ? expected == null : actual.equals(expected));
    }

    /**
     * 获取应用默认缓存路径
     *
     * @param context 上下文
     */
    public static String getDefaultCachePath(Context context)
    {
        File defCacheFile = context.getExternalCacheDir();
        if (!defCacheFile.exists())
            defCacheFile.mkdirs();
        return defCacheFile.getAbsolutePath() + "/";
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context)
    {
        int width;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        return width;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context)
    {
        int height;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        height = outMetrics.heightPixels;
        return height;
    }

    /**
     * 检查sd卡是否存在
     */
    public static boolean isSdExist()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
