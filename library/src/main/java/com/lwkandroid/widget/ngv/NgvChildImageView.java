package com.lwkandroid.widget.ngv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @description: 图片子控件
 * @author:
 * @date: 2021/5/25 13:33
 */
public class NgvChildImageView extends ViewGroup
{
    private ImageView mContentImageView;
    private ImageView mDeleteImageView;

    private boolean mShowDeleteImageView;
    private int mContentImageWidth;
    private int mContentImageHeight;
    private int mDeleteImageSize;
    private float mDeleteImageSizeRatio;

    public NgvChildImageView(Context context)
    {
        this(context, null);
    }

    public NgvChildImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initChildView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        mDeleteImageSize = mShowDeleteImageView ? (int) (Math.min(measuredWidth, measuredHeight) * mDeleteImageSizeRatio) : 0;

        if (mDeleteImageView != null)
        {
            //删除按钮的尺寸
            //选择控件宽高中最小的值x比例
            int imgDeleteSpec = MeasureSpec.makeMeasureSpec(mDeleteImageSize, MeasureSpec.EXACTLY);
            mDeleteImageView.measure(imgDeleteSpec, imgDeleteSpec);
        }

        if (mContentImageView != null)
        {
            mContentImageWidth = measuredWidth - mDeleteImageSize;
            mContentImageHeight = measuredHeight - mDeleteImageSize;
            int imgContentWidthSpec = MeasureSpec.makeMeasureSpec(mContentImageWidth, MeasureSpec.EXACTLY);
            int imgContentHeightSpec = MeasureSpec.makeMeasureSpec(mContentImageHeight, MeasureSpec.EXACTLY);
            mContentImageView.measure(imgContentWidthSpec, imgContentHeightSpec);
        }

        setMeasuredDimension(measuredWidth, measuredHeight);

        //        Log.i("NgvChildImageView", "measure: mImageContentWidth=" + mImageContentWidth + " mImageContentHeight="
        //                + mImageContentHeight + " mImageDeleteSize=" + mImageDeleteSize + " measuredWidth=" + measuredWidth
        //                + " measuredHeight=" + measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int mMeasuredWidth = getMeasuredWidth();

        if (mDeleteImageView != null)
            mDeleteImageView.layout(mMeasuredWidth - mDeleteImageSize, 0, mMeasuredWidth, mDeleteImageSize);

        if (mContentImageView != null)
            mContentImageView.layout(mDeleteImageSize / 2, mDeleteImageSize / 2,
                    mDeleteImageSize / 2 + mContentImageWidth, mDeleteImageSize / 2 + mContentImageHeight);
    }

    public ImageView getImageContent()
    {
        return mContentImageView;
    }

    public ImageView getImageDelete()
    {
        return mDeleteImageView;
    }

    public void showDeleteImageView(boolean show)
    {
        this.mShowDeleteImageView = show;
        requestLayout();
    }

    public void setContentImageResource(int resId)
    {
        if (mContentImageView != null)
        {
            mContentImageView.setImageResource(resId);
        }
    }

    public void setContentImageScaleType(ImageView.ScaleType scaleType)
    {
        if (mContentImageView != null)
        {
            mContentImageView.setScaleType(scaleType);
        }
    }

    public void setDeleteImageResource(int resId)
    {
        if (mDeleteImageView != null)
        {
            mDeleteImageView.setImageResource(resId);
        }
    }

    public void setDeleteImageDrawable(Drawable deleteDrawable)
    {
        if (mDeleteImageView != null)
        {
            mDeleteImageView.setImageDrawable(deleteDrawable);
        }
    }

    public int getContentImageWidth()
    {
        return mContentImageWidth;
    }

    public int getContentImageHeight()
    {
        return mContentImageHeight;
    }

    public float getDeleteImageSizeRatio()
    {
        return mDeleteImageSizeRatio;
    }

    public void setDeleteImageSizeRatio(float sizeRatio)
    {
        this.mDeleteImageSizeRatio = sizeRatio;
    }

    private void initChildView(Context context)
    {
        mContentImageView = new ImageView(context);
        mDeleteImageView = new ImageView(context);

        addView(mContentImageView);
        addView(mDeleteImageView);
    }

    /*************************************************************状态恢复******************************************************************/

    @Override
    protected Parcelable onSaveInstanceState()
    {
        SavedViewState ss = new SavedViewState(super.onSaveInstanceState());
        ss.mShowDeleteImageView = mShowDeleteImageView;
        ss.mDeleteImageSizeRatio = mDeleteImageSizeRatio;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if (!(state instanceof SavedViewState))
        {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedViewState ss = (SavedViewState) state;
        super.onRestoreInstanceState(ss);
        mShowDeleteImageView = ss.mShowDeleteImageView;
        mDeleteImageSizeRatio = ss.mDeleteImageSizeRatio;
    }

    private static class SavedViewState extends BaseSavedState
    {
        private boolean mShowDeleteImageView;
        private float mDeleteImageSizeRatio;

        public SavedViewState(Parcelable superState)
        {
            super(superState);
        }

        public SavedViewState(Parcel source)
        {
            super(source);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                mShowDeleteImageView = source.readBoolean();
            } else
            {
                mShowDeleteImageView = source.readByte() == (byte) 1;
            }
            mDeleteImageSizeRatio = source.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags)
        {
            super.writeToParcel(out, flags);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                out.writeBoolean(mShowDeleteImageView);
            } else
            {
                out.writeByte(mShowDeleteImageView ? (byte) 1 : (byte) 0);
            }
            out.writeFloat(mDeleteImageSizeRatio);
        }

        public static final Parcelable.Creator<SavedViewState> CREATOR = new Creator<SavedViewState>()
        {
            @Override
            public SavedViewState createFromParcel(Parcel source)
            {
                return new SavedViewState(source);
            }

            @Override
            public SavedViewState[] newArray(int size)
            {
                return new SavedViewState[size];
            }
        };
    }
}
