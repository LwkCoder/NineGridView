package com.lwk.imagepicker;

import android.app.Activity;
import android.content.Intent;

import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.model.ImagePickerMode;
import com.lwk.imagepicker.utils.ImageActivityManager;
import com.lwk.imagepicker.view.activity.ImagePickerGridActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:帮助类，功能总入口
 */
public class ImagePicker
{
    private ImagePicker()
    {
    }

    private static final class ImagePickerHolder
    {
        public static ImagePicker instance = new ImagePicker();
    }

    public static ImagePicker getInstance()
    {
        return ImagePickerHolder.instance;
    }

    public interface OnSelectedListener
    {
        void onSelected(List<ImageBean> list);
    }

    private ImagePickerOptions mOptions;
    private OnSelectedListener mListener;
    private List<ImageBean> mSelectedData = new ArrayList<>();

    /**
     * 单选图片
     *
     * @param activity 发起跳转的界面
     * @param needCrop 是否需要裁剪
     * @param listener 监听
     */
    public void pickSingleImage(Activity activity, boolean needCrop, OnSelectedListener listener)
    {
        mOptions = new ImagePickerOptions();
        mOptions.setNeedCrop(needCrop);
        this.mListener = listener;
        activity.startActivity(new Intent(activity, ImagePickerGridActivity.class));
    }

    /**
     * 多选图片
     *
     * @param activity  发起跳转的activity
     * @param litmitNum 最大可选数量
     * @param listener  监听
     */
    public void pickMutilImage(Activity activity, int litmitNum, OnSelectedListener listener)
    {
        mOptions = new ImagePickerOptions();
        mOptions.setPickerMode(ImagePickerMode.MUTIL);
        mOptions.setLimitNum(litmitNum);
        this.mListener = listener;
        activity.startActivity(new Intent(activity, ImagePickerGridActivity.class));
    }

    /**
     * 带自定义参数选择图片
     *
     * @param activity 发起跳转的activity
     * @param options  自定义参数
     * @param listener 监听
     */
    public void pickWithOptions(Activity activity, ImagePickerOptions options, OnSelectedListener listener)
    {
        this.mOptions = options;
        this.mListener = listener;
        activity.startActivity(new Intent(activity, ImagePickerGridActivity.class));
    }

    /**
     * 获取当前参数
     */
    public ImagePickerOptions getOptions()
    {
        return mOptions;
    }

    /**
     * 多选模式下触发选择完成的回调
     */
    public void handleMutilModeListener()
    {
        List<ImageBean> resultList = new ArrayList<>();
        resultList.addAll(mSelectedData);
        if (mListener != null)
            mListener.onSelected(resultList);
        clear();
        ImageActivityManager.getInstance().finishAllActivity();
    }

    /**
     * 单选模式下触发选择完成回调
     */
    public void handleSingleModeListener(ImageBean imageBean)
    {
        List<ImageBean> resultList = new ArrayList<>();
        resultList.add(imageBean);
        if (mListener != null)
            mListener.onSelected(resultList);
        clear();
        ImageActivityManager.getInstance().finishAllActivity();
    }

    /**
     * 获取当前所有已选图片
     */
    public List<ImageBean> getAllSelectedImages()
    {
        return mSelectedData;
    }

    /**
     * 添加选中的图片
     */
    public boolean addImage(ImageBean imageBean)
    {
        //如果未添加前数量已达上限，直接返回添加失败
        if (mSelectedData.size() == mOptions.getLimitNum())
            return false;
        mSelectedData.add(imageBean);
        return true;
    }

    /**
     * 移除选中的图片
     */
    public void removeImage(ImageBean imageBean)
    {
        if (mSelectedData.contains(imageBean))
            mSelectedData.remove(imageBean);
    }

    /**
     * 清除所有选中的图片
     */
    public void removeAllSelectedImages()
    {
        mSelectedData.clear();
    }

    /**
     * 清除缓存
     */
    public void clear()
    {
        mListener = null;
        mSelectedData.clear();
        mOptions = null;
    }
}
