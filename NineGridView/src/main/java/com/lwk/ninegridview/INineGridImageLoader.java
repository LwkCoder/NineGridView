package com.lwk.ninegridview;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by LWK
 * TODO 加载图片的接口
 * 2016/12/1
 */

public interface INineGridImageLoader
{
    void displayNineGridImage(Context context, String url, ImageView imageView);

    void displayNineGridImage(Context context, String url, ImageView imageView, int width, int height);
}
