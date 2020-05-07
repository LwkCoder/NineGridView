package com.lwkandroid.widget.ninegridview;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;
import java.util.UUID;

/**
 * 数据实体类
 *
 * @author LWK
 */
public final class NineGridBean implements Parcelable
{
    private String id;
    private String thumbUrl;
    private String originUrl;
    private String transitionName;

    public NineGridBean(String originUrl)
    {
        this(originUrl, null);
    }

    public NineGridBean(String originUrl, String thumbUrl)
    {
        this(originUrl, thumbUrl, null);
    }

    public NineGridBean(String originUrl, String thumbUrl, String transitionName)
    {
        this.thumbUrl = thumbUrl;
        this.originUrl = originUrl;
        this.transitionName = transitionName;
        this.id = UUID.randomUUID().toString();
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

    public String getTransitionName()
    {
        return transitionName;
    }

    public void setTransitionName(String transitionName)
    {
        this.transitionName = transitionName;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "NineGridBean{" +
                "id='" + id + '\'' +
                ", thumbUrl='" + thumbUrl + '\'' +
                ", originUrl='" + originUrl + '\'' +
                ", transitionName='" + transitionName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        NineGridBean that = (NineGridBean) o;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            return Objects.equals(id, that.id);
        } else
        {
            return id.equals(that.id);
        }
    }

    @Override
    public int hashCode()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            return Objects.hash(id);
        } else
        {
            return id.hashCode();
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.id);
        dest.writeString(this.thumbUrl);
        dest.writeString(this.originUrl);
        dest.writeString(this.transitionName);
    }

    protected NineGridBean(Parcel in)
    {
        this.id = in.readString();
        this.thumbUrl = in.readString();
        this.originUrl = in.readString();
        this.transitionName = in.readString();
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
