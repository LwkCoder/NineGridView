package com.lwk.ninegridview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * ImageContainer
 */

public class NineGirdImageContainer extends FrameLayout
{
    private int mWidth = 0, mHeight = 0;
    private NineGridImageView mImageView;
    private ImageView mImgDelete;
    private boolean mIsDeleteMode;
    private onClickDeleteListener mListener;

    public NineGirdImageContainer(Context context)
    {
        super(context);
        init(context, null);
    }

    public NineGirdImageContainer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        setWillNotDraw(false);
        inflate(context, R.layout.layout_ninegrid_image_container, this);
        mImageView = (NineGridImageView) findViewById(R.id.img_ninegrid_imagecontainer_content);
        mImgDelete = (ImageView) findViewById(R.id.img_ninegrid_imagecontainer_delete);
        mImgDelete.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mListener != null)
                    mListener.onClickDelete();
            }
        });
        setIsDeleteMode(mIsDeleteMode);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);

        //Measure the size of the delete button
        int delSize = mWidth * 1 / 5;
        int delMode = MeasureSpec.EXACTLY;
        int delSpec = MeasureSpec.makeMeasureSpec(delSize, delMode);
        mImgDelete.measure(delSpec, delSpec);
        //Measure the size of imageView
        int imgWidthSize = 0;
        int imgHeightSize = 0;
        int imgMode = MeasureSpec.EXACTLY;
        int imgWidthSpec = 0;
        int imgHeightSpec = 0;
        if (mIsDeleteMode)
        {
            imgWidthSize = mWidth * 4 / 5;
            imgHeightSize = mHeight * 4 / 5;
        } else
        {
            imgWidthSize = mWidth;
            imgHeightSize = mHeight;
        }
        imgWidthSpec = MeasureSpec.makeMeasureSpec(imgWidthSize, imgMode);
        imgHeightSpec = MeasureSpec.makeMeasureSpec(imgHeightSize, imgMode);
        mImageView.measure(imgWidthSpec, imgHeightSpec);
    }

    /**
     * Set scantype of imageView
     */
    private void setScanType(ImageView.ScaleType scanType)
    {
        if (mImageView != null)
            mImageView.setScaleType(scanType);
    }

    /**
     * Set if is in the delete mode
     */
    public void setIsDeleteMode(boolean b)
    {
        this.mIsDeleteMode = b;
        if (mIsDeleteMode)
            mImgDelete.setVisibility(VISIBLE);
        else
            mImgDelete.setVisibility(GONE);
        requestLayout();
    }

    /**
     * If is in the delete mode
     */
    public boolean isDeleteMode()
    {
        return mIsDeleteMode;
    }

    /**
     * Get imageView object
     */
    public ImageView getImageView()
    {
        return mImageView;
    }

    public interface onClickDeleteListener
    {
        void onClickDelete();
    }

    public void setOnClickDeleteListener(onClickDeleteListener listener)
    {
        this.mListener = listener;
    }
}
