package com.lwk.imagepicker.presenter;

import android.content.Context;

import com.lwk.imagepicker.ImagePicker;
import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.bean.ImageFloderBean;
import com.lwk.imagepicker.model.ImageScanModel;
import com.lwk.imagepicker.presenter.impl.GridPickerPresentImpl;
import com.lwk.imagepicker.view.impl.GridPickerViewImpl;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Function:图片选择器Presenter
 */
public class ImagePickerGridPresenter implements GridPickerPresentImpl
{
    private GridPickerViewImpl mViewImpl;

    private ImageScanModel mModel;

    public ImagePickerGridPresenter(GridPickerViewImpl v)
    {
        this.mViewImpl = v;
        mModel = ImageScanModel.getInstance();
    }

    @Override
    public void scanAllData(final Context context)
    {
        mViewImpl.startScanData();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                boolean b = mModel.scanAllData(context);
                if (b)
                    mViewImpl.scanDataSuccess();
                else
                    mViewImpl.scanDataFail();
            }
        }).start();
    }

    @Override
    public List<ImageBean> getAllImages()
    {
        return mModel.getAllImages();
    }

    @Override
    public Map<String, ImageFloderBean> getAllFloderMap()
    {
        return mModel.getAllFloderMap();
    }

    @Override
    public List<ImageFloderBean> getAllFloderList()
    {
        return mModel.getAllFloderList();
    }

    @Override
    public void takePhoto()
    {
        mViewImpl.clickTakePhoto();
    }

    @Override
    public void selectedNumChanged()
    {
        mViewImpl.onSelectedNumChanged(ImagePicker.getInstance().getAllSelectedImages().size()
                , ImagePicker.getInstance().getOptions().getLimitNum());
    }

    @Override
    public void numLimitedWarning()
    {
        mViewImpl.onNumLimited(ImagePicker.getInstance().getOptions().getLimitNum());
    }

    @Override
    public List<ImageBean> getImagesByFloder(ImageFloderBean floder)
    {
        return mModel.getImagesByFloder(floder);
    }

    @Override
    public ImageFloderBean getFloderById(String id)
    {
        return mModel.getFloderById(id);
    }

    @Override
    public void singleImageSelected(ImageBean imageBean)
    {
        mViewImpl.onSingleImageSelected(imageBean);
    }

    @Override
    public File getTakePhotoPath()
    {
        String tempPicRootPath = ImagePicker.getInstance().getOptions().getCachePath();
        String mNewPicName = new StringBuffer().append("IMG_")
                .append(String.valueOf(System.currentTimeMillis()))
                .append(".jpg").toString().trim();
        File file = new File(tempPicRootPath, mNewPicName);
        return file;
    }

    @Override
    public void changeFloder(ImageFloderBean floderBean)
    {
        mViewImpl.onCurFloderChanged(floderBean);
    }

    @Override
    public void enterDetailActivity(int startPosition)
    {
        mViewImpl.enterDetailActivity(startPosition);
    }

    @Override
    public void clearData()
    {
        mModel.clearData();
    }

}
