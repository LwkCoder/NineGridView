package com.lwk.imagepicker.utils;

import com.lwk.imagepicker.bean.ImageBean;

import java.util.Comparator;

/**
 * Function:对本地图片先后顺序排列的比较器
 */
public class ImageComparator implements Comparator<ImageBean>
{
    @Override
    public int compare(ImageBean first, ImageBean second)
    {
        long fModify = first.getLastModified();
        long sModify = second.getLastModified();
        if (fModify > sModify)
            return -1;
        else if (fModify < sModify)
            return 1;
        else
            return 0;
    }
}
