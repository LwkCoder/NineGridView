package com.lwk.imagepicker.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lwk.imagepicker.ImagePicker;
import com.lwk.imagepicker.ImagePickerOptions;
import com.lwk.imagepicker.R;
import com.lwk.imagepicker.bean.ImageBean;
import com.lwk.imagepicker.model.ImagePickerMode;
import com.lwk.imagepicker.presenter.ImagePickerGridPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:图片选择girdview的适配器
 */
public class ImageGridAdapter extends BaseAdapter
{
    private static final int VIEW_TYPE_CAMERA = 1;
    private static final int VIEW_TYPE_IMAGE = 2;
    private ImagePickerOptions mOption;
    private ImagePickerGridPresenter mPresenter;
    private Activity mContext;
    private List<ImageBean> mDataList = new ArrayList<>();
    //每个元素的宽高
    private int mImageLayoutSize;

    public ImageGridAdapter(Activity context, List<ImageBean> datas, ImagePickerGridPresenter p)
    {
        this.mOption = ImagePicker.getInstance().getOptions();
        this.mPresenter = p;
        this.mContext = context;
        if (datas != null && datas.size() > 0)
            mDataList.addAll(datas);
        if (mOption.isShowCamera())
            mDataList.add(0, null);
        //计算每个元素的宽高
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        int cols = screenWidth / densityDpi;
        cols = cols < 3 ? 3 : cols;
        int columnSpace = (int) (2 * context.getResources().getDisplayMetrics().density);
        mImageLayoutSize = (screenWidth - columnSpace * (cols - 1)) / cols;
    }

    @Override
    public int getViewTypeCount()
    {
        return 3;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mOption.isShowCamera() && position == 0)
            return VIEW_TYPE_CAMERA;
        else
            return VIEW_TYPE_IMAGE;
    }

    private int createViewByType(int viewType)
    {
        if (viewType == VIEW_TYPE_CAMERA)
            return R.layout.layout_camera_griditem;
        else
            return R.layout.layout_imagepicker_griditem;
    }

    @Override
    public int getCount()
    {
        return mDataList.size();
    }

    @Override
    public ImageBean getItem(int position)
    {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    private ViewHolder holder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        int viewType = getItemViewType(position);
        if (convertView == null)
        {
            int layoutId = createViewByType(viewType);
            convertView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
            //设置每个item为正方形
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageLayoutSize));
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        if (viewType == VIEW_TYPE_CAMERA)
        {
            setCameraData(holder);
        } else
        {
            setImageData(position, holder, mDataList.get(position));
        }
        return convertView;
    }

    //设置相机入口UI
    private void setCameraData(ViewHolder holder)
    {
        holder.layoutCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.takePhoto();
            }
        });
    }

    //设置图片UI
    private void setImageData(final int position, ViewHolder holder, final ImageBean itemData)
    {
        if (itemData.isThumbFileExist())
            mOption.getDisplayer().display(mContext, holder.imgContent, itemData.getThumbnailPath(), 300, 300);
        else
            mOption.getDisplayer().display(mContext, holder.imgContent, itemData.getImagePath(), 300, 300);

        if (mOption.getPickerMode() == ImagePickerMode.MUTIL)
        {
            if (ImagePicker.getInstance().getAllSelectedImages().contains(itemData))
                holder.ckSelect.setBackgroundResource(R.drawable.ck_imagepicker_selected);
            else
                holder.ckSelect.setBackgroundResource(R.drawable.ck_imagepicker_normal);

            holder.ckSelect.setOnClickListener(new OnSelectedChangeListener(position));
            holder.imgContent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int p = position;
                    ArrayList<ImageBean> datas = new ArrayList<>();
                    datas.addAll(mDataList);
                    //有相机的情况下需要找到原本的数据和position
                    if (ImagePicker.getInstance().getOptions().isShowCamera())
                    {
                        datas.remove(0);
                        p -= 1;
                    }
                    mPresenter.enterDetailActivity(p);
                }
            });
        } else
        {
            holder.ckSelect.setVisibility(View.GONE);
            holder.imgContent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mPresenter.singleImageSelected(itemData);
                }
            });
        }
    }

    //二次封装的OnClickListener，判断选中逻辑
    private class OnSelectedChangeListener implements View.OnClickListener
    {
        private int mPosition;

        OnSelectedChangeListener(int position)
        {
            this.mPosition = position;
        }

        @Override
        public void onClick(View v)
        {
            boolean hasSelected = ImagePicker.getInstance().getAllSelectedImages().contains(getItem(mPosition));
            if (hasSelected)
            {
                //从选中变为不选中
                v.setBackgroundResource(R.drawable.ck_imagepicker_normal);
                ImagePicker.getInstance().removeImage(getItem(mPosition));
                mPresenter.selectedNumChanged();
            } else
            {
                //从不选中变为选中
                boolean success = ImagePicker.getInstance().addImage(getItem(mPosition));
                if (success)
                {
                    v.setBackgroundResource(R.drawable.ck_imagepicker_selected);
                    mPresenter.selectedNumChanged();
                } else
                {
                    mPresenter.numLimitedWarning();
                }
            }
        }
    }

    //刷新数据的方法
    public void refreshData(List<ImageBean> dataList)
    {
        mDataList.clear();
        if (dataList != null && dataList.size() != 0)
            mDataList.addAll(dataList);
        if (mOption.isShowCamera())
            mDataList.add(0, null);
        notifyDataSetChanged();
    }

    //重置选中的方法
    public void reset()
    {
        ImagePicker.getInstance().removeAllSelectedImages();
        mPresenter.selectedNumChanged();
        notifyDataSetChanged();
    }

    private class ViewHolder
    {
        View layoutCamera;
        ImageView imgContent;
        View ckSelect;

        ViewHolder(View convertView)
        {
            if (convertView != null)
            {
                layoutCamera = convertView.findViewById(R.id.fl_imagepicker_griditem_camera);
                imgContent = (ImageView) convertView.findViewById(R.id.img_imagepicker_griditem);
                ckSelect = convertView.findViewById(R.id.ck_imagepicker_griditem);
            }
        }
    }
}
