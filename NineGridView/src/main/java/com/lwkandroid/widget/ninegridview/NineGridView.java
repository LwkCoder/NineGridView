package com.lwkandroid.widget.ninegridview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lwk.ninegridview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 九宫格控件
 *
 * @author LWK
 */
public class NineGridView extends ViewGroup
{
    /**
     * 只有单张图片时，ImageView的宽度
     */
    private int mSingleImageWidth = 0;
    /**
     * 只有单张图片时，ImageView的宽高比
     */
    private float mSingleImageRatio = 1.0f;
    /**
     * 间距大小
     */
    private int mSpaceSize = 3;
    /**
     * 多张图片时，每个ImageView的宽高
     */
    private int mImageWidth, mImageHeight;
    /**
     * 列数
     */
    private int mColumnCount = 3;
    /**
     * 行数
     */
    private int mRawCount;
    /**
     * 图片加载器接口
     */
    private INineGridImageLoader mImageLoader;
    /**
     * 图片数据
     */
    private List<NineGridBean> mDataList = new ArrayList<>();
    /**
     * “+”号图片
     */
    private NineGridImageView mImgAddData;
    /**
     * 子控件点击监听
     */
    private onItemClickListener mListener;
    /**
     * 当前是否为编辑模式
     */
    private boolean mIsEditMode;
    /**
     * 最大显示图片数量
     */
    private int mMaxNum = 9;
    /**
     * “+”号图片资源id
     */
    private int mIcAddMoreResId = R.drawable.ic_ngv_add_pic;
    /**
     * 删除图片的资源id
     */
    private int mIcDelete = R.drawable.ic_ngv_delete;
    /**
     * 删除图片的尺寸比例
     */
    private float mRatioOfDelete = 0.25f;

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
        mSingleImageWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mSingleImageWidth, dm);
        mSpaceSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mSpaceSize, dm);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NineGridView);
        if (ta != null)
        {
            int count = ta.getIndexCount();
            for (int i = 0; i < count; i++)
            {
                int index = ta.getIndex(i);
                if (index == R.styleable.NineGridView_sapce_size)
                {
                    mSpaceSize = ta.getDimensionPixelSize(index, mSpaceSize);
                } else if (index == R.styleable.NineGridView_single_image_ratio)
                {
                    mSingleImageRatio = ta.getFloat(index, mSingleImageRatio);
                } else if (index == R.styleable.NineGridView_single_image_size)
                {
                    mSingleImageWidth = ta.getDimensionPixelSize(index, mSingleImageWidth);
                } else if (index == R.styleable.NineGridView_column_count)
                {
                    mColumnCount = ta.getInteger(index, mColumnCount);
                } else if (index == R.styleable.NineGridView_is_edit_mode)
                {
                    mIsEditMode = ta.getBoolean(index, mIsEditMode);
                } else if (index == R.styleable.NineGridView_max_num)
                {
                    mMaxNum = ta.getInteger(index, mMaxNum);
                } else if (index == R.styleable.NineGridView_icon_addmore)
                {
                    mIcAddMoreResId = ta.getResourceId(index, mIcAddMoreResId);
                } else if (index == R.styleable.NineGridView_icon_delete)
                {
                    mIcDelete = ta.getResourceId(index, mIcDelete);
                } else if (index == R.styleable.NineGridView_delete_ratio)
                {
                    mRatioOfDelete = ta.getFloat(index, mRatioOfDelete);
                }
            }
            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //最终整个控件的所需宽高
        int requiredWidth, requiredHeight;
        //获取当前可用最大宽度
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int totalWidth = measureWidth - getPaddingLeft() - getPaddingRight();

        //多张图片时，每个子控件的建议尺寸
        int childCount = getChildCount();
        int suggestWidth = (totalWidth - (mColumnCount - 1) * mSpaceSize) / mColumnCount;

        if (canShowAddMore())
        {
            //编辑模式下，每个子控件的尺寸=建议尺寸
            mImageWidth = mImageHeight = suggestWidth;
            if (childCount < mColumnCount)
            {
                requiredWidth = mImageWidth * childCount + (childCount - 1) * mSpaceSize + getPaddingLeft() + getPaddingRight();
            } else
            {
                requiredWidth = mImageWidth * mColumnCount + (mColumnCount - 1) * mSpaceSize + getPaddingLeft() + getPaddingRight();
            }
            requiredHeight = mImageHeight * mRawCount + (mRawCount - 1) * mSpaceSize + getPaddingTop() + getPaddingBottom();
        } else
        {
            //非编辑模式下，每个控件的尺寸需要根据实际情况决定

            if (getDataList().isEmpty())
            {
                //没有数据的时候，每个子控件尺寸=0
                mImageWidth = mImageHeight = 0;
                requiredWidth = getPaddingLeft() + getPaddingRight();
                requiredHeight = getPaddingTop() + getPaddingBottom();
            } else if (getDataList().size() == 1)
            {
                //只有一张图片时
                //没有额外设置单张图片尺寸时使用建议尺寸，否则需要取最小值
                if (mSingleImageWidth <= 0)
                {
                    mImageWidth = mImageHeight = suggestWidth;
                } else
                {
                    mImageWidth = Math.min(mSingleImageWidth, totalWidth);
                    mImageHeight = (int) (mImageWidth / mSingleImageRatio);
                }
                requiredWidth = mImageWidth + getPaddingLeft() + getPaddingRight();
                requiredHeight = mImageHeight + getPaddingTop() + getPaddingBottom();
            } else
            {
                //多张图片时，每个子控件的尺寸=建议尺寸
                mImageWidth = mImageHeight = suggestWidth;
                if (childCount < mColumnCount)
                {
                    requiredWidth = mImageWidth * childCount + (childCount - 1) * mSpaceSize + getPaddingLeft() + getPaddingRight();
                } else
                {
                    requiredWidth = mImageWidth * mColumnCount + (mColumnCount - 1) * mSpaceSize + getPaddingLeft() + getPaddingRight();
                }
                requiredHeight = mImageHeight * mRawCount + (mRawCount - 1) * mSpaceSize + getPaddingTop() + getPaddingBottom();
            }
        }

        //设置最终该控件宽高
        setMeasuredDimension(requiredWidth, requiredHeight);

        //设置每个子控件宽高
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
     * 设置数据集合
     */
    public void setDataList(List<NineGridBean> dataList)
    {
        mDataList.clear();
        //不允许超过最大值
        if (dataList != null && !dataList.isEmpty())
        {
            if (dataList.size() <= mMaxNum)
            {
                mDataList.addAll(dataList);
            } else
            {
                mDataList.addAll(dataList.subList(0, mMaxNum - 1));
            }
        }
        clearAllViews();
        addChildViews(mDataList);
    }

    /**
     * 添加数据集合
     */
    public void addDataList(List<NineGridBean> dataList)
    {
        if (mDataList.size() >= mMaxNum || dataList == null || dataList.isEmpty())
        {
            return;
        }
        int cha = mMaxNum - mDataList.size();
        List<NineGridBean> availableList;
        if (cha >= dataList.size())
        {
            availableList = dataList;
        } else
        {
            availableList = dataList.subList(0, cha - 1);
        }
        mDataList.addAll(availableList);
        addChildViews(availableList);
    }

    /**
     * 设置是否为编辑模式
     */
    public void setIsEditMode(boolean b)
    {
        mIsEditMode = b;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            View childView = getChildAt(i);
            if (childView instanceof NineGirdImageContainer)
            {
                ((NineGirdImageContainer) childView).setIsDeleteMode(b);
            }
        }

        if (canShowAddMore())
        {
            if (mImgAddData != null)
            {
                return;
            }
            addInAddMoreView();
        } else
        {
            removeAddMoreView();
        }

        calRawAndColumn();
        requestLayout();
    }

    /**
     * 设置图片加载器实现类
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
    }

    /**
     * 设置删除图片尺寸在父容器内的比例
     */
    public void setRatioOfDeleteIcon(float ratio)
    {
        this.mRatioOfDelete = ratio;
    }

    /**
     * 获取当前数据集合
     */
    public List<NineGridBean> getDataList()
    {
        return mDataList;
    }

    /**
     * 设置“+”号图片的资源id
     */
    public void setIcAddMoreResId(int resId)
    {
        this.mIcAddMoreResId = resId;
        if (mImgAddData != null)
        {
            mImgAddData.setImageResource(resId);
        }
    }

    /**
     * 设置删除图片的资源id
     */
    public void setIcDeleteResId(int resId)
    {
        this.mIcDelete = resId;
    }

    /**
     * 获取最大允许显示数量与当前显示数量的差值
     */
    public int getDiffValue()
    {
        return mMaxNum - mDataList.size();
    }

    /**
     * 设置子控件点击监听
     */
    public void setOnItemClickListener(onItemClickListener l)
    {
        this.mListener = l;
    }

    /**
     * 最社最大显示数量
     */
    public void setMaxNum(int maxNum)
    {
        this.mMaxNum = maxNum;
    }

    /**
     * 设置间距尺寸，单位dp
     */
    public void setSpaceSize(int dpValue)
    {
        this.mSpaceSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue
                , getContext().getResources().getDisplayMetrics());
    }

    /**
     * 【已废弃】
     * 设置只有单张图片时的显示尺寸，单位dp
     * use {@link #setSingleImageWidth(int dpValue)}
     */
    @Deprecated
    public void setSingleImageSize(int dpValue)
    {
        setSingleImageWidth(dpValue);
    }

    /**
     * 设置只有单张图片时的显示宽度，单位dp
     */
    public void setSingleImageWidth(int dpValue)
    {
        this.mSingleImageWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue
                , getContext().getResources().getDisplayMetrics());
    }

    /**
     * 设置只有单张图片时的显示宽高比，单位dp
     */
    public void setSingleImageRatio(float ratio)
    {
        this.mSingleImageRatio = ratio;
    }

    /**
     * 子控件点击监听
     */
    public interface onItemClickListener
    {

        /**
         * Callback when click plus button be clicked
         *
         * @param dValue the diff value between current data number displayed and maximum number
         */
        void onNineGirdAddMoreClick(int dValue);

        /**
         * Callback when image be clicked
         *
         * @param position       position,started with 0
         * @param gridBean       data of image be clicked
         * @param imageContainer image container of image be clicked
         */
        void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer);

        /**
         * Callback when one image be deleted
         *
         * @param position       position,started with 0
         * @param gridBean       data of image be clicked
         * @param imageContainer image container of image be clicked
         */
        void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer);
    }

    /**
     * 移除“+”号
     */
    private void removeAddMoreView()
    {
        if (mImgAddData != null)
        {
            removeView(mImgAddData);
        }
        mImgAddData = null;
    }

    /**
     * 添加“+”号
     */
    private void addInAddMoreView()
    {
        mImgAddData = new NineGridImageView(getContext());
        mImgAddData.setImageResource(mIcAddMoreResId);
        int paddingSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10
                , getContext().getResources().getDisplayMetrics());
        mImgAddData.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
        mImgAddData.setScaleType(ImageView.ScaleType.FIT_XY);
        mImgAddData.setOnClickListener(view -> {
            if (mListener != null)
            {
                mListener.onNineGirdAddMoreClick(getDiffValue());
            }
        });
        addView(mImgAddData);
    }

    /**
     * 计算行数和列数
     */
    private void calRawAndColumn()
    {
        int childCount = getChildCount();

        //calculate the raw count
        if (childCount == 0)
        {
            mRawCount = 0;
        } else if (childCount <= mColumnCount)
        {
            mRawCount = 1;
        } else
        {
            mRawCount = childCount % mColumnCount == 0 ? childCount / mColumnCount : childCount / mColumnCount + 1;
        }
    }

    /**
     * 根据数据集合添加子控件
     *
     * @param dataList 待显示的数据集合
     */
    private void addChildViews(List<NineGridBean> dataList)
    {
        if (canShowAddMore())
        {
            removeAddMoreView();
        }

        if (dataList != null)
        {
            for (int i = 0, dataSize = dataList.size(); i < dataSize; i++)
            {
                final NineGridBean gridBean = dataList.get(i);
                final NineGirdImageContainer imageContainer = new NineGirdImageContainer(getContext());
                imageContainer.setIsDeleteMode(mIsEditMode);
                imageContainer.setRatioOfDeleteIcon(mRatioOfDelete);
                imageContainer.setDeleteIcon(mIcDelete);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    String transitionName = TextUtils.isEmpty(gridBean.getTransitionName()) ?
                            gridBean.getOriginUrl() : gridBean.getTransitionName();
                    imageContainer.getImageView().setTransitionName(transitionName);
                }

                imageContainer.setOnClickDeleteListener(() -> {
                    int position = mDataList.indexOf(gridBean);
                    mDataList.remove(position);
                    removeViewAt(position);
                    calRawAndColumn();
                    requestLayout();
                    if (mListener != null)
                    {
                        mListener.onNineGirdItemDeleted(position, gridBean, imageContainer);
                    }
                });
                imageContainer.getImageView().setOnClickListener(view -> {
                    if (mListener != null)
                    {
                        mListener.onNineGirdItemClick(mDataList.indexOf(gridBean), gridBean, imageContainer);
                    }
                });
                addView(imageContainer);

                imageContainer.post(() -> {
                    if (mImageLoader != null)
                    {
                        String url = TextUtils.isEmpty(gridBean.getThumbUrl()) ? gridBean.getOriginUrl() : gridBean.getThumbUrl();
                        if (imageContainer.getImageWidth() != 0 && imageContainer.getImageWidth() != 0)
                        {
                            mImageLoader.displayNineGridImage(getContext(), url, imageContainer.getImageView()
                                    , imageContainer.getImageWidth(), imageContainer.getImageHeight());
                        } else
                        {
                            mImageLoader.displayNineGridImage(getContext(), url, imageContainer.getImageView());
                        }

                    } else
                    {
                        Log.w("NineGridView", "Can not display the image of NineGridView, you'd better set a imageloader!!!!");
                    }
                });
            }
        }

        setIsEditMode(mIsEditMode);
    }

    /**
     * 判断当前是否能显示“+”号
     */
    private boolean canShowAddMore()
    {
        return mIsEditMode && mDataList.size() < mMaxNum;
    }

    /**
     * 清除所有子控件
     */
    private void clearAllViews()
    {
        removeAllViews();
        mImgAddData = null;
    }

    /*****************************************************
     * State cache
     ****************************************************************/

    @Override
    protected Parcelable onSaveInstanceState()
    {
        SavedViewState ss = new SavedViewState(super.onSaveInstanceState());
        ss.singleImageSize = mSingleImageWidth;
        ss.singleImageRatio = mSingleImageRatio;
        ss.spaceSize = mSpaceSize;
        ss.columnCount = mColumnCount;
        ss.rawCount = mRawCount;
        ss.maxNum = mMaxNum;
        ss.isEditMode = mIsEditMode;
        ss.icAddMoreResId = mIcAddMoreResId;
        ss.icDeleteResId = mIcDelete;
        ss.ratioDelete = mRatioOfDelete;
        ss.dataList = mDataList;
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
        mSingleImageWidth = ss.singleImageSize;
        mSingleImageRatio = ss.singleImageRatio;
        mSpaceSize = ss.spaceSize;
        mColumnCount = ss.columnCount;
        mRawCount = ss.rawCount;
        mMaxNum = ss.maxNum;
        mIsEditMode = ss.isEditMode;
        mIcAddMoreResId = ss.icAddMoreResId;
        mIcDelete = ss.icDeleteResId;
        mRatioOfDelete = ss.ratioDelete;
        setDataList(ss.dataList);
    }

    static class SavedViewState extends BaseSavedState
    {
        int singleImageSize;
        float singleImageRatio;
        int spaceSize;
        int columnCount;
        int rawCount;
        int maxNum;
        boolean isEditMode;
        int icAddMoreResId;
        List<NineGridBean> dataList;
        int icDeleteResId;
        float ratioDelete;

        SavedViewState(Parcelable superState)
        {
            super(superState);
        }

        private SavedViewState(Parcel source)
        {
            super(source);
            singleImageSize = source.readInt();
            singleImageRatio = source.readFloat();
            spaceSize = source.readInt();
            columnCount = source.readInt();
            rawCount = source.readInt();
            maxNum = source.readInt();
            isEditMode = source.readByte() == (byte) 1;
            icAddMoreResId = source.readInt();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                dataList = source.readParcelableList(dataList, NineGridView.class.getClassLoader());
            } else
            {
                dataList = source.readArrayList(NineGridBean.class.getClassLoader());
            }
            icDeleteResId = source.readInt();
            ratioDelete = source.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags)
        {
            super.writeToParcel(out, flags);
            out.writeInt(singleImageSize);
            out.writeFloat(singleImageRatio);
            out.writeInt(spaceSize);
            out.writeInt(columnCount);
            out.writeInt(rawCount);
            out.writeInt(maxNum);
            out.writeByte(isEditMode ? (byte) 1 : (byte) 0);
            out.writeInt(icAddMoreResId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                out.writeParcelableList(dataList, 0);
            } else
            {
                out.writeList(dataList);
            }
            out.writeInt(icDeleteResId);
            out.writeFloat(ratioDelete);
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
