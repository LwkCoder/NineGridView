package com.lwk.ninegridview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LWK
 * TODO 九宫格图片显示器
 * 2016/11/30
 */
public class NineGridView extends ViewGroup
{
    //单张图片尺寸，默认100dp
    private int mSingleImageSize = 100;
    //单张图片宽高比，默认1：1
    private float mSingleImageRatio = 1.0f;
    //间隔大小，默认3dp
    private int mSpaceSize = 3;
    //图片尺寸
    private int mImageWidth, mImageHeight;
    //列数，默认3列
    private int mColumnCount = 3;
    //行数，根据数据和列数动态计算
    private int mRawCount;
    //图片加载接口
    private INineGridImageLoader mImageLoader;
    //图片数据
    private List<NineGridBean> mDataList = new ArrayList<>();
    //添加按钮
    private NineGridImageView mImgAddData;
    //子View点击监听
    private onItemClickListener mListener;
    //是否为编辑模式
    private boolean mIsEditMode;
    //最大显示数量，默认9张
    private int mMaxNum = 9;

    public NineGridView(Context context)
    {
        super(context);
        initParams(context, null);
    }

    public NineGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initParams(context, attrs);
    }

    private void initParams(Context context, AttributeSet attrs)
    {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mSingleImageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mSingleImageSize, dm);
        mSpaceSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mSpaceSize, dm);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NineGridView);
        if (ta != null)
        {
            int count = ta.getIndexCount();
            for (int i = 0; i < count; i++)
            {
                int index = ta.getIndex(i);
                if (index == R.styleable.NineGridView_sapce_size)
                    mSpaceSize = ta.getDimensionPixelSize(index, mSpaceSize);
                else if (index == R.styleable.NineGridView_single_image_ratio)
                    mSingleImageRatio = ta.getFloat(index, mSingleImageRatio);
                else if (index == R.styleable.NineGridView_single_image_size)
                    mSingleImageSize = ta.getDimensionPixelSize(index, mSingleImageSize);
                else if (index == R.styleable.NineGridView_column_count)
                    mColumnCount = ta.getInteger(index, mColumnCount);
                else if (index == R.styleable.NineGridView_is_edit_mode)
                    mIsEditMode = ta.getBoolean(index, mIsEditMode);
                else if (index == R.styleable.NineGridView_max_num)
                    mMaxNum = ta.getInteger(R.styleable.NineGridView_max_num, mMaxNum);
            }
            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int resWidth = 0, resHeight = 0;

        //测量宽度
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        //可用宽度
        int totalWidth = measureWidth - getPaddingLeft() - getPaddingRight();

        if (canShowAddMore())
        {
            //编辑模式下，所有子View大小一样
            mImageWidth = mImageHeight = (totalWidth - (mColumnCount - 1) * mSpaceSize) / mColumnCount;
            int childCount = getChildCount();
            if (childCount < mColumnCount)
                resWidth = mImageWidth * childCount + (childCount - 1) * mSpaceSize + getPaddingLeft() + getPaddingRight();
            else
                resWidth = mImageWidth * mColumnCount + (mColumnCount - 1) * mSpaceSize + getPaddingLeft() + getPaddingRight();
            resHeight = mImageHeight * mRawCount + (mRawCount - 1) * mSpaceSize + getPaddingTop() + getPaddingBottom();
        } else
        {
            //非编辑模式下，控件大小取决于数据量
            int dataCount = mDataList.size();
            if (mDataList != null && dataCount > 0)
            {
                if (dataCount == 1)
                {
                    mImageWidth = mSingleImageSize > totalWidth ? totalWidth : mSingleImageSize;
                    mImageHeight = (int) (mImageWidth / mSingleImageRatio);
                    //矫正图片显示区域大小，不允许超过最大显示范围
                    if (mImageHeight > mSingleImageSize)
                    {
                        float ratio = mSingleImageSize * 1.0f / mImageHeight;
                        mImageWidth = (int) (mImageWidth * ratio);
                        mImageHeight = mSingleImageSize;
                    }
                    resWidth = mImageWidth + getPaddingLeft() + getPaddingRight();
                    resHeight = mImageHeight + getPaddingTop() + getPaddingBottom();
                } else
                {
                    mImageWidth = mImageHeight = (totalWidth - (mColumnCount - 1) * mSpaceSize) / mColumnCount;
                    if (dataCount < mColumnCount)
                        resWidth = mImageWidth * dataCount + (dataCount - 1) * mSpaceSize + getPaddingLeft() + getPaddingRight();
                    else
                        resWidth = mImageWidth * mColumnCount + (mColumnCount - 1) * mSpaceSize + getPaddingLeft() + getPaddingRight();
                    resHeight = mImageHeight * mRawCount + (mRawCount - 1) * mSpaceSize + getPaddingTop() + getPaddingBottom();
                }
            }
        }

        setMeasuredDimension(resWidth, resHeight);

        //测量子view
        int childrenCount = getChildCount();
        for (int index = 0; index < childrenCount; index++)
        {
            View childView = getChildAt(index);
            int childWidth = mImageWidth;
            int childHeight = mImageHeight;
            int childMode = MeasureSpec.EXACTLY;
            int childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, childMode);
            int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, childMode);
            childView.measure(childWidthSpec, childHeightSpec);
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++)
        {
            View childrenView = getChildAt(index);
            int rowNum = index / mColumnCount;
            int columnNum = index % mColumnCount;
            int left = (mImageWidth + mSpaceSize) * columnNum + getPaddingLeft();
            int top = (mImageHeight + mSpaceSize) * rowNum + getPaddingTop();
            int right = left + mImageWidth;
            int bottom = top + mImageHeight;
            childrenView.layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        setDataList(null);
    }

    /**
     * 设置数据
     */
    public void setDataList(List<NineGridBean> dataList)
    {
        mDataList.clear();
        //添加的数据量不能超过设置的最大数量
        if (dataList != null && dataList.size() > 0)
        {
            if (dataList.size() <= mMaxNum)
                mDataList.addAll(dataList);
            else
                mDataList.addAll(dataList.subList(0, mMaxNum - 1));
        }
        clearAllViews();
        calRawAndColumn();
        initChildViews();
        requestLayout();
    }

    /**
     * 增加数据
     */
    public void addDataList(List<NineGridBean> dataList)
    {
        if (mDataList.size() >= mMaxNum)
            return;
        //添加的数据量不能超过设置的最大数量
        int cha = mMaxNum - mDataList.size();
        if (dataList.size() <= cha)
            mDataList.addAll(dataList);
        else
            mDataList.addAll(dataList.subList(0, cha - 1));

        clearAllViews();
        calRawAndColumn();
        initChildViews();
        requestLayout();
    }

    //计算行数、列数
    private void calRawAndColumn()
    {
        int childSize = mDataList.size();
        //编辑模式下需要在后面显示“+”号，所以子view数量+1
        if (canShowAddMore())
            childSize++;

        //计算行数
        if (childSize == 0)
        {
            mRawCount = 0;
        } else if (childSize <= mColumnCount)
        {
            mRawCount = 1;
        } else
        {
            if (childSize % mColumnCount == 0)
                mRawCount = childSize / mColumnCount;
            else
                mRawCount = childSize / mColumnCount + 1;
        }
    }

    //初始化View
    private void initChildViews()
    {
        //添加图片container
        int dataSize = mDataList.size();
        for (int i = 0; i < dataSize; i++)
        {
            final NineGridBean gridBean = mDataList.get(i);
            final NineGirdImageContainer imageContainer = new NineGirdImageContainer(getContext());
            if (mImageLoader != null)
                mImageLoader.displayNineGridImage(getContext(), gridBean.getThumbUrl(), imageContainer.getImageView());
            else
                Log.w("NineGridView", "You'd better set a imageloader!!!!");

            imageContainer.setIsDeleteMode(mIsEditMode);
            final int position = i;
            imageContainer.getImageView().setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (mListener != null)
                        mListener.onNineGirdItemClick(position, gridBean, imageContainer);
                }
            });
            imageContainer.setOnClickDeleteListener(new NineGirdImageContainer.onClickDeleteListener()
            {
                @Override
                public void onClickDelete()
                {
                    mDataList.remove(position);
                    clearAllViews();
                    calRawAndColumn();
                    initChildViews();
                    requestLayout();
                    if (mListener != null)
                        mListener.onNineGirdItemDeleted(position, gridBean, imageContainer);
                }
            });
            addView(imageContainer, position);
        }

        setIsEditMode(mIsEditMode);
    }

    /**
     * 设置是否显示为编辑模式
     */
    public void setIsEditMode(boolean b)
    {
        mIsEditMode = b;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            View childView = getChildAt(i);
            if (childView instanceof NineGirdImageContainer)
                ((NineGirdImageContainer) childView).setIsDeleteMode(b);
        }

        //添加“+”号
        if (canShowAddMore())
        {
            if (mImgAddData != null)
                return;

            mImgAddData = new NineGridImageView(getContext());
            mImgAddData.setImageResource(R.drawable.ic_ninegrid_addmore);
            int padddingSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10
                    , getContext().getResources().getDisplayMetrics());
            mImgAddData.setPadding(padddingSize, padddingSize, padddingSize, padddingSize);
            mImgAddData.setScaleType(ImageView.ScaleType.FIT_XY);
            mImgAddData.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (mListener != null)
                        mListener.onNineGirdAddMoreClick(mMaxNum - mDataList.size());
                }
            });
            addView(mImgAddData);
        } else
        {
            if (mImgAddData != null)
                removeView(mImgAddData);
            mImgAddData = null;
        }

        calRawAndColumn();
        requestLayout();
    }

    //是否可以显示“+”号
    private boolean canShowAddMore()
    {
        return mIsEditMode && mDataList.size() < mMaxNum;
    }

    /**
     * 设置图片加载器
     */
    public void setImageLoader(INineGridImageLoader loader)
    {
        this.mImageLoader = loader;
    }

    /**
     * 设置列数
     */
    public void setColumnCount(int columnCount)
    {
        this.mColumnCount = columnCount;
        calRawAndColumn();
        requestLayout();
    }

    /**
     * 设置最大选取数量
     */
    public void setMaxNum(int maxNum)
    {
        this.mMaxNum = maxNum;
    }

    /**
     * 设置图片展示时的间隔大小
     *
     * @param dpValue dp值
     */
    public void setSpcaeSize(int dpValue)
    {
        this.mSpaceSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue
                , getContext().getResources().getDisplayMetrics());
    }

    /**
     * 设置单张图片展示时的尺寸
     *
     * @param dpValue dp值
     */
    public void setSingleImageSize(int dpValue)
    {
        this.mSingleImageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue
                , getContext().getResources().getDisplayMetrics());
    }

    /**
     * 设置单张图片展示时的宽高比
     *
     * @param ratio 宽高比
     */
    public void setSingleImageRatio(float ratio)
    {
        this.mSingleImageRatio = ratio;
    }

    //清空所有View
    private void clearAllViews()
    {
        removeAllViews();
        if (mImgAddData != null)
            removeView(mImgAddData);
        mImgAddData = null;
    }

    /**
     * 获取当前数据
     */
    public List<NineGridBean> getDataList()
    {
        return mDataList;
    }

    /**
     * 设置子View点击事件监听
     */
    public void setOnItemClickListener(onItemClickListener l)
    {
        this.mListener = l;
    }

    public interface onItemClickListener
    {
        /**
         * 点击“+”号
         *
         * @param cha 当前数据量与最大数量的差值
         */
        void onNineGirdAddMoreClick(int cha);

        /**
         * 点击图片item
         *
         * @param position       位置
         * @param gridBean       图片数据
         * @param imageContainer 图片容器
         */
        void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer);

        /**
         * 图片被删除
         *
         * @param position       位置
         * @param gridBean       图片数据
         * @param imageContainer 图片容器
         */
        void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer);
    }

    /*****************************************************
     * 状态缓存
     ****************************************************************/
    private final String SINGLE_IMAGE_SIZE = "singleImageSize";
    private final String SINGLE_IMAGE_RATIO = "singleImgaeRatio";
    private final String SPACE_SIZE = "spaceSize";
    private final String COLUMN_COUNT = "columnCount";
    private final String RAW_COUNT = "rawCount";
    private final String MAX_NUM = "maxNum";
    private final String EDIT_MODE = "delMode";
    private final String DATALIST = "datalist";

    @Override
    protected Parcelable onSaveInstanceState()
    {
        //这里必须调用super.onSaveInstanceState()
        //否则会报错:Derived class did not call super.onSaveInstanceState()
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putInt(SINGLE_IMAGE_SIZE, mSingleImageSize);
        bundle.putFloat(SINGLE_IMAGE_RATIO, mSingleImageRatio);
        bundle.putInt(SPACE_SIZE, mSpaceSize);
        bundle.putInt(COLUMN_COUNT, mColumnCount);
        bundle.putInt(RAW_COUNT, mRawCount);
        bundle.putInt(MAX_NUM, mMaxNum);
        bundle.putBoolean(EDIT_MODE, mIsEditMode);
        bundle.putParcelableArrayList(DATALIST, (ArrayList<? extends Parcelable>) mDataList);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        super.onRestoreInstanceState(state);
        Bundle bundle = (Bundle) state;
        this.mSingleImageSize = bundle.getInt(SINGLE_IMAGE_SIZE);
        this.mSingleImageRatio = bundle.getFloat(SINGLE_IMAGE_RATIO);
        this.mSpaceSize = bundle.getInt(SPACE_SIZE);
        this.mColumnCount = bundle.getInt(COLUMN_COUNT);
        this.mRawCount = bundle.getInt(RAW_COUNT);
        this.mMaxNum = bundle.getInt(MAX_NUM);
        this.mIsEditMode = bundle.getBoolean(EDIT_MODE);
        this.mDataList = bundle.getParcelableArrayList(DATALIST);
    }
}
