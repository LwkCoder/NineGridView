package com.lwkandroid.ngvsample;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.lwkandroid.widget.ngv.DefaultNgvAdapter;
import com.lwkandroid.widget.ngv.NgvChildImageView;
import com.lwkandroid.widget.ngv.NineGridView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener
{
    private NineGridView mNineGridView;
    private DefaultNgvAdapter<ImageBean> mAdapter;
    private final int REQUEST_CODE_PICKER = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        CheckBox checkBox = (CheckBox) findViewById(R.id.ck_main_is_edit_mode);
        checkBox.setOnCheckedChangeListener(this);

        mNineGridView = findViewById(R.id.ninegridview);

        //设置图片分割间距，默认8dp，默认对应attr属性中divider_line_size
        mNineGridView.setDividerLineSize(TypedValue.COMPLEX_UNIT_DIP, 2);
        //设置是否开启编辑模式，默认false，对应attr属性中enable_edit_mode
        mNineGridView.setEnableEditMode(true);
        //设置水平方向上有多少列，默认3，对应attr属性中horizontal_child_count
        mNineGridView.setHorizontalChildCount(3);
        //设置非编辑模式下，只有一张图片时的尺寸，默认都为0，当宽高都非0才生效，且不会超过NineGridView内部可用总宽度，对应attr属性中single_image_width、single_image_height
        mNineGridView.setSingleImageSize(TypedValue.COMPLEX_UNIT_DIP, 150, 200);
        mAdapter = new DefaultNgvAdapter<>(100, new GlideDisplayer());


        mAdapter.setOnChildClickListener(new DefaultNgvAdapter.OnChildClickedListener<ImageBean>()
        {
            @Override
            public void onPlusImageClicked(ImageView plusImageView, int dValueToLimited)
            {
                //编辑模式下，图片展示数量尚未达到最大数量时，会显示一个“+”号，点击后回调这里
                new ImagePicker()
                        .pickType(ImagePickType.MULTI)
                        .maxNum(dValueToLimited)
                        .start(MainActivity.this, REQUEST_CODE_PICKER);
            }

            @Override
            public void onContentImageClicked(int position, ImageBean data, NgvChildImageView childImageView)
            {
                Toast.makeText(MainActivity.this, "点击position=" + position + "\n" + data.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImageDeleted(int position, ImageBean data)
            {
                Toast.makeText(MainActivity.this, "删除position=" + position + "\n" + data.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        mNineGridView.setAdapter(mAdapter);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b)
    {
        mNineGridView.setEnableEditMode(b);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null)
        {
            List<ImageBean> list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            mAdapter.addDataList(list);
        }
    }
}
