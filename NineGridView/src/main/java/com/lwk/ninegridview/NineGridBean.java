package com.lwk.ninegridview;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data source
 * NOTE:T class must implemente the interface of Parcelable
 */

public class NineGridBean<T extends Parcelable> implements Parcelable
{
    private String thumbUrl;
    private String originUrl;
    private T t;

    public NineGridBean(String thumbUrl, String originUrl, T t)
    {
        this.thumbUrl = thumbUrl;
        this.originUrl = originUrl;
        this.t = t;
    }

    public String getThumbUrl()
    {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl)
    {
        this.thumbUrl = thumbUrl;
    }

    public String getOriginUrl()
    {
        return originUrl;
    }

    public void setOriginUrl(String originUrl)
    {
        this.originUrl = originUrl;
    }

    public T getT()
    {
        return t;
    }

    public void setT(T t)
    {
        this.t = t;
    }

    @Override
    public String toString()
    {
        return "NineGridBean{" +
                "thumbUrl='" + thumbUrl + '\'' +
                ", originUrl='" + originUrl + '\'' +
                ", t=" + t +
                '}';
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.thumbUrl);
        dest.writeString(this.originUrl);
        dest.writeString(t.getClass().getName());//Write the class name of T class
        dest.writeParcelable(this.t, flags);
    }

    protected NineGridBean(Parcel in)
    {
        this.thumbUrl = in.readString();
        this.originUrl = in.readString();
        String tClassName = in.readString();//Read the class name of T class
        try
        {
            this.t = in.readParcelable(Class.forName(tClassName).getClassLoader());
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static final Parcelable.Creator<NineGridBean> CREATOR = new Parcelable.Creator<NineGridBean>()
    {
        @Override
        public NineGridBean createFromParcel(Parcel source)
        {
            return new NineGridBean(source);
        }

        @Override
        public NineGridBean[] newArray(int size)
        {
            return new NineGridBean[size];
        }
    };
}
