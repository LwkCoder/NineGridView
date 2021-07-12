package com.lwkandroid.widget.ngv;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * @description: Attr中配置项
 * @author:
 * @date: 2021/5/27 10:09
 */
class NgvAttrOptions
{
    private int mDividerSize;
    private int mSingleImageWidth;
    private int mSingleImageHeight;
    private Drawable mIconPlusDrawable;
    private Drawable mIconDeleteDrawable;
    private float mIconDeleteSizeRatio;
    private boolean mEnableEditMode;
    private int mHorizontalChildCount;
    private ImageView.ScaleType mImageScaleType;

    public int getDividerSize()
    {
        return mDividerSize;
    }

    public void setDividerSize(int dividerSize)
    {
        this.mDividerSize = dividerSize;
    }

    public int getSingleImageWidth()
    {
        return mSingleImageWidth;
    }

    public void setSingleImageWidth(int singleImageWidth)
    {
        this.mSingleImageWidth = singleImageWidth;
    }

    public int getSingleImageHeight()
    {
        return mSingleImageHeight;
    }

    public void setSingleImageHeight(int singleImageHeight)
    {
        this.mSingleImageHeight = singleImageHeight;
    }

    public Drawable getIconPlusDrawable()
    {
        return mIconPlusDrawable;
    }

    public void setIconPlusDrawable(Drawable iconPlusDrawable)
    {
        this.mIconPlusDrawable = iconPlusDrawable;
    }

    public Drawable getIconDeleteDrawable()
    {
        return mIconDeleteDrawable;
    }

    public void setIconDeleteDrawable(Drawable iconDeleteDrawable)
    {
        this.mIconDeleteDrawable = iconDeleteDrawable;
    }

    public float getIconDeleteSizeRatio()
    {
        return mIconDeleteSizeRatio;
    }

    public void setIconDeleteSizeRatio(float iconDeleteSizeRatio)
    {
        this.mIconDeleteSizeRatio = iconDeleteSizeRatio;
    }

    public boolean isEnableEditMode()
    {
        return mEnableEditMode;
    }

    public void setEnableEditMode(boolean enableEditMode)
    {
        this.mEnableEditMode = enableEditMode;
    }

    public int getHorizontalChildCount()
    {
        return mHorizontalChildCount;
    }

    public void setHorizontalChildCount(int count)
    {
        this.mHorizontalChildCount = count;
    }

    public ImageView.ScaleType getImageScaleType()
    {
        return mImageScaleType;
    }

    public void setImageScaleType(ImageView.ScaleType scaleType)
    {
        this.mImageScaleType = scaleType;
    }

}
