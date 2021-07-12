package com.lwkandroid.widget.ngv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

class Utils
{
    /**
     * drawable转byteArr
     *
     * @param drawable drawable对象
     * @return 字节数组
     */
    public static byte[] drawable2Bytes(Drawable drawable)
    {
        return drawable2Bytes(drawable, Bitmap.CompressFormat.PNG, 100);
    }

    /**
     * drawable转byteArr
     *
     * @param drawable drawable对象
     * @param format   格式
     * @param quality  质量[0-100]
     * @return 字节数组
     */
    public static byte[] drawable2Bytes(Drawable drawable, Bitmap.CompressFormat format, int quality)
    {
        return drawable == null ? null : bitmap2Bytes(getBitmap(drawable), format, quality);
    }

    /**
     * bitmap转byteArr
     *
     * @param bitmap  bitmap对象
     * @param format  格式
     * @param quality 质量 [0-100]
     * @return 字节数组
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format, int quality)
    {
        if (bitmap == null)
        {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, quality, baos);
        return baos.toByteArray();
    }


    /**
     * drawable转bitmap
     *
     * @param drawable drawable对象
     * @return bitmap
     */
    public static Bitmap getBitmap(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null)
            {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0)
        {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else
        {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * byteArr转drawable
     *
     * @param bytes 字节数组
     * @return drawable
     */
    public static Drawable bytes2Drawable(Context context, byte[] bytes)
    {
        return bitmap2Drawable(context, getBitmap(bytes));
    }

    /**
     * byteArr转bitmap
     *
     * @param data 字节数组
     * @return bitmap
     */
    public static Bitmap getBitmap(byte[] data)
    {
        return getBitmap(data, 0);
    }

    /**
     * byteArr转bitmap
     *
     * @param data   字节数组
     * @param offset 偏移量
     * @return bitmap
     */
    public static Bitmap getBitmap(final byte[] data, final int offset)
    {
        if (data.length == 0)
        {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, offset, data.length);
    }


    /**
     * bitmap转drawable
     *
     * @param bitmap bitmap对象
     * @return drawable
     */
    public static Drawable bitmap2Drawable(Context context, Bitmap bitmap)
    {
        return bitmap == null ? null : new BitmapDrawable(context.getResources(), bitmap);
    }

}
