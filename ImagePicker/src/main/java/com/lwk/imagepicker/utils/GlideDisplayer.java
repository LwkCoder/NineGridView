package com.lwk.imagepicker.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lwk.imagepicker.R;

/**
 * Function:Glide图片加载器
 * Created by LWK
 * 2016/6/27
 */
public class GlideDisplayer implements ImagePickerDisplayer
{
    @Override
    public void display(Context context, ImageView imageView, String url, int maxX, int maxY)
    {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(R.drawable.glide_default_picture)
                .error(R.drawable.glide_default_picture)
                .override(maxX, maxY)
                .into(imageView);
    }
}
