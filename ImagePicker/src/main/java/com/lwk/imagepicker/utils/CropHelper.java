package com.lwk.imagepicker.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;

import com.soundcloud.android.crop.Crop;

import java.io.File;

/**
 * Function:裁剪图片帮助类
 * 2016/5/17
 * <p/>
 * <p/>
 * 在调用该裁剪方法后的Activity/Fragment中如下重写获取裁减后的数据
 *
 * @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
 * {
 * super.onActivityResult(requestCode, resultCode, data);
 * if (requestCode == CropHelper.REQUEST_CODE_CROP && resultCode == RESULT_OK)
 * {
 * Pair<Uri, String> pair = CropHelper.getCropedData(this, data);
 * mImg.setImageURI(pair.first);
 * }
 * }
 */
public class CropHelper
{
    //跳转的requestCode
    public static final int REQUEST_CODE_CROP = 326;

    private static final String TAG = "CropHelper";

    /**
     * 跳转到裁剪矩形的界面
     *
     * @param activity  上下文
     * @param filePath  待裁剪图片绝对路径
     * @param cachePath 缓存地址
     * @param maxX      裁剪后最大分辨率X
     * @param maxY      裁剪后最大分辨率Y
     */
    public static void startCropInRect(Activity activity, String filePath
            , String cachePath, int maxX, int maxY)
    {
        Pair<Uri, Uri> uriPair = getUris(activity, filePath, cachePath);
        if (uriPair == null)
            return;
        Crop.of(uriPair.first, uriPair.second).asSquare()
                .withMaxSize(maxX, maxY).start(activity, REQUEST_CODE_CROP);
    }

    /**
     * 跳转到裁剪矩形的界面
     *
     * @param fragment  跳转的片段
     * @param filePath  待裁剪图片绝对路径
     * @param cachePath 缓存地址
     * @param maxX      裁剪后最大分辨率X
     * @param maxY      裁剪后最大分辨率Y
     */
    public static void startCropInRect(Fragment fragment, String filePath
            , String cachePath, int maxX, int maxY)
    {
        Activity activity = fragment.getActivity();
        if (activity == null)
        {
            Log.e(TAG, "CropHelper--->片段附着的Activity为空");
            return;
        }
        Pair<Uri, Uri> uriPair = getUris(activity, filePath, cachePath);
        if (uriPair == null)
            return;
        Crop.of(uriPair.first, uriPair.second)
                .asSquare().withMaxSize(maxX, maxY)
                .start(activity, fragment, REQUEST_CODE_CROP);
    }

    /**
     * 跳转到裁剪图片的界面
     *
     * @param activity  跳转的片段
     * @param filePath  待裁剪图片绝对路径
     * @param cachePath 缓存地址
     * @param aspectX   裁剪比例X
     * @param aspectY   裁剪比例Y
     * @param maxX      裁剪后最大分辨率X
     * @param maxY      裁剪后最大分辨率Y
     */
    public static void startCrop(Activity activity, String filePath, String cachePath
            , int aspectX, int aspectY, int maxX, int maxY)
    {
        Pair<Uri, Uri> uriPair = getUris(activity, filePath, cachePath);
        if (uriPair == null)
            return;
        Crop.of(uriPair.first, uriPair.second)
                .withAspect(aspectX, aspectY)
                .withMaxSize(maxX, maxY)
                .start(activity, REQUEST_CODE_CROP);
    }

    /**
     * 跳转到裁剪图片的界面
     *
     * @param fragment  上下文
     * @param filePath  待裁剪图片绝对路径
     * @param cachePath 缓存地址
     * @param aspectX   裁剪比例X
     * @param aspectY   裁剪比例Y
     * @param maxX      裁剪后最大分辨率X
     * @param maxY      裁剪后最大分辨率Y
     */
    public static void startCrop(Fragment fragment, String filePath, String cachePath
            , int aspectX, int aspectY, int maxX, int maxY)
    {
        Activity activity = fragment.getActivity();
        if (activity == null)
        {
            Log.e(TAG, "CropHelper--->片段附着的Activity为空");
            return;
        }
        Pair<Uri, Uri> uriPair = getUris(activity, filePath, cachePath);
        if (uriPair == null)
            return;
        Crop.of(uriPair.first, uriPair.second)
                .withAspect(aspectX, aspectY)
                .withMaxSize(maxX, maxY)
                .start(activity, fragment, REQUEST_CODE_CROP);
    }

    //根据待裁剪的图片地址获取其对应的Uri以及预设的裁剪后的Uri
    private static Pair<Uri, Uri> getUris(Activity activity, String filePath, String cachePath)
    {
        if (filePath == null)
        {
            Log.e(TAG, "CropHelper--->待裁剪图片绝对路径为空！！！");
            return null;
        }
        File sourceFile = new File(filePath);
        if (!sourceFile.exists())
        {
            Log.e(TAG, "CropHelper--->待裁剪图片无法访问！！！");
            return null;
        }
        if (OtherUtils.isEmpty(cachePath))
            cachePath = OtherUtils.getDefaultCachePath(activity);
        File destFile = new File(cachePath, createCropImgName());
        Log.i(TAG, "CropHelper--->待裁剪图片绝对路径：" + filePath);
        Log.i(TAG, "CropHelper--->预设裁剪后图片路径：" + destFile.getAbsolutePath());
        Uri source = Uri.fromFile(sourceFile);
        Uri destination = Uri.fromFile(destFile);
        return new Pair<>(source, destination);
    }

    //创建裁剪后的图片名字
    private static String createCropImgName()
    {
        return new StringBuffer().append("CropImg_")
                .append(String.valueOf(System.currentTimeMillis()))
                .append(".jpg").toString();
    }

    /**
     * 裁剪后根据intent获取数据
     *
     * @return Pair中第一个数据为裁剪后图片的Uri，第二个为裁剪后图片的绝对路径
     */
    public static Pair<Uri, String> getCropedData(Activity activity, Intent data)
    {
        if (data == null)
        {
            Log.e(TAG, "CropHelper--->裁剪后intent数据为空");
            return null;
        }

        Uri uri = getUriFromResultIntent(data);
        String path = getAbsPathByUri(activity, uri);
        return new Pair<>(uri, path);
    }

    /**
     * 裁剪完成后根据intent获取裁剪后图片的Uri
     */
    private static Uri getUriFromResultIntent(Intent intent)
    {
        return Crop.getOutput(intent);
    }

    /**
     * 裁剪完成后根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     */
    @TargetApi(19)
    private static String getAbsPathByUri(Activity context, Uri imageUri)
    {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri))
        {
            if (isExternalStorageDocument(imageUri))
            {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type))
                {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri))
            {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri))
            {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type))
                {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type))
                {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type))
                {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme()))
        {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme()))
        {
            return imageUri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs)
    {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try
        {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst())
            {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally
        {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri)
    {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri)
    {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
