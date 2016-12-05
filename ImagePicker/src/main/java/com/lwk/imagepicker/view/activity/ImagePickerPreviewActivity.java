package com.lwk.imagepicker.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lwk.imagepicker.ImagePicker;
import com.lwk.imagepicker.R;
import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.model.ImagePickerMode;
import com.lwk.imagepicker.presenter.ImagePickerPreviewPresenter;
import com.lwk.imagepicker.view.impl.PreviewPickerViewImpl;

/**
 * 已选图片预览界面
 * 虽然大部分功能和图片详情界面一样，还是分开比较好
 */
public class ImagePickerPreviewActivity extends ImagePickerPagerActivity implements PreviewPickerViewImpl
{
    private ImagePickerPreviewPresenter mPresenter;
    private View mCkSelected;
    private ImageView mImgSelected;
    private Button mBtnOk;

    /**
     * 跳转到该界面的公共方法
     */
    public static void start(Activity activity)
    {
        Intent intent = new Intent(activity, ImagePickerPreviewActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        super.beforeOnCreate(savedInstanceState);
        mPresenter = new ImagePickerPreviewPresenter(this);
        mImageItems.addAll(ImagePicker.getInstance().getAllSelectedImages());
        mCurrentPosition = 0;
    }

    @Override
    protected void initUI()
    {
        mTvIndicate = findView(R.id.tv_imagepicker_actionbar_title);
        mCkSelected = findView(R.id.ck_imagepicker_vp_bottom);
        mImgSelected = findView(R.id.img_imagepicker_vp_bottom_choose);
        mBtnOk = findView(R.id.btn_imagepicker_vp_bottom_ok);
        addClick(mBtnOk);
        addClick(mCkSelected);
        super.initUI();
    }

    @Override
    protected void initData()
    {
        super.initData();
        //初始化当前页面indicator的状态
        int totalNum = mImageItems.size();
        if (totalNum > 0)
            mTvIndicate.setText(getString(R.string.tv_imagepicker_pager_titlecount, mCurrentPosition + 1, mImageItems.size()));
        else
            mCkSelected.setVisibility(View.GONE);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        //初始化当前选中数量状态
        mPresenter.selectNumChanged();
        //初始化当前图片状态
        refreshCurImgStatus();
    }

    private ViewPager.SimpleOnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener()
    {
        @Override
        public void onPageSelected(int position)
        {
            super.onPageSelected(position);
            mCurrentPosition = position;
            refreshCurImgStatus();
            mTvIndicate.setText(getString(R.string.tv_imagepicker_pager_titlecount, mCurrentPosition + 1, mImageItems.size()));
        }
    };

    @Override
    protected void onClick(int id, View v)
    {
        if (id == R.id.btn_imagepicker_vp_bottom_ok)
        {
            ImagePicker.getInstance().handleMutilModeListener();
        } else if (id == R.id.ck_imagepicker_vp_bottom)
        {
            ImageBean imageBean = mImageItems.get(mCurrentPosition);
            boolean hasSelected = mPresenter.hasSelectedData(imageBean);
            if (hasSelected)
                mPresenter.removeImage(imageBean);
            else
                mPresenter.addImage(imageBean);
        }
    }

    @Override
    public void onCurImageBeAdded()
    {
        mImgSelected.setImageResource(R.drawable.ck_imagepicker_detail_selected);
    }

    @Override
    public void onCurImageBeRemoved()
    {
        mImgSelected.setImageResource(R.drawable.ck_imagepicker_detail_normal);
    }

    @Override
    public void refreshCurImgStatus()
    {
        if (mImageItems != null && mCurrentPosition < mImageItems.size())
        {
            ImageBean imageBean = mImageItems.get(mCurrentPosition);
            boolean hasSelected = mPresenter.hasSelectedData(imageBean);
            if (hasSelected)
                mImgSelected.setImageResource(R.drawable.ck_imagepicker_detail_selected);
            else
                mImgSelected.setImageResource(R.drawable.ck_imagepicker_detail_normal);
        }
    }

    @Override
    public void onSelectedNumChanged(int curNum, int maxNum)
    {
        if (ImagePicker.getInstance().getOptions().getPickerMode() != ImagePickerMode.SINGLE)
        {
            mBtnOk.setText(getString(R.string.btn_imagepicker_ok, curNum, maxNum));
            if (curNum == 0)
                mBtnOk.setEnabled(false);
            else
                mBtnOk.setEnabled(true);
        }
    }
}
