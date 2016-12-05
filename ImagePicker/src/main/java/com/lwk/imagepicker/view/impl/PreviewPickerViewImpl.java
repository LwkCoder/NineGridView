package com.lwk.imagepicker.view.impl;

/**
 * Function:已选图片预览界面Impl
 */
public interface PreviewPickerViewImpl
{
    /**
     * 当前的图片被选中
     */
    void onCurImageBeAdded();

    /**
     * 当前的图片被移除
     */
    void onCurImageBeRemoved();

    /**
     * 刷新当前图片选中状态
     */
    void refreshCurImgStatus();

    /**
     * 刷新选中数量的按钮
     */
    void onSelectedNumChanged(int curNum, int maxNum);
}
