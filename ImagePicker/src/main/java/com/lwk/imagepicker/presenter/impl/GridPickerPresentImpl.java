package com.lwk.imagepicker.presenter.impl;

import android.content.Context;

import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.bean.ImageFloderBean;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Function:图片网格界面Presenter接口
 */
public interface GridPickerPresentImpl
{
    /**
     * 扫描所有图片数据
     */
    void scanAllData(Context context);

    /**
     * 获取所有图片数据
     */
    List<ImageBean> getAllImages();

    /**
     * 获取所有文件夹Map
     */
    Map<String, ImageFloderBean> getAllFloderMap();

    /**
     * 获取所有文件夹List
     */
    List<ImageFloderBean> getAllFloderList();

    /**
     * 拍照
     */
    void takePhoto();

    /**
     * 多选模式下所选图片数量发生改变
     */
    void selectedNumChanged();

    /**
     * 多选模式下所以选图片达到上限
     */
    void numLimitedWarning();

    /**
     * 获取某文件夹下所有图片数据
     */
    List<ImageBean> getImagesByFloder(ImageFloderBean floder);

    /**
     * 根据文件夹id获取该文件夹下所有图片数据
     */
    ImageFloderBean getFloderById(String id);

    /**
     * 单选图片选定
     */
    void singleImageSelected(ImageBean imageBean);

    /**
     * 获取拍照预设路径
     */
    File getTakePhotoPath();

    /**
     * 切换当前显示的文件夹
     */
    void changeFloder(ImageFloderBean floderBean);

    /**
     * 进入图片详情界面
     *
     * @param startPosition 起始位置
     */
    void enterDetailActivity(int startPosition);

    /**
     * 清除数据
     */
    void clearData();
}
