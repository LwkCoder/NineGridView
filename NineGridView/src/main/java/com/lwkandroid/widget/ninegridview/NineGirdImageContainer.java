package com.lwkandroid.widget.ninegridview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;


/**
 * 图片自定义控件
 *
 * @author LWK
 */
public final class NineGirdImageContainer extends FrameLayout
{
    private int mWidth = 0, mHeight = 0;
    private NineGridImageView mImageView;
    private ImageView mImgDelete;
    private boolean mIsDeleteMode;
    private onClickDeleteListener mListener;
    private int mImageWidth, mImageHeight;
    private int mIcDelete = R.drawable.ic_ngv_delete;
    private float mRatio = 0.35f;

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
        mImgDelete.setImageResource(mIcDelete);
        mImgDelete.setOnClickListener(view -> {
            if (mListener != null)
            {
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

        //设置删除图片的尺寸
        int delSize = (int) (mWidth * mRatio);
        int delMode = MeasureSpec.EXACTLY;
        int delSpec = MeasureSpec.makeMeasureSpec(delSize, delMode);
        mImgDelete.measure(delSpec, delSpec);
        //根据模式设置显示图片的尺寸
        mImageWidth = 0;
        mImageHeight = 0;
        int imgMode = MeasureSpec.EXACTLY, imgWidthSpec = 0, imgHeightSpec = 0;
        if (mIsDeleteMode)
        {
            mImageWidth = mWidth * 4 / 5;
            mImageHeight = mHeight * 4 / 5;
        } else
        {
            mImageWidth = mWidth;
            mImageHeight = mHeight;
        }
        imgWidthSpec = MeasureSpec.makeMeasureSpec(mImageWidth, imgMode);
        imgHeightSpec = MeasureSpec.makeMeasureSpec(mImageHeight, imgMode);
        mImageView.measure(imgWidthSpec, imgHeightSpec);
    }

    public int getImageWidth()
    {
        return mImageWidth;
    }

    public int getImageHeight()
    {
        return mImageHeight;
    }

    /**
     * 设置显示图片的ScaleType
     */
    private void setScaleType(ImageView.ScaleType scanType)
    {
        if (mImageView != null)
        {
            mImageView.setScaleType(scanType);
        }
    }

    /**
     * 设置是否为删除模式
     */
    public void setIsDeleteMode(boolean b)
    {
        this.mIsDeleteMode = b;
        if (mIsDeleteMode)
        {
            mImgDelete.setVisibility(VISIBLE);
        } else
        {
            mImgDelete.setVisibility(GONE);
        }
        requestLayout();
    }

    /**
     * 设置删除图片的资源id
     */
    public void setDeleteIcon(int resId)
    {
        this.mIcDelete = resId;
        if (mImgDelete != null)
        {
            mImgDelete.setImageResource(mIcDelete);
        }
    }

    /**
     * 当前是否为删除模式
     */
    public boolean isDeleteMode()
    {
        return mIsDeleteMode;
    }

    /**
     * 设置删除图片的尺寸占父容器比例
     *
     * @param ratio
     */
    public void setRatioOfDeleteIcon(float ratio)
    {
        this.mRatio = ratio;
    }

    /**
     * 获取实际显示图片的ImageView
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
