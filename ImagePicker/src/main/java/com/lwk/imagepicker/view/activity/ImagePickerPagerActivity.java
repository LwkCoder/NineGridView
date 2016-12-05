package com.lwk.imagepicker.view.activity;

import android.os.Build;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.lwk.imagepicker.R;
import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.utils.OtherUtils;
import com.lwk.imagepicker.view.adapter.ImagePickerPagerAdapter;
import com.lwk.imagepicker.view.base.ImagePickerBaseActivity;
import com.lwk.imagepicker.view.widget.ImagePickerActionBar;
import com.lwk.imagepicker.view.widget.ViewPagerFixed;

import java.util.ArrayList;

/**
 * Function:ViewPager形式的界面基类
 */
public abstract class ImagePickerPagerActivity extends ImagePickerBaseActivity
{
    protected static final String INTENT_KEY_START_POSITION = "start_pos";
    protected static final String INTENT_KEY_FLODER_ID = "floder_id";
    protected ArrayList<ImageBean> mImageItems = new ArrayList<>();      //跳转进ImagePreviewFragment的图片文件夹
    protected int mCurrentPosition = 0;              //跳转进ImagePreviewFragment时的序号，第几个图片
    protected TextView mTvIndicate;                  //显示当前图片的位置  例如  5/31
    protected ViewPagerFixed mViewPager;
    protected ImagePickerPagerAdapter mAdapter;
    protected View mViewContent;
    protected ImagePickerActionBar mActionBar;
    protected View mViewBottom;

    @Override
    protected int setContentViewId()
    {
        return R.layout.activity_image_picker_viewpager;
    }

    @Override
    protected void initUI()
    {
        OtherUtils.changeStatusBarColor(this, getResources().getColor(R.color.black_statusbar));
        mActionBar = findView(R.id.cab_imagepicker_viewpager_top);
        mActionBar.setLeftLayoutAsBack(this);
        mViewBottom = findView(R.id.view_imagepicker_viewpager_bottom);
        mViewContent = findView(R.id.rl_imagepicker_viewpager_root);
        mViewPager = findView(R.id.vp_imagepicker_viewpager_content);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mAdapter = new ImagePickerPagerAdapter(this, mImageItems);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);
        mAdapter.setPhotoViewClickListener(new ImagePickerPagerAdapter.PhotoViewClickListener()
        {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1)
            {
                onImageSingleTap();
            }
        });
    }

    /**
     * 根据单击来隐藏/显示头部和尾部的布局
     */
    public void onImageSingleTap()
    {
        if (mActionBar == null || mViewBottom == null)
            return;
        if (mActionBar.getVisibility() == View.VISIBLE)
        {
            mActionBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.imagepicker_actionbar_dismiss));
            mViewBottom.setAnimation(AnimationUtils.loadAnimation(this, R.anim.imagepicker_bottom_dismiss));
            mActionBar.setVisibility(View.GONE);
            mViewBottom.setVisibility(View.GONE);
            //更改状态栏为透明
            OtherUtils.changeStatusBarColor(this, getResources().getColor(R.color.transparent_imagepicker));
            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
            if (Build.VERSION.SDK_INT >= 16)
                mViewContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else
        {
            mActionBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.imagepicker_actionbar_show));
            mViewBottom.setAnimation(AnimationUtils.loadAnimation(this, R.anim.imagepicker_bottom_show));
            mActionBar.setVisibility(View.VISIBLE);
            mViewBottom.setVisibility(View.VISIBLE);
            //改回状态栏颜色
            OtherUtils.changeStatusBarColor(this, getResources().getColor(R.color.black_statusbar));
            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
            if (Build.VERSION.SDK_INT >= 16)
                mViewContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
