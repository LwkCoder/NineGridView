package com.lwkandroid.ngvsample;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.lwkandroid.rcvadapter.RcvSingleAdapter;
import com.lwkandroid.rcvadapter.holder.RcvHolder;
import com.lwkandroid.rcvadapter.utils.RcvLinearDecoration;
import com.lwkandroid.widget.ninegridview.DefaultNgvAdapter;
import com.lwkandroid.widget.ninegridview.NgvChildImageView;
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
        for (int i = 0; i < 1; i++)
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
            NineGridView nineGridView = holder.findView(R.id.ngv_images);
            DefaultNgvAdapter<String> adapter = new DefaultNgvAdapter<>(9, new GlideDisplayer2());
            adapter.setDataList(createData());
            adapter.setOnChildClickListener(new DefaultNgvAdapter.OnChildClickedListener<String>()
            {
                @Override
                public void onPlusImageClicked(NgvChildImageView plusImageView, int dValueToLimited)
                {

                }

                @Override
                public void onContentImageClicked(int position, String data, NgvChildImageView childImageView)
                {
                    Toast.makeText(childImageView.getContext(), "Click->" + position + "\n" + data, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onImageDeleted(int position, String data)
                {

                }
            });
            nineGridView.setAdapter(adapter);
        }

        private List<String> createData()
        {
            //            String url = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=173843264,1010023015&fm=26&gp=0.jpg";
            String url = "https://c-ssl.duitang.com/uploads/item/201503/21/20150321160159_vVxSC.jpeg";
            List<String> imageInfos = new ArrayList<>();
            for (int i = 0; i < 2; i++)
            {
                imageInfos.add(url);
            }
            return imageInfos;
        }
    }

}
