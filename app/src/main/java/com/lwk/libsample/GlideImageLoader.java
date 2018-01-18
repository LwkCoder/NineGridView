package com.lwk.libsample;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lwkandroid.widget.ninegridview.INineGridImageLoader;

/**
 * Created by LWK
 * TODO 九宫格的图片加载器
 * 2016/12/5
 */

public class GlideImageLoader implements INineGridImageLoader
{
    @Override
    public void displayNineGridImage(Context context, String url, ImageView imageView)
    {
        Glide.with(context).load(url).into(imageView);
    }

    @Override
    public void displayNineGridImage(Context context, String url, ImageView imageView, int width, int height)
    {
        Glide.with(context).load(url)
                .apply(new RequestOptions().override(width, height))
                .into(imageView);
    }
}
