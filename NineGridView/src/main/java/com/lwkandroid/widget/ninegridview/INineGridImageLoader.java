package com.lwkandroid.widget.ninegridview;

import android.content.Context;
import android.widget.ImageView;

/**
 *The interface of imageloader
 */

public interface INineGridImageLoader
{
    void displayNineGridImage(Context context, String url, ImageView imageView);

    void displayNineGridImage(Context context, String url, ImageView imageView, int width, int height);
}
