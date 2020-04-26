package com.lwk.libsample;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.lwkandroid.rcvadapter.RcvSingleAdapter;
import com.lwkandroid.rcvadapter.holder.RcvHolder;
import com.lwkandroid.rcvadapter.utils.RcvLinearDecoration;
import com.lwkandroid.widget.ninegridview.NineGirdImageContainer;
import com.lwkandroid.widget.ninegridview.NineGridBean;
import com.lwkandroid.widget.ninegridview.NineGridView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WechatActivity extends AppCompatActivity
{
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat);

        mRecyclerView = findViewById(R.id.rcv_wechat);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(RcvLinearDecoration.createDefaultVertical(this));

        List<String> dataList = new LinkedList<>();
        for (int i = 0; i < 20; i++)
        {
            dataList.add("i=" + i);
        }

        mRecyclerView.setAdapter(new MyAdapter(this, dataList));
    }

    public static class MyAdapter extends RcvSingleAdapter<String>
    {

        public MyAdapter(Context context, List<String> datas)
        {
            super(context, R.layout.adapter_wechat, datas);
        }

        @Override
        public void onBindView(RcvHolder holder, String itemData, int position)
        {
            NineGridView nine_grid_view = holder.findView(R.id.ngv_images);
            nine_grid_view.setImageLoader(new GlideImageLoader());
            //设置显示列数，默认3列
            nine_grid_view.setColumnCount(3);
            //设置是否为编辑模式，默认为false
            nine_grid_view.setIsEditMode(false);
            //设置单张图片显示时的尺寸，默认100dp
            nine_grid_view.setSingleImageWidth(120);
            //设置单张图片显示时的宽高比，默认1.0f
            nine_grid_view.setSingleImageRatio(0.8f);
            //设置最大显示数量，默认9张
            nine_grid_view.setMaxNum(9);
            //设置图片显示间隔大小，默认3dp
            nine_grid_view.setSpcaeSize(5);
            nine_grid_view.setDataList(createData());
            nine_grid_view.setOnItemClickListener(new NineGridView.onItemClickListener()
            {
                @Override
                public void onNineGirdAddMoreClick(int dValue)
                {

                }

                @Override
                public void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer)
                {
                    Toast.makeText(getContext(), gridBean.getOriginUrl(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer)
                {

                }
            });
        }

        private List<NineGridBean> createData()
        {
//            String url = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=173843264,1010023015&fm=26&gp=0.jpg";
            String url = "https://c-ssl.duitang.com/uploads/item/201503/21/20150321160159_vVxSC.jpeg";
            List<NineGridBean> imageInfos = new ArrayList<>();
            for (int i = 0; i < 2; i++)
            {
                NineGridBean imageInfo = new NineGridBean(url, url);
                imageInfos.add(imageInfo);
            }
            return imageInfos;
        }
    }

}
