package com.lwk.imagepicker;

import com.lwk.imagepicker.model.ImagePickerMode;
import com.lwk.imagepicker.utils.GlideDisplayer;
import com.lwk.imagepicker.utils.ImagePickerDisplayer;
import com.lwk.imagepicker.utils.OtherUtils;

import java.io.File;

/**
 * Function:选择图片时的参数
 */
public class ImagePickerOptions
{
    /**
     * 选择图片的模式,默认单选
     */
    private ImagePickerMode mPickerMode = ImagePickerMode.SINGLE;

    /**
     * 是否展示相机入口，默认展示
     */
    private boolean mShowCamera = true;

    /**
     * 最大选取数量，默认为1
     */
    private int mLimitNum = 1;

    /**
     * 是否需要裁剪，默认不需要
     */
    private boolean mNeedCrop = false;

    /**
     * 图片加载器，默认为Glide加载
     */
    private ImagePickerDisplayer mDisplayer = new GlideDisplayer();

    /**
     * 缓存地址
     */
    private String mCachePath;

    protected ImagePickerOptions()
    {
        //默认缓存地址
        mCachePath = new StringBuffer()
                .append(OtherUtils.getSdPath())
                .append("/imagepicker/").toString().trim();
    }

    public ImagePickerMode getPickerMode()
    {
        return mPickerMode;
    }

    public void setPickerMode(ImagePickerMode mPickerMode)
    {
        this.mPickerMode = mPickerMode;
    }

    public boolean isShowCamera()
    {
        return mShowCamera;
    }

    public void setShowCamera(boolean mShowCamera)
    {
        this.mShowCamera = mShowCamera;
    }

    public int getLimitNum()
    {
        return mLimitNum;
    }

    public void setLimitNum(int mLimitNum)
    {
        this.mLimitNum = mLimitNum;
    }

    public boolean isNeedCrop()
    {
        return mNeedCrop;
    }

    public void setNeedCrop(boolean mNeedCrop)
    {
        this.mNeedCrop = mNeedCrop;
    }

    public ImagePickerDisplayer getDisplayer()
    {
        return mDisplayer;
    }

    public void setDisplayer(ImagePickerDisplayer mDisplayer)
    {
        this.mDisplayer = mDisplayer;
    }

    public String getCachePath()
    {
        File file = new File(mCachePath);
        if (!file.exists())
            file.mkdirs();
        return mCachePath;
    }

    public void setCachePath(String mCachePath)
    {
        this.mCachePath = mCachePath;
    }

    public static class Builder
    {
        private ImagePickerOptions mOptions;

        public Builder()
        {
            mOptions = new ImagePickerOptions();
        }

        public Builder pickMode(ImagePickerMode mode)
        {
            mOptions.setPickerMode(mode);
            return this;
        }

        public Builder showCamera(boolean b)
        {
            mOptions.setShowCamera(b);
            return this;
        }

        public Builder limitNum(int num)
        {
            mOptions.setLimitNum(num);
            return this;
        }

        public Builder needCrop(boolean b)
        {
            mOptions.setNeedCrop(b);
            return this;
        }

        public Builder displayer(ImagePickerDisplayer displayer)
        {
            mOptions.setDisplayer(displayer);
            return this;
        }

        public Builder cachePath(String path)
        {
            mOptions.setCachePath(path);
            return this;
        }

        public ImagePickerOptions build()
        {
            return mOptions;
        }
    }
}
