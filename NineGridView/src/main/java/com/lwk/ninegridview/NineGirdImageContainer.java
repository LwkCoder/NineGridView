package com.lwk.ninegridview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by LWK
 * TODO 图片容器
 * 2016/12/1
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

        //测量并设置删除按钮的大小
        int delSize = mWidth * 1 / 5;
        int delMode = MeasureSpec.EXACTLY;
        int delSpec = MeasureSpec.makeMeasureSpec(delSize, delMode);
        mImgDelete.measure(delSpec, delSpec);
        //测量并设置图片大小
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
     * 设置图片ScanType
     */
    private void setScanType(ImageView.ScaleType scanType)
    {
        if (mImageView != null)
            mImageView.setScaleType(scanType);
    }

    /**
     * 设置是否开启删除模式
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
     * 当前是否为删除模式
     */
    public boolean isDeleteMode()
    {
        return mIsDeleteMode;
    }

    /**
     * 获取ImageView对象
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
