package com.lwkandroid.ngvsample;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lwkandroid.widget.ninegridview.INgvImageLoader;

/**
 * 图片加载器，必须设置
 */
class GlideDisplayer2 implements INgvImageLoader<String>
{
    @Override
    public void load(String source, ImageView imageView, int width, int height)
    {
        Glide.with(imageView.getContext())
                .load(source)
                .apply(new RequestOptions().override(width, height))
                .into(imageView);
    }
}
