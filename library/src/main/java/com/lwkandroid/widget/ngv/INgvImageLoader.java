package com.lwkandroid.widget.ngv;

import android.widget.ImageView;

/**
 * @description: 加载图片的接口
 * @author:
 * @date: 2021/5/26 15:46
 */
public interface INgvImageLoader<D>
{
    /**
     * 执行图片加载的方法
     *
     * @param source    图片数据源
     * @param imageView ImageView
     * @param width     宽度
     * @param height    高度
     */
    void load(D source, ImageView imageView, int width, int height);
}
