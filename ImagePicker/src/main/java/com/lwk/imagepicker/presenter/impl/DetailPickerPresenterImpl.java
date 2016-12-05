package com.lwk.imagepicker.presenter.impl;

import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.bean.ImageFloderBean;

import java.util.List;

/**
 * Function:图片详情界面Presenter接口
 */
public interface DetailPickerPresenterImpl
{
    /**
     * 获取某文件夹下所有图片数据
     */
    List<ImageBean> getImagesByFloder(ImageFloderBean floder);

    /**
     * 根据文件夹id获取该文件夹下所有图片数据
     */
    ImageFloderBean getFloderById(String id);

    /**
     * 添加新图片
     */
    void addImage(ImageBean imageBean);

    /**
     * 移除某图片
     */
    void removeImage(ImageBean imageBean);

    /**
     * 判断某图片是否已经添加
     */
    boolean hasSelectedData(ImageBean imageBean);

    /**
     * 刷新已选数据
     */
    void selectNumChanged();
}
