package com.lwk.imagepicker.view.impl;

/**
 * Function:图片详情界面impl
 */
public interface DetailPickerViewImpl
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
     * 所选数量达到上限
     */
    void onNumLimited(int maxNum);

    /**
     * 刷新选中数量的按钮
     */
    void onSelectedNumChanged(int curNum, int maxNum);
}
