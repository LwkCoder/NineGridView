package com.lwk.imagepicker.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lwk.imagepicker.ImagePicker;
import com.lwk.imagepicker.R;
import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.bean.ImageFloderBean;
import com.lwk.imagepicker.model.ImagePickerMode;
import com.lwk.imagepicker.presenter.ImagePickerDetailPresenter;
import com.lwk.imagepicker.view.impl.DetailPickerViewImpl;

/**
 * 浏览大图界面
 */
public class ImagePickerDetailActivity extends ImagePickerPagerActivity implements DetailPickerViewImpl
{
    private ImagePickerDetailPresenter mPresenter;
    private View mCkSelected;
    private ImageView mImgSelected;
    private Button mBtnOk;
    //是否为第一次加载
    private boolean isFristLoading = true;

    /**
     * 跳转到该界面的公共方法
     *
     * @param activity      发起跳转的界面
     * @param startPosition 图片起始位置
     * @param folderId      所选文件夹id
     */
    public static void start(Activity activity, int startPosition, String folderId)
    {
        Intent intent = new Intent(activity, ImagePickerDetailActivity.class);
        intent.putExtra(INTENT_KEY_START_POSITION, startPosition);
        intent.putExtra(INTENT_KEY_FLODER_ID, folderId);
        activity.startActivity(intent);
    }

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        super.beforeOnCreate(savedInstanceState);
        mPresenter = new ImagePickerDetailPresenter(this);
        Intent intent = getIntent();
        mCurrentPosition = intent.getIntExtra(INTENT_KEY_START_POSITION, 0);
        String floderId = intent.getStringExtra(INTENT_KEY_FLODER_ID);
        ImageFloderBean floderBean = mPresenter.getFloderById(floderId);
        mImageItems.addAll(mPresenter.getImagesByFloder(floderBean));
    }

    @Override
    protected void initUI()
    {
        super.initUI();
        mTvIndicate = findView(R.id.tv_imagepicker_actionbar_title);
        mActionBar.setRightTvText(R.string.tv_imagepicker_actionbar_preview);
        mActionBar.setRightLayoutClickListener(this);
        mCkSelected = findView(R.id.ck_imagepicker_vp_bottom);
        mImgSelected = findView(R.id.img_imagepicker_vp_bottom_choose);
        mBtnOk = findView(R.id.btn_imagepicker_vp_bottom_ok);
        addClick(mBtnOk);
        addClick(mCkSelected);
    }

    @Override
    protected void initData()
    {
        super.initData();
        //初始化当前页面的状态
        mTvIndicate.setText(getString(R.string.tv_imagepicker_pager_titlecount, mCurrentPosition + 1, mImageItems.size()));
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        //初始化当前选中数量状态
        mPresenter.selectNumChanged();
        //初始化当前图片状态
        refreshCurImgStatus();
    }

    private ViewPager.SimpleOnPageChangeListener mPageChangeListener = new SimpleOnPageChangeListener()
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
    protected void onStart()
    {
        super.onStart();
        if (!isFristLoading)
        {
            refreshCurImgStatus();
            mPresenter.selectNumChanged();
        } else
        {
            isFristLoading = false;
        }
    }

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
        } else if (id == R.id.fl_imagepicker_actionbar_right)
        {
            //预览已选图片
            ImagePickerPreviewActivity.start(this);
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
    public void onNumLimited(int maxNum)
    {
        showToast(getString(R.string.warning_imagepicker_limit_num, maxNum));
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
