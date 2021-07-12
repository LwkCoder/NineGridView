package com.lwkandroid.widget.ngv;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

/**
 * @description: 默认UI的适配器实现
 * @author:
 * @date: 2021/5/25 10:40
 */
public class DefaultNgvAdapter<D> extends AbsNgvAdapter<ImageView, NgvChildImageView, D>
{
    private INgvImageLoader<D> mImageLoader;
    private OnChildClickedListener<D> mListener;

    public DefaultNgvAdapter(int maxDataSize, INgvImageLoader<D> mImageLoader)
    {
        super(maxDataSize);
        this.mImageLoader = mImageLoader;
    }

    public DefaultNgvAdapter(int maxDataSize, List<D> dataList, INgvImageLoader<D> mImageLoader)
    {
        super(maxDataSize, dataList);
        this.mImageLoader = mImageLoader;
    }

    @Override
    ImageView createPlusView(Context context)
    {
        return new ImageView(context);
    }

    @Override
    void bindPlusView(ImageView plusView, NgvAttrOptions attrOptions)
    {
        plusView.setImageDrawable(attrOptions.getIconPlusDrawable());
        plusView.setScaleType(ImageView.ScaleType.FIT_XY);
        plusView.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onPlusImageClicked(plusView, getDValueToLimited());
        });
    }

    @Override
    NgvChildImageView createContentView(Context context)
    {
        return new NgvChildImageView(context);
    }

    @Override
    void bindContentView(NgvChildImageView childView, D data, int position, NgvAttrOptions attrOptions)
    {
        childView.getImageContent().setScaleType(attrOptions.getImageScaleType());
        childView.setDeleteImageSizeRatio(attrOptions.getIconDeleteSizeRatio());
        childView.setDeleteImageDrawable(attrOptions.getIconDeleteDrawable());
        childView.showDeleteImageView(attrOptions.isEnableEditMode());
        if (mImageLoader != null)
        {
            mImageLoader.load(data, childView.getImageContent(), childView.getContentImageWidth(), childView.getContentImageHeight());
        }

        childView.getImageContent().setOnClickListener(v -> {
            if (mListener != null)
                mListener.onContentImageClicked(position, data, childView);
        });
        childView.getImageDelete().setOnClickListener(v -> {
            removeData(data);
            if (mListener != null)
                mListener.onImageDeleted(position, data);
        });
    }

    public OnChildClickedListener<D> getOnChildClickedListener()
    {
        return mListener;
    }

    public void setOnChildClickListener(OnChildClickedListener<D> listener)
    {
        this.mListener = listener;
    }

    public interface OnChildClickedListener<D>
    {
        void onPlusImageClicked(ImageView plusImageView, int dValueToLimited);

        void onContentImageClicked(int position, D data, NgvChildImageView childImageView);

        void onImageDeleted(int position, D data);
    }
}

