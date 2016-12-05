package com.lwk.imagepicker.view.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lwk.imagepicker.ImagePicker;
import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.utils.OtherUtils;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * ViewPager适配器
 */
public class ImagePickerPagerAdapter extends PagerAdapter
{
    private int mScreenWidth;
    private int mScreenHeight;
    private ImagePicker mImagePicker;
    private ArrayList<ImageBean> mAllmageList = new ArrayList<>();
    private Activity mActivity;
    public PhotoViewClickListener mListener;

    public ImagePickerPagerAdapter(Activity activity, ArrayList<ImageBean> images)
    {
        this.mActivity = activity;
        this.mAllmageList.addAll(images);

        mScreenWidth = OtherUtils.getScreenWidth(activity);
        mScreenHeight = OtherUtils.getScreenHeight(activity);
        mImagePicker = ImagePicker.getInstance();
    }

    public void setData(ArrayList<ImageBean> images)
    {
        mAllmageList.clear();
        this.mAllmageList.addAll(images);
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener)
    {
        this.mListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        PhotoView photoView = new PhotoView(mActivity);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        photoView.setEnabled(true);
        ImageBean imageItem = mAllmageList.get(position);
        mImagePicker.getOptions().getDisplayer().display(mActivity, photoView, imageItem.getImagePath(), mScreenWidth, mScreenHeight);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener()
        {
            @Override
            public void onPhotoTap(View view, float x, float y)
            {
                if (mListener != null)
                    mListener.OnPhotoTapListener(view, x, y);
            }
        });
        container.addView(photoView);
        return photoView;
    }

    @Override
    public int getCount()
    {
        return mAllmageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    public interface PhotoViewClickListener
    {
        void OnPhotoTapListener(View view, float v, float v1);
    }
}
