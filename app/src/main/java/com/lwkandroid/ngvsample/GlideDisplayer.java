package com.lwkandroid.ngvsample;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.widget.ngv.INgvImageLoader;

/**
 * @description:
 * @author:
 * @date: 2021/5/27 14:45
 */
class GlideDisplayer implements INgvImageLoader<ImageBean>
{
    @Override
    public void load(ImageBean source, ImageView imageView, int width, int height)
    {
        Glide.with(imageView.getContext())
                .load(source.getImagePath())
                .apply(new RequestOptions().override(width, height))
                .into(imageView);
    }
}
