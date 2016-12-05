package com.lwk.imagepicker.utils;

import android.content.Context;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Function:图片加载器
 */
public interface ImagePickerDisplayer extends Serializable
{
    void display(Context context, ImageView imageView, String url, int maxX, int maxY);
}
