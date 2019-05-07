package com.lwkandroid.widget.ninegridview;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data source
 */

public class NineGridBean implements Parcelable
{
    private String thumbUrl;
    private String originUrl;

    public NineGridBean(String originUrl)
    {
        this.originUrl = originUrl;
    }

    public NineGridBean(String originUrl, String thumbUrl)
    {
        this.thumbUrl = thumbUrl;
        this.originUrl = originUrl;
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

    @Override
    public String toString()
    {
        return "NineGridBean{" +
                "thumbUrl='" + thumbUrl + '\'' +
                ", originUrl='" + originUrl + '\'' +
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
    }

    protected NineGridBean(Parcel in)
    {
        this.thumbUrl = in.readString();
        this.originUrl = in.readString();
    }

    public static final Creator<NineGridBean> CREATOR = new Creator<NineGridBean>()
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
