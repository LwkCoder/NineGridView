package com.lwk.imagepicker.view.pop;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.lwk.imagepicker.R;
import com.lwk.imagepicker.bean.ImageFloderBean;
import com.lwk.imagepicker.presenter.ImagePickerGridPresenter;
import com.lwk.imagepicker.utils.OtherUtils;
import com.lwk.imagepicker.view.adapter.ImagePickerFloderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:选择图片时切换各文件夹的pop
 */
public class ImagePickerFloderPop implements View.OnClickListener, AdapterView.OnItemClickListener, PopupWindow.OnDismissListener
{
    private Activity mContext;
    private PopupWindow mPop;
    private View mLayout;
    private List<ImageFloderBean> mAllFloderList = new ArrayList<>();
    private ImageFloderBean mCurFloder;
    private ListView mListView;
    private ImagePickerGridPresenter mPresenter;

    public ImagePickerFloderPop(Activity context, ImageFloderBean curFloder, ImagePickerGridPresenter presenter)
    {
        this.mContext = context;
        this.mCurFloder = curFloder;
        this.mPresenter = presenter;
        this.mAllFloderList = mPresenter.getAllFloderList();

        mLayout = LayoutInflater.from(context).inflate(R.layout.layout_imagepicker_floder_pop, null);
        mPop = new PopupWindow(mLayout, RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        mPop.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句。
        mPop.setFocusable(true);//设置pop可获取焦点
        mPop.setAnimationStyle(R.style.FloderPopAnimStyle);//设置显示、消失动画
        mPop.setOutsideTouchable(true);//设置点击外部可关闭pop
        mPop.setOnDismissListener(this);

        mListView = (ListView) mLayout.findViewById(R.id.lv_floderpop);
        ImagePickerFloderAdapter adapter = new ImagePickerFloderAdapter(context, mAllFloderList, curFloder);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);

        mLayout.findViewById(R.id.view_floderpop_bg).setOnClickListener(this);
    }

    public void showAtLocation(View view, int gravity, int x, int y)
    {
        mPop.showAtLocation(view, gravity, x, y);
        //Activity窗口变暗
        setActivityBackgroundAlpha(0.2f);
        //显示当前展示文件夹位置
        final int p = mAllFloderList.indexOf(mCurFloder);
        // 增加绘制监听
        ViewTreeObserver vto = mListView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                // 移除监听
                mListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mListView.smoothScrollToPosition(p);
            }
        });
    }

    public boolean isShowing()
    {
        return mPop != null && mPop.isShowing();
    }

    public void dismiss()
    {
        mPop.dismiss();
        mPop = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        ImageFloderBean floder = mAllFloderList.get(position);

        if (mCurFloder != null && !OtherUtils.isEquals(mCurFloder.getFloderId(), floder.getFloderId()))
            mPresenter.changeFloder(floder);
        dismiss();
    }

    @Override
    public void onDismiss()
    {
        //将Activity窗口变为透明
        setActivityBackgroundAlpha(1f);
    }

    //改变Activity窗口透明度
    private void setActivityBackgroundAlpha(final float alpha)
    {
        if (mContext != null)
        {
            WindowManager.LayoutParams layoutParams = mContext.getWindow().getAttributes();
            layoutParams.alpha = alpha;
            mContext.getWindow().setAttributes(layoutParams);
        }
    }

    @Override
    public void onClick(View v)
    {
        int i = v.getId();
        if (i == R.id.view_floderpop_bg)
        {
            dismiss();
        }
    }
}
