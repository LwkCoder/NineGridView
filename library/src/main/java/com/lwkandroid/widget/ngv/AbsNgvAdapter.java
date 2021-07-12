package com.lwkandroid.widget.ngv;

import android.content.Context;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * @description: 通用顶层适配器
 * @author: LWK
 * @date: 2021/5/25 9:26
 */
public abstract class AbsNgvAdapter<P extends View, V extends View, D>
{
    private final List<D> mDataList = new LinkedList<>();
    private int mMaxDataSize;
    private List<OnDataChangedListener<D>> mDataChangedListenerList = new LinkedList<>();

    /**
     * 创建“+”图片控件的方法
     *
     * @param context
     * @return
     */
    abstract P createPlusView(Context context);

    /**
     * 设置“+”图片控件配置项的方法
     *
     * @param plusView
     * @param attrOptions
     */
    abstract void bindPlusView(P plusView, NgvAttrOptions attrOptions);

    /**
     * 创建图片子控件的方法
     *
     * @param context
     * @return
     */
    abstract V createContentView(Context context);

    /**
     * 设置子控件配置项的方法
     *
     * @param childView
     * @param data
     * @param position
     * @param attrOptions
     */
    abstract void bindContentView(V childView, D data, int position, NgvAttrOptions attrOptions);


    public AbsNgvAdapter(int maxDataSize)
    {
        this(maxDataSize, null);
    }

    public AbsNgvAdapter(int maxDataSize, List<D> dataList)
    {
        this.mMaxDataSize = maxDataSize;
        setDataList(dataList);
    }

    public int getMaxDataSize()
    {
        return mMaxDataSize;
    }

    public void setMaxDataSize(int maxDataSize)
    {
        this.mMaxDataSize = maxDataSize;
    }

    public void setDataList(List<D> dataList)
    {
        mDataList.clear();
        if (dataList != null)
        {
            int availableSize = mMaxDataSize - getDataList().size();
            if (dataList.size() > availableSize)
            {
                mDataList.addAll(dataList.subList(0, availableSize - 1));
            } else
            {
                mDataList.addAll(dataList);
            }
        }
        notifyAllDataChanged();
    }

    public void addData(D data)
    {
        addData(data, mDataList.size());
    }

    public void addData(D data, int position)
    {
        if (data == null || getDataList().size() == mMaxDataSize)
        {
            return;
        }

        if (position < 0)
        {
            position = 0;
        }
        if (position > mDataList.size())
        {
            position = mDataList.size();
        }
        mDataList.add(position, data);
        notifyDataAdded(data, position);
    }

    public void addDataList(List<D> dataList)
    {
        addDataList(dataList, mDataList.size());
    }

    public void addDataList(List<D> dataList, int startPosition)
    {
        if (dataList == null)
            return;

        int availableSize = mMaxDataSize - getDataList().size();
        if (startPosition < 0)
        {
            startPosition = 0;
        }
        startPosition = Math.min(startPosition, getDataList().size());

        if (dataList.size() > availableSize)
        {
            mDataList.addAll(startPosition, dataList.subList(0, availableSize - 1));
        } else
        {
            mDataList.addAll(startPosition, dataList);
        }
        notifyDataListAdded(dataList, startPosition);
    }

    public void removeData(int position)
    {
        if (position < 0 || position >= mDataList.size())
        {
            return;
        }
        D data = mDataList.get(position);
        if (mDataList.remove(data))
        {
            notifyDataRemoved(data, position);
        }
    }

    public void removeData(D data)
    {
        int index = mDataList.indexOf(data);
        if (mDataList.remove(data))
        {
            notifyDataRemoved(data, index);
        }
    }

    public void replaceData(int position, D data)
    {
        if (position < 0 || position >= mDataList.size())
        {
            return;
        }
        mDataList.remove(position);
        mDataList.add(position, data);
        notifyDataChanged(data, position);
    }

    public void addDataChangedListener(OnDataChangedListener<D> listener)
    {
        if (listener == null)
        {
            return;
        }
        this.mDataChangedListenerList.add(listener);
    }

    public void removeDataChangedListener(OnDataChangedListener<D> listener)
    {
        this.mDataChangedListenerList.remove(listener);
    }

    public List<D> getDataList()
    {
        return mDataList;
    }

    /**
     * 数据容量是否已经到达极限
     */
    public boolean isDataToLimited()
    {
        return mDataList.size() >= mMaxDataSize;
    }

    /**
     * 距离最大容量的差值
     *
     * @return
     */
    public int getDValueToLimited()
    {
        return Math.max(mMaxDataSize - mDataList.size(), 0);
    }

    /************************************************************数据变化内部通知*************************************************************/

    void notifyAllDataChanged()
    {
        for (OnDataChangedListener<D> listener : mDataChangedListenerList)
        {
            listener.onAllDataChanged(mDataList, mDataList.size() == mMaxDataSize);
        }
    }

    void notifyDataChanged(D data, int position)
    {
        for (OnDataChangedListener<D> listener : mDataChangedListenerList)
        {
            listener.onDataChanged(data, position, mDataList.size() == mMaxDataSize);
        }
    }

    void notifyDataAdded(D data, int position)
    {
        for (OnDataChangedListener<D> listener : mDataChangedListenerList)
        {
            listener.onDataAdded(data, position, mDataList.size() == mMaxDataSize);
        }
    }

    void notifyDataListAdded(List<D> dataList, int startPosition)
    {
        for (OnDataChangedListener<D> listener : mDataChangedListenerList)
        {
            listener.onDataListAdded(dataList, startPosition, mDataList.size() == mMaxDataSize);
        }
    }

    void notifyDataRemoved(D data, int position)
    {
        for (OnDataChangedListener<D> listener : mDataChangedListenerList)
        {
            listener.onDataRemoved(data, position, mDataList.size() == mMaxDataSize);
        }
    }

    /************************************************************各类监听*******************************************************************/

    public interface OnDataChangedListener<D>
    {
        void onAllDataChanged(List<D> dataList, boolean reachLimitedSize);

        void onDataChanged(D data, int position, boolean reachLimitedSize);

        void onDataAdded(D data, int position, boolean reachLimitedSize);

        void onDataListAdded(List<D> dataList, int startPosition, boolean reachLimitedSize);

        void onDataRemoved(D data, int position, boolean reachLimitedSize);
    }
}
