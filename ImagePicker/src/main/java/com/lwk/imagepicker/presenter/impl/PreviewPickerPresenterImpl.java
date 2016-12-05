package com.lwk.imagepicker.presenter.impl;

import com.lwk.imagepicker.bean.ImageBean;

/**
 * Function:已选图片界面Presenter接口
 */
public interface PreviewPickerPresenterImpl
{
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
