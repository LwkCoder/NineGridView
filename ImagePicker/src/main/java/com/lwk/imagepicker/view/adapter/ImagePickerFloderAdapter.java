package com.lwk.imagepicker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwk.imagepicker.ImagePicker;
import com.lwk.imagepicker.R;
import com.lwk.imagepicker.bean.ImageFloderBean;
import com.lwk.imagepicker.utils.OtherUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:文件夹pop的适配器
 */
public class ImagePickerFloderAdapter extends BaseAdapter
{
    private Context mContext;
    private List<ImageFloderBean> mDatas = new ArrayList<>();
    private ImageFloderBean mCurFloder;

    public ImagePickerFloderAdapter(Context context, List<ImageFloderBean> datas, ImageFloderBean curFloder)
    {
        this.mContext = context;
        this.mCurFloder = curFloder;
        if (datas != null && datas.size() > 0)
            mDatas.addAll(datas);
    }

    @Override
    public int getCount()
    {
        return mDatas.size();
    }

    @Override
    public ImageFloderBean getItem(int position)
    {
        return mDatas.get(position);
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
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_imagepicker_floder_pop_listitem, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageFloderBean itemData = mDatas.get(position);
        String firstImgPath = itemData.getFirstImgPath();
        if (OtherUtils.isNotEmpty(firstImgPath))
            ImagePicker.getInstance().getOptions().getDisplayer().display(mContext, holder.imgLogo, firstImgPath, 300, 300);
        holder.tvName.setText(itemData.getFloderName());
        holder.tvNum.setText(mContext.getString(R.string.tv_imagepicker_floder_img_num, itemData.getNum()));
        if (mCurFloder != null && OtherUtils.isEquals(itemData.getFloderId(), mCurFloder.getFloderId()))
            holder.imgSelect.setVisibility(View.VISIBLE);
        else
            holder.imgSelect.setVisibility(View.GONE);

        return convertView;
    }

    private class ViewHolder
    {
        ImageView imgLogo;
        TextView tvName;
        TextView tvNum;
        ImageView imgSelect;

        ViewHolder(View convertView)
        {
            if (convertView != null)
            {
                imgLogo = (ImageView) convertView.findViewById(R.id.img_floder_listitem_firstImg);
                tvName = (TextView) convertView.findViewById(R.id.tv_floder_pop_listitem_name);
                tvNum = (TextView) convertView.findViewById(R.id.tv_floder_pop_listitem_num);
                imgSelect = (ImageView) convertView.findViewById(R.id.img_floder_pop_listitem_selected);
            }
        }
    }
}
