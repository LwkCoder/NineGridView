package com.lwk.imagepicker.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.lwk.imagepicker.R;
import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.bean.ImageFloderBean;
import com.lwk.imagepicker.utils.ImageComparator;
import com.lwk.imagepicker.utils.OtherUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Function:本地图片扫描相关类
 */
public class ImageScanModel
{
    private ImageScanModel()
    {
    }

    private static final class ImageScanModelHolder
    {
        private static ImageScanModel instance = new ImageScanModel();
    }

    public static ImageScanModel getInstance()
    {
        return ImageScanModelHolder.instance;
    }

    //所有图片
    private ArrayList<ImageBean> mAllImgList = new ArrayList<>();
    //所有文件夹
    private HashMap<String, ImageFloderBean> mAllFloderMap = new HashMap<>();
    //所有文件夹List
    private List<ImageFloderBean> mAllFloderList = new ArrayList<>();
    // 缩略图列表
    private HashMap<String, String> mThumbnailList = new HashMap<>();

    public ArrayList<ImageBean> getAllImages()
    {
        return mAllImgList;
    }

    public HashMap<String, ImageFloderBean> getAllFloderMap()
    {
        return mAllFloderMap;
    }

    public List<ImageFloderBean> getAllFloderList()
    {
        if (mAllFloderList.size() == 0)
        {
            for (ImageFloderBean floderBean : mAllFloderMap.values())
            {
                if (OtherUtils.isEquals(ImageFloderBean.ALL_FLODER_ID, floderBean.getFloderId()))
                    mAllFloderList.add(0, floderBean);
                else
                    mAllFloderList.add(floderBean);
            }
        }
        return mAllFloderList;
    }

    /**
     * 根据id获取对应的文件夹
     */
    public ImageFloderBean getFloderById(String id)
    {
        for (String floderId : mAllFloderMap.keySet())
        {
            if (OtherUtils.isEquals(id, floderId))
                return mAllFloderMap.get(floderId);
        }
        return null;
    }

    /**
     * 获取某文件夹下所有图片
     *
     * @param floder 文件夹对象
     */
    public List<ImageBean> getImagesByFloder(ImageFloderBean floder)
    {
        String floderId = floder.getFloderId();
        //如果是获取所有图片直接返回
        if (OtherUtils.isEquals(ImageFloderBean.ALL_FLODER_ID, floderId))
        {
            return mAllImgList;
        }

        List<ImageBean> result = new ArrayList<>();
        for (ImageBean bean : mAllImgList)
        {
            if (OtherUtils.isEquals(bean.getFloderId(), floderId))
                result.add(bean);
        }
        return result;
    }

    /**
     * 扫描本地所有图片数据
     *
     * @param context 上下文
     * @return 是否扫描成功
     */
    public boolean scanAllData(Context context)
    {
        boolean success = false;
        try
        {
            mAllImgList.clear();
            mAllFloderMap.clear();
            mThumbnailList.clear();

            ContentResolver cr = context.getContentResolver();
            // 构造缩略图索引
            getThumbnail(cr);

            HashSet<String> allFloderNameSet = new HashSet<>();

            // 构造索引
            String columns[] =
                    new String[]{MediaStore.Images.Media._ID,
                            MediaStore.Images.Media.BUCKET_ID,
                            MediaStore.Images.Media.PICASA_ID,
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.WIDTH,
                            MediaStore.Images.Media.HEIGHT,
                            MediaStore.Images.Media.DISPLAY_NAME,
                            MediaStore.Images.Media.TITLE,
                            MediaStore.Images.Media.DATE_ADDED,
                            MediaStore.Images.Media.DATE_MODIFIED,
                            MediaStore.Images.Media.DATE_TAKEN,
                            MediaStore.Images.Media.SIZE,
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
            // 得到一个游标
            Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
            int totalNum = 0;
            if (cur != null && cur.moveToFirst())
            {
                // 获取图片总数
                totalNum = cur.getCount();
                // 获取指定列的索引
                int photoIDIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int photoModifyIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
                int photoWidthIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
                int photoHeightIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
                //                    int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
                //                    int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
                //                    int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
                int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int bucketIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);

                do
                {
                    String _id = cur.getString(photoIDIndex);//图片id
                    String path = cur.getString(photoPathIndex);//图片地址
                    //                        String name = cur.getString(photoNameIndex);//图片名字索引（全称，带后缀）
                    //                        String title = cur.getString(photoTitleIndex);//图片名字（不带后缀）
                    //                        String size = cur.getString(photoSizeIndex);//图片大小
                    String modify = cur.getString(photoModifyIndex);
                    String bucketId = cur.getString(bucketIdIndex);//文件夹id
                    String bucketName = cur.getString(bucketDisplayNameIndex);//文件夹名字
                    String width = cur.getString(photoWidthIndex);
                    String height = cur.getString(photoHeightIndex);

                    if (new File(path).exists())
                    {
                        ImageBean imageItem = new ImageBean();
                        imageItem.setImageId(_id);
                        if (width != null && width.length() > 0)
                            imageItem.setWidth(Integer.valueOf(width));
                        if (height != null && height.length() > 0)
                            imageItem.setHeight(Integer.valueOf(height));
                        imageItem.setImagePath(path);
                        imageItem.setThumbnailPath(mThumbnailList.get(_id));
                        if (modify != null && modify.length() > 0)
                            imageItem.setLastModified(Long.valueOf(modify));
                        imageItem.setFloderId(bucketId);
                        mAllImgList.add(imageItem);

                        //检查图片文件夹是否存在，存在将数量+1，不存在就创建
                        ImageFloderBean floder = null;
                        if (!allFloderNameSet.contains(bucketId))
                        {
                            floder = new ImageFloderBean();
                            floder.setFloderId(bucketId);
                            floder.setFloderName(bucketName);
                            floder.setFirstImgPath(path);
                            floder.setNum(1);
                            allFloderNameSet.add(bucketId);
                            mAllFloderMap.put(bucketId, floder);
                        } else
                        {
                            floder = mAllFloderMap.get(bucketId);
                            floder.setFirstImgPath(path);
                            floder.gainNum();
                        }
                    }
                } while (cur.moveToNext());
                cur.close();
            }

            //扫描完成后回调
            //排序，越新的照片下标要越小
            Collections.sort(mAllImgList, new ImageComparator());
            //添加"全部图片"文件夹
            ImageFloderBean firstFloder = new ImageFloderBean();
            firstFloder.setFloderId(ImageFloderBean.ALL_FLODER_ID);
            firstFloder.setFloderName(context.getResources().getString(R.string.label_imagepicker_allfloder));
            firstFloder.setFirstImgPath(mAllImgList.size() != 0 ? mAllImgList.get(0).getImagePath() : null);
            firstFloder.setNum(totalNum);
            mAllFloderMap.put(ImageFloderBean.ALL_FLODER_ID, firstFloder);
            success = true;
        } catch (Exception e)
        {
            Log.e("ImagePicker", "ImagePicker.ImageScanModel--->scanAllData() fail:" + e.toString());
            success = false;
        }

        return success;
    }

    /**
     * 得到缩略图
     */
    private void getThumbnail(ContentResolver cr)
    {
        String[] projection = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA};
        Cursor cursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
        getThumbnailColumnData(cursor);
        if (cursor != null)
            cursor.close();
    }

    /**
     * 从数据库中得到缩略图
     *
     * @param cur
     */
    private void getThumbnailColumnData(Cursor cur)
    {
        if (cur != null && cur.moveToFirst())
        {
            int _id;
            int image_id;
            String image_path;
            int _idColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails._ID);
            int image_idColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails.DATA);

            do
            {
                _id = cur.getInt(_idColumn);
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);

                mThumbnailList.put(String.valueOf(image_id), image_path);
            } while (cur.moveToNext());
        }
    }

    /**
     * 清除缓存
     */
    public void clearData()
    {
        mAllFloderList.clear();
        mThumbnailList.clear();
        mAllFloderMap.clear();
        mAllImgList.clear();
    }
}
