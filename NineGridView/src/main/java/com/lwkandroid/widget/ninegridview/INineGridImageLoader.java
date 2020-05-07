package com.lwkandroid.widget.ninegridview;

import android.content.Context;
import android.widget.ImageView;

/**
 * 图片加载器接口
 *
 * @author LWK
 */
public interface INineGridImageLoader
{
    void displayNineGridImage(Context context, String url, ImageView imageView);

    void displayNineGridImage(Context context, String url, ImageView imageView, int width, int height);
}
