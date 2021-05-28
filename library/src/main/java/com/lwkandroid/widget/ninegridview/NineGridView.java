package com.lwkandroid.widget.ninegridview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.core.content.res.ResourcesCompat;

/**
 * @description:图片九宫格排列控件
 * @author: LWK
 * @date: 2021/5/26 15:37
 */
public class NineGridView extends ViewGroup implements AbsNgvAdapter.OnDataChangedListener
{
    private AbsNgvAdapter mAdapter;
    private NgvAttrOptions mAttrOptions;
    private View mPlusImageView;

    public static final ImageView.ScaleType[] SCALE_TYPE_ARRAY = {
            ImageView.ScaleType.MATRIX,
            ImageView.ScaleType.FIT_XY,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE
    };

    public static int getImageScaleTypeIndex(ImageView.ScaleType scaleType)
    {
        if (ImageView.ScaleType.MATRIX == scaleType)
            return 0;
        else if (ImageView.ScaleType.FIT_XY == scaleType)
            return 1;
        else if (ImageView.ScaleType.FIT_START == scaleType)
            return 2;
        else if (ImageView.ScaleType.FIT_CENTER == scaleType)
            return 3;
        else if (ImageView.ScaleType.FIT_END == scaleType)
            return 4;
        else if (ImageView.ScaleType.CENTER == scaleType)
            return 5;
        else if (ImageView.ScaleType.CENTER_CROP == scaleType)
            return 6;
        else if (ImageView.ScaleType.CENTER_INSIDE == scaleType)
            return 7;
        else
            return -1;
    }

    public NineGridView(Context context)
    {
        this(context, null);
    }

    public NineGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NineGridView);
        mAttrOptions = new NgvAttrOptions();
        mAttrOptions.setDividerSize(ta.getDimensionPixelOffset(R.styleable.NineGridView_divider_line_size,
                context.getResources().getDimensionPixelOffset(R.dimen.ngv_divider_line_size_default)));
        mAttrOptions.setSingleImageWidth(ta.getDimensionPixelOffset(R.styleable.NineGridView_single_image_width, 0));
        mAttrOptions.setSingleImageHeight(ta.getDimensionPixelOffset(R.styleable.NineGridView_single_image_height, 0));
        Drawable plusDrawable = ta.getDrawable(R.styleable.NineGridView_icon_plus_drawable);
        if (plusDrawable == null)
        {
            plusDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_ngv_add_pic, context.getTheme());
        }
        mAttrOptions.setIconPlusDrawable(plusDrawable);
        Drawable deleteDrawable = ta.getDrawable(R.styleable.NineGridView_icon_delete_drawable);
        if (deleteDrawable == null)
        {
            deleteDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_ngv_delete, context.getTheme());
        }
        mAttrOptions.setIconDeleteDrawable(deleteDrawable);
        float ratio = ta.getFloat(R.styleable.NineGridView_icon_delete_size_ratio, 0.2f);
        mAttrOptions.setIconDeleteSizeRatio((float) Math.min(1.0, Math.max(0.0, ratio)));
        mAttrOptions.setEnableEditMode(ta.getBoolean(R.styleable.NineGridView_enable_edit_mode, false));
        mAttrOptions.setHorizontalChildCount(ta.getInt(R.styleable.NineGridView_horizontal_child_count, 3));
        mAttrOptions.setImageScaleType(SCALE_TYPE_ARRAY[ta.getInt(R.styleable.NineGridView_android_scaleType, 6)]);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //水平方向有多少列
        int horizontalChildCount = mAttrOptions.getHorizontalChildCount();
        //计算竖直方向有多少行
        int verticalChildCount = 0;
        int childCount = getChildCount();
        if (childCount == 0)
        {
            verticalChildCount = 0;
        } else if (childCount <= horizontalChildCount)
        {
            verticalChildCount = 1;
        } else
        {
            verticalChildCount = (int) Math.ceil((float) childCount / (float) horizontalChildCount);
        }
        //子控件间距
        int dividerSize = mAttrOptions.getDividerSize();
        //最终整个控件的所需宽高
        int requiredWidth, requiredHeight;
        //图片尺寸
        int mImageWidth, mImageHeight;
        //获取当前可用最大宽高
        int totalAvailableWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int totalAvailableHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        //多张图片时，每个子控件的建议尺寸
        int suggestImageSize = (totalAvailableWidth - (horizontalChildCount - 1) * dividerSize) / horizontalChildCount;

        if (mAttrOptions.isEnableEditMode())
        {
            //编辑模式下，每个子控件的尺寸=建议尺寸
            mImageWidth = mImageHeight = suggestImageSize;
            if (childCount < horizontalChildCount)
            {
                requiredWidth = mImageWidth * childCount + (childCount - 1) * dividerSize + getPaddingLeft() + getPaddingRight();
            } else
            {
                requiredWidth = mImageWidth * horizontalChildCount + (horizontalChildCount - 1) * dividerSize + getPaddingLeft() + getPaddingRight();
            }
            requiredHeight = mImageHeight * verticalChildCount + (verticalChildCount - 1) * dividerSize + getPaddingTop() + getPaddingBottom();
        } else
        {
            //非编辑模式下，每个控件的尺寸需要根据实际情况决定
            if (mAdapter == null || mAdapter.getDataList().size() == 0)
            {
                //没有数据的时候，每个子控件尺寸=0
                mImageWidth = mImageHeight = 0;
                requiredWidth = getPaddingLeft() + getPaddingRight();
                requiredHeight = getPaddingTop() + getPaddingBottom();
            } else if (mAdapter.getDataList().size() == 1)
            {
                //只有一张图片时
                //没有额外设置单张图片尺寸时使用建议尺寸，否则需要取最小值
                if (mAttrOptions.getSingleImageWidth() <= 0 || mAttrOptions.getSingleImageHeight() <= 0)
                {
                    mImageWidth = mImageHeight = suggestImageSize;
                } else
                {
                    mImageWidth = Math.min(mAttrOptions.getSingleImageWidth(), totalAvailableWidth);
                    mImageHeight = Math.min(mAttrOptions.getSingleImageHeight(), totalAvailableHeight);
                }
                requiredWidth = mImageWidth + getPaddingLeft() + getPaddingRight();
                requiredHeight = mImageHeight + getPaddingTop() + getPaddingBottom();
            } else
            {
                //多张图片时，每个子控件的尺寸=建议尺寸
                mImageWidth = mImageHeight = suggestImageSize;
                if (childCount < horizontalChildCount)
                {
                    requiredWidth = mImageWidth * childCount + (childCount - 1) * dividerSize + getPaddingLeft() + getPaddingRight();
                } else
                {
                    requiredWidth = mImageWidth * horizontalChildCount + (horizontalChildCount - 1) * dividerSize + getPaddingLeft() + getPaddingRight();
                }
                requiredHeight = mImageHeight * verticalChildCount + (verticalChildCount - 1) * dividerSize + getPaddingTop() + getPaddingBottom();
            }
        }

        //设置最终该控件宽高
        setMeasuredDimension(requiredWidth, requiredHeight);

        //设置每个子控件宽高
        for (int index = 0; index < childCount; index++)
        {
            View childView = getChildAt(index);
            int childWidthSpec = MeasureSpec.makeMeasureSpec(mImageWidth, MeasureSpec.EXACTLY);
            int childHeightSpec = MeasureSpec.makeMeasureSpec(mImageHeight, MeasureSpec.EXACTLY);
            childView.measure(childWidthSpec, childHeightSpec);
        }
        //给加号图片增加间距
        if (mPlusImageView != null)
        {
            int paddingSize = (int) (Math.min(mImageWidth, mImageHeight) * mAttrOptions.getIconDeleteSizeRatio()) / 2;
            mPlusImageView.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++)
        {
            View childView = getChildAt(index);
            int horizontalIndex = index / mAttrOptions.getHorizontalChildCount();
            int verticalIndex = index % mAttrOptions.getHorizontalChildCount();
            int left = (childView.getMeasuredWidth() + mAttrOptions.getDividerSize()) * verticalIndex + getPaddingLeft();
            int top = (childView.getMeasuredHeight() + mAttrOptions.getDividerSize()) * horizontalIndex + getPaddingTop();
            int right = left + childView.getMeasuredWidth();
            int bottom = top + childView.getMeasuredHeight();
            childView.layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        autoAdjustPlusImageView();
    }

    @Override
    public void onAllDataChanged(List dataList, boolean reachLimitedSize)
    {
        removePlusImageView();
        removeAllViews();

        int count = mAdapter.getDataList().size();
        for (int i = 0; i < count; i++)
        {
            addView(buildChildView(i));
        }
        autoAdjustPlusImageView();
    }

    @Override
    public void onDataChanged(Object data, int position, boolean reachLimitedSize)
    {
        mAdapter.bindContentView(getChildAt(position), data, position, mAttrOptions);
    }

    @Override
    public void onDataAdded(Object data, int position, boolean reachLimitedSize)
    {
        addView(buildChildView(position), position);
        autoAdjustPlusImageView();
    }

    @Override
    public void onDataListAdded(List dataList, int startPosition, boolean reachLimitedSize)
    {
        for (int i = 0; i < dataList.size(); i++)
        {
            onDataAdded(dataList.get(i), startPosition + i, reachLimitedSize);
        }
        autoAdjustPlusImageView();
    }

    @Override
    public void onDataRemoved(Object data, int position, boolean reachLimitedSize)
    {
        removeViewAt(position);
        autoAdjustPlusImageView();
    }

    public AbsNgvAdapter getAdapter()
    {
        return mAdapter;
    }

    public void setAdapter(AbsNgvAdapter adapter)
    {
        this.mAdapter = adapter;
        if (mAdapter != null)
        {
            onAllDataChanged(mAdapter.getDataList(), mAdapter.isDataToLimited());
            mAdapter.addDataChangedListener(this);
        }
    }

    public boolean isEditModeEnabled()
    {
        return mAttrOptions.isEnableEditMode();
    }

    public void setEnableEditMode(boolean enable)
    {
        mAttrOptions.setEnableEditMode(enable);

        if (mAdapter != null)
        {
            List dataList = mAdapter.getDataList();
            for (int position = 0; position < dataList.size(); position++)
            {
                mAdapter.bindContentView(getChildAt(position), dataList.get(position), position, mAttrOptions);
            }
        }
        autoAdjustPlusImageView();
        requestLayout();
    }

    public void setDividerLineSize(int unit, int size)
    {
        mAttrOptions.setDividerSize((int) TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics()));
        requestLayout();
    }

    public void setSingleImageSize(int unit, int width, int height)
    {
        mAttrOptions.setSingleImageWidth((int) TypedValue.applyDimension(unit, width, getResources().getDisplayMetrics()));
        mAttrOptions.setSingleImageHeight((int) TypedValue.applyDimension(unit, height, getResources().getDisplayMetrics()));
        requestLayout();
    }

    public void setIconPlusDrawable(@DrawableRes int resId)
    {
        setIconPlusDrawable(ResourcesCompat.getDrawable(getResources(), resId, getContext().getTheme()));
    }

    public void setIconPlusDrawable(Drawable drawable)
    {
        mAttrOptions.setIconPlusDrawable(drawable);
        requestLayout();
    }

    public void setIconDeleteDrawable(@DrawableRes int resId)
    {
        setIconDeleteDrawable(ResourcesCompat.getDrawable(getResources(), resId, getContext().getTheme()));
    }

    public void setIconDeleteDrawable(Drawable drawable)
    {
        mAttrOptions.setIconDeleteDrawable(drawable);
        requestLayout();
    }

    public void setIconDeleteSizeRatio(@FloatRange(from = 0.0, to = 1.0) float ratio)
    {
        mAttrOptions.setIconDeleteSizeRatio(ratio);
        requestLayout();
    }

    public void setHorizontalChildCount(int count)
    {
        mAttrOptions.setHorizontalChildCount(count);
        requestLayout();
    }

    public void setImageScaleType(ImageView.ScaleType scaleType)
    {
        mAttrOptions.setImageScaleType(scaleType);
        requestLayout();
    }

    /*************************************************私有方法*******************************************************************************/
    /**
     * 判断当前是否能显示“+”号
     */
    private boolean canShowPlusImageView()
    {
        return mAttrOptions.isEnableEditMode() && (mAdapter == null || !mAdapter.isDataToLimited());
    }

    private View buildChildView(int position)
    {
        View childViewGroup = mAdapter.createContentView(getContext());
        mAdapter.bindContentView(childViewGroup, mAdapter.getDataList().get(position), position, mAttrOptions);
        return childViewGroup;
    }

    private void autoAdjustPlusImageView()
    {
        if (canShowPlusImageView())
        {
            showPlusImageView();
        } else
        {
            removePlusImageView();
        }
    }

    private void showPlusImageView()
    {
        if (mPlusImageView != null || mAdapter == null)
            return;

        mPlusImageView = mAdapter.createPlusView(getContext());
        mAdapter.bindPlusView(mPlusImageView, mAttrOptions);

        addView(mPlusImageView);
    }

    private void removePlusImageView()
    {
        if (mPlusImageView != null)
            removeView(mPlusImageView);
        mPlusImageView = null;
    }

    /*************************************************************状态恢复******************************************************************/

    @Override
    protected Parcelable onSaveInstanceState()
    {
        SavedViewState ss = new SavedViewState(super.onSaveInstanceState());
        ss.mDividerSize = mAttrOptions.getDividerSize();
        ss.mSingleImageWidth = mAttrOptions.getSingleImageWidth();
        ss.mSingleImageHeight = mAttrOptions.getSingleImageHeight();
        ss.mIconPlusDrawable = Utils.drawable2Bytes(mAttrOptions.getIconPlusDrawable());
        ss.mIconDeleteDrawable = Utils.drawable2Bytes(mAttrOptions.getIconDeleteDrawable());
        ss.mIconDeleteSizeRatio = mAttrOptions.getIconDeleteSizeRatio();
        ss.mEnableEditMode = mAttrOptions.isEnableEditMode();
        ss.mHorizontalChildCount = mAttrOptions.getHorizontalChildCount();
        ss.mImageScaleType = getImageScaleTypeIndex(mAttrOptions.getImageScaleType());
        ss.mDataList = mAdapter.getDataList();
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
        if (mAttrOptions != null)
        {
            mAttrOptions.setDividerSize(ss.mDividerSize);
            mAttrOptions.setSingleImageWidth(ss.mSingleImageWidth);
            mAttrOptions.setSingleImageHeight(ss.mSingleImageHeight);
            mAttrOptions.setIconPlusDrawable(Utils.bytes2Drawable(getContext(), ss.mIconPlusDrawable));
            mAttrOptions.setIconDeleteDrawable(Utils.bytes2Drawable(getContext(), ss.mIconDeleteDrawable));
            mAttrOptions.setIconDeleteSizeRatio(ss.mIconDeleteSizeRatio);
            mAttrOptions.setEnableEditMode(ss.mEnableEditMode);
            mAttrOptions.setHorizontalChildCount(ss.mHorizontalChildCount);
            mAttrOptions.setImageScaleType(SCALE_TYPE_ARRAY[ss.mImageScaleType]);
        }
        if (mAdapter != null)
            mAdapter.setDataList(ss.mDataList);
        requestLayout();
    }

    private static class SavedViewState extends BaseSavedState
    {
        private int mDividerSize;
        private int mSingleImageWidth;
        private int mSingleImageHeight;
        private byte[] mIconPlusDrawable;
        private byte[] mIconDeleteDrawable;
        private float mIconDeleteSizeRatio;
        private boolean mEnableEditMode;
        private int mHorizontalChildCount;
        private int mImageScaleType;
        private List mDataList;

        public SavedViewState(Parcelable superState)
        {
            super(superState);
        }

        public SavedViewState(Parcel source)
        {
            super(source);
            mDividerSize = source.readInt();
            mSingleImageWidth = source.readInt();
            mSingleImageHeight = source.readInt();
            source.readByteArray(mIconPlusDrawable);
            source.readByteArray(mIconDeleteDrawable);
            mIconDeleteSizeRatio = source.readFloat();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                mEnableEditMode = source.readBoolean();
            } else
            {
                mEnableEditMode = source.readByte() == (byte) 1;
            }
            mHorizontalChildCount = source.readInt();
            mImageScaleType = source.readInt();
            source.readList(mDataList, Object.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags)
        {
            super.writeToParcel(out, flags);
            out.writeInt(mDividerSize);
            out.writeInt(mSingleImageWidth);
            out.writeInt(mSingleImageHeight);
            out.writeByteArray(mIconPlusDrawable);
            out.writeByteArray(mIconDeleteDrawable);
            out.writeFloat(mIconDeleteSizeRatio);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                out.writeBoolean(mEnableEditMode);
            } else
            {
                out.writeByte(mEnableEditMode ? (byte) 1 : (byte) 0);
            }
            out.writeInt(mHorizontalChildCount);
            out.writeInt(mImageScaleType);
            out.writeList(mDataList);
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
