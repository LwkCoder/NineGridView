package com.lwk.imagepicker.view.impl;

import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.bean.ImageFloderBean;

/**
 * Function:图片选择界面Impl
 */
public interface GridPickerViewImpl
{
    /**
     * 开始扫描本地所有图片数据
     */
    void startScanData();

    /**
     * 扫描本地图片成功
     */
    void scanDataSuccess();

    /**
     * 扫描本地图片失败
     */
    void scanDataFail();

    /**
     * 点击拍照
     */
    void clickTakePhoto();

    /**
     * 选中的照片数量发生改变的回调
     */
    void onSelectedNumChanged(int curNum, int maxNum);

    /**
     * 所选数量达到上限
     */
    void onNumLimited(int maxNum);

    /**
     * 单选照片选定后
     */
    void onSingleImageSelected(ImageBean imageBean);

    /**
     * 当前查看的文件夹发生变化
     */
    void onCurFloderChanged(ImageFloderBean curFloder);

    /**
     * 进入图片详情界面
     *
     * @param startPosition 起始位置
     */
    void enterDetailActivity(int startPosition);
}
