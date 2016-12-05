package com.lwk.imagepicker.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lwk.imagepicker.ImagePicker;
import com.lwk.imagepicker.R;
import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.bean.ImageFloderBean;
import com.lwk.imagepicker.model.ImagePickerMode;
import com.lwk.imagepicker.presenter.ImagePickerGridPresenter;
import com.lwk.imagepicker.utils.CropHelper;
import com.lwk.imagepicker.utils.OtherUtils;
import com.lwk.imagepicker.view.adapter.ImageGridAdapter;
import com.lwk.imagepicker.view.base.ImagePickerBaseActivity;
import com.lwk.imagepicker.view.impl.GridPickerViewImpl;
import com.lwk.imagepicker.view.pop.ImagePickerFloderPop;
import com.lwk.imagepicker.view.widget.ImagePickerActionBar;

import java.io.File;

/**
 * 图片选择界面
 */
public class ImagePickerGridActivity extends ImagePickerBaseActivity implements GridPickerViewImpl
{
    //图片选择activity跳转到拍照界面的requestCode
    private static final int sREQUESTCODE_TAKE_PHOTO = 101;
    //扫描本地数据成功
    private static final int FLAG_SCAN_DATA_SUCCESS = 102;
    //扫描本地数据失败
    private static final int FLAG_SCAN_DATA_FAIL = 103;
    //sdk23获取sd卡读写权限的requestCode
    private static final int REQUEST_CODE_SDCARD = 110;
    //sdk23获取sd卡拍照权限的requestCode
    private static final int REQUEST_CODE_CAMERA = 111;
    private ImagePickerGridPresenter mPresenter;
    private GridView mGridView;
    private View mRlBottom;
    private TextView mTvCurFloder;
    private Button mBtnOk;
    private ProgressBar mPgbLoading;
    private ImageGridAdapter mAdapter;
    private ImageFloderBean mCurFloader;
    //当前拍照文件路径
    private String mCurTakePhotoPath;
    //是否为第一次加载
    private boolean isFristLoading = true;

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        super.beforeOnCreate(savedInstanceState);
        mPresenter = new ImagePickerGridPresenter(this);
        //沉浸式状态栏
        OtherUtils.changeStatusBarColor(this, getResources().getColor(R.color.black_statusbar));
    }

    @Override
    protected int setContentViewId()
    {
        return R.layout.activity_image_picker_grid;
    }

    @Override
    protected void initUI()
    {
        //防止在此界面发生权限更改时，界面重新onCreate()导致的空指针
        if (ImagePicker.getInstance().getOptions() == null)
        {
            finish();
            return;
        }

        ImagePickerActionBar actionBar = findView(R.id.cab_imagepicker_gridactivity);
        actionBar.setLeftLayoutAsBack(this);
        actionBar.setBackgroundColor(getResources().getColor(R.color.black_actionbar));
        actionBar.setTitleText(R.string.tv_imagepicker_gridactivity_title);
        if (ImagePicker.getInstance().getOptions().getPickerMode() == ImagePickerMode.MUTIL)
        {
            actionBar.setRightTvText(R.string.tv_imagepicker_actionbar_preview);
            actionBar.setRightLayoutClickListener(this);
        }
        mGridView = findView(R.id.gridView_imagepicker_gridactivity);
        mAdapter = new ImageGridAdapter(this, null, mPresenter);
        mGridView.setAdapter(mAdapter);
        mRlBottom = findView(R.id.bottom_imagepicker_gridactivity);
        mTvCurFloder = findView(R.id.tv_imagepicker_bottom_floder);
        mBtnOk = findView(R.id.btn_imagepicker_bottom_ok);
        mPgbLoading = findView(R.id.pgb_imagepicker_gridactivity);

        addClick(mBtnOk);
        addClick(R.id.fl_imagepicker_bottom_floder);
    }

    @Override
    protected void initData()
    {
        super.initData();
        scanData();
    }

    //扫描本地媒体数据
    private void scanData()
    {
        //sdk23以上需要检查权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            int checkResult = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            //没有权限时
            if (checkResult != PackageManager.PERMISSION_GRANTED)
            {
                //对权限做出解释
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    new AlertDialog.Builder(this).setCancelable(false)
                            .setTitle(R.string.dialog_imagepicker_permission_title)
                            .setMessage(R.string.dialog_imagepicker_permission_sdcard_message)
                            .setPositiveButton(R.string.dialog_imagepicker_permission_confirm, new DialogInterface.OnClickListener()
                            {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_SDCARD);
                                }
                            }).create().show();
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_SDCARD);
                return;
            }

            //开启扫描
            mPresenter.scanAllData(this);
        } else
        {
            //sdk23以下直接开启扫描
            mPresenter.scanAllData(this);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //刷新UI
        if (!isFristLoading)
        {
            mPresenter.selectedNumChanged();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void startScanData()
    {
        mPgbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void scanDataSuccess()
    {
        mMainHanlder.sendEmptyMessage(FLAG_SCAN_DATA_SUCCESS);
    }

    @Override
    public void scanDataFail()
    {
        mMainHanlder.sendEmptyMessage(FLAG_SCAN_DATA_FAIL);
    }

    @Override
    public void clickTakePhoto()
    {
        //sdk23以上需要检查权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            int checkResult = checkSelfPermission(Manifest.permission.CAMERA);
            //没有权限时
            if (checkResult != PackageManager.PERMISSION_GRANTED)
            {
                //对权限做出解释
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                {
                    new AlertDialog.Builder(this).setCancelable(false)
                            .setTitle(R.string.dialog_imagepicker_permission_title)
                            .setMessage(R.string.dialog_imagepicker_permission_camera_message)
                            .setPositiveButton(R.string.dialog_imagepicker_permission_confirm, new DialogInterface.OnClickListener()
                            {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                                }
                            }).create().show();
                    return;
                }

                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                return;
            }

            //拍照
            startTakePhoto();
        } else
        {
            //sdk22以下直接拍照
            startTakePhoto();
        }
    }

    private void startTakePhoto()
    {
        if (!OtherUtils.isSdExist())
        {
            showToast(R.string.error_no_sdcard);
            return;
        }
        // 拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
        // 有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //自己指定保存路径
        File tempPicFile = mPresenter.getTakePhotoPath();
        mCurTakePhotoPath = tempPicFile.getAbsolutePath();
        if (getAndroidSDKVersion() < 24)
            doTakePhotoBeforeSdk24(intent, tempPicFile);
        else
            doTakePhotoAfterSdk24(intent, tempPicFile);
    }

    //获取当前sdk版本
    public int getAndroidSDKVersion()
    {
        int version = 0;
        try
        {
            version = Integer.valueOf(Build.VERSION.SDK);
        } catch (NumberFormatException e)
        {
        }
        return version;
    }

    /**
     * sdk24之前拍照
     */
    private void doTakePhotoBeforeSdk24(Intent intent, File tempPicFile)
    {
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempPicFile));//将拍取的照片保存到指定URI
        startActivityForResult(intent, sREQUESTCODE_TAKE_PHOTO);
    }

    /**
     * sdk24之后拍照
     */
    private void doTakePhotoAfterSdk24(Intent intent, File tempPicFile)
    {
        Uri imageUri = FileProvider.getUriForFile(this, getResources().getString(R.string.author_imagepicker), tempPicFile);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, sREQUESTCODE_TAKE_PHOTO);
    }

    @Override
    public void onSelectedNumChanged(int curNum, int maxNum)
    {
        if (ImagePicker.getInstance().getOptions().getPickerMode() != ImagePickerMode.SINGLE)
        {
            mBtnOk.setText(getString(R.string.btn_imagepicker_ok, curNum, maxNum));
            if (curNum == 0)
                mBtnOk.setEnabled(false);
            else
                mBtnOk.setEnabled(true);
        }
    }

    @Override
    public void onNumLimited(int maxNum)
    {
        showToast(getString(R.string.warning_imagepicker_limit_num, maxNum));
    }

    @Override
    public void onSingleImageSelected(ImageBean imageBean)
    {
        if (ImagePicker.getInstance().getOptions().isNeedCrop())
        {
            int max = OtherUtils.getScreenWidth(this);
            CropHelper.startCropInRect(this
                    , imageBean.getImagePath()
                    , ImagePicker.getInstance().getOptions().getCachePath()
                    , max, max);
        } else
        {
            ImagePicker.getInstance().handleSingleModeListener(imageBean);
        }
    }

    @Override
    public void onCurFloderChanged(ImageFloderBean curFloder)
    {
        if (curFloder == null)
            return;

        mCurFloader = curFloder;
        if (OtherUtils.isEquals(ImageFloderBean.ALL_FLODER_ID, mCurFloader.getFloderId()))
        {
            mAdapter.refreshData(mPresenter.getAllImages());
        } else
        {
            mAdapter.refreshData(mPresenter.getImagesByFloder(curFloder));
        }

        mTvCurFloder.setText(mCurFloader.getFloderName());
        mGridView.smoothScrollToPosition(0);//滑动到顶部
    }

    @Override
    public void enterDetailActivity(int startPosition)
    {
        ImagePickerDetailActivity.start(this, startPosition, mCurFloader.getFloderId());
    }

    @Override
    protected void onHandlerMessage(Message msg)
    {
        super.onHandlerMessage(msg);
        if (msg.what == FLAG_SCAN_DATA_SUCCESS)
        {
            mPgbLoading.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            mRlBottom.setVisibility(View.VISIBLE);
            //默认展示"全部图片"数据
            mPresenter.changeFloder(mPresenter.getFloderById(ImageFloderBean.ALL_FLODER_ID));
            //刷新其他UI
            if (ImagePicker.getInstance().getOptions().getPickerMode() == ImagePickerMode.SINGLE)
            {
                mBtnOk.setVisibility(View.GONE);
            } else
            {
                mBtnOk.setVisibility(View.VISIBLE);
                //刷新下确认按钮的文案
                mPresenter.selectedNumChanged();
            }
        } else if (msg.what == FLAG_SCAN_DATA_FAIL)
        {
            mPgbLoading.setVisibility(View.GONE);
            showToast(R.string.error_imagepicker_scanfail);
        }
        isFristLoading = false;
    }

    @Override
    protected void onClick(int id, View v)
    {
        if (id == R.id.btn_imagepicker_bottom_ok)
        {
            //选择完毕
            ImagePicker.getInstance().handleMutilModeListener();
        } else if (id == R.id.fl_imagepicker_bottom_floder)
        {
            //展示文件夹菜单
            ImagePickerFloderPop pop = new ImagePickerFloderPop(this, mCurFloader, mPresenter);
            pop.showAtLocation(mRootLayout, Gravity.CENTER, 0, 0);
        } else if (id == R.id.fl_imagepicker_actionbar_right)
        {
            //预览已选图片
            ImagePickerPreviewActivity.start(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
        {
            if (mAdapter != null)
                mAdapter.reset();
            if (resultCode == 404)
                showToast(R.string.error_imagepicker_parsefail);
            return;
        }
        //拍照返回
        if (requestCode == sREQUESTCODE_TAKE_PHOTO)
        {
            ImageBean imageBean = new ImageBean();
            imageBean.setImagePath(mCurTakePhotoPath);
            mPresenter.singleImageSelected(imageBean);
        }
        //裁剪返回
        else if (requestCode == CropHelper.REQUEST_CODE_CROP)
        {
            Pair<Uri, String> resultData = CropHelper.getCropedData(this, data);
            ImageBean imageBean = new ImageBean();
            imageBean.setImagePath(resultData.second);
            ImagePicker.getInstance().handleSingleModeListener(imageBean);
        }
    }

    @Override
    protected void onDestroy()
    {
        mPresenter.clearData();
        mPresenter = null;
        ImagePicker.getInstance().clear();
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_SDCARD:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mPresenter.scanAllData(this);
                } else
                {
                    showToast(R.string.warning_imagepicker_permission_sdcard_denied);
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                        //处理NerverAsk
                        new AlertDialog.Builder(this).setCancelable(false)
                                .setTitle(R.string.dialog_imagepicker_permission_title)
                                .setMessage(R.string.dialog_imagepicker_permission_sdcard_nerver_ask_message)
                                .setNegativeButton(R.string.dialog_imagepicker_permission_nerver_ask_cancel, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                        ImagePickerGridActivity.this.finish();
                                    }
                                })
                                .setPositiveButton(R.string.dialog_imagepicker_permission_nerver_ask_confirm, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).create().show();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startTakePhoto();
                } else
                {
                    showToast(R.string.warning_imagepicker_permission_camera_denied);
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                        //处理NerverAsk
                        new AlertDialog.Builder(this).setCancelable(false)
                                .setTitle(R.string.dialog_imagepicker_permission_title)
                                .setMessage(R.string.dialog_imagepicker_permission_camera_nerver_ask_message)
                                .setNegativeButton(R.string.dialog_imagepicker_permission_nerver_ask_cancel, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.dialog_imagepicker_permission_nerver_ask_confirm, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).create().show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
