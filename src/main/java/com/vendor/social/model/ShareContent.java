package com.vendor.social.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

/**
 * 分享内容体
 * Created by ljfan on 16/4/19.
 */
public class ShareContent implements Parcelable {

    public static final Creator<ShareContent> CREATOR = new Creator<ShareContent>() {
        @Override
        public ShareContent createFromParcel(Parcel source) {
            return new ShareContent(source);
        }

        @Override
        public ShareContent[] newArray(int size) {
            return new ShareContent[size];
        }
    };
    /** app名字 */
    private String appName;
    /** 标题 */
    private String title;
    /** 内容 */
    private String text;
    /** 分享跳转地址 */
    private String targetUrl;
    private int appIcon;
    private List<String> iconList;

    private ShareContent(){

    }

    protected ShareContent(Parcel in) {
        this.appName = in.readString();
        this.title = in.readString();
        this.text = in.readString();
        this.targetUrl = in.readString();
        this.appIcon = in.readInt();
        this.iconList = in.createStringArrayList();
    }

    public String getTitle() {
        return title;
    }

    public String getAppName() {
        return appName;
    }

    public String getText() {
        return text;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public int getAppIcon() {
        return appIcon;
    }

    public List<String> getIconList() {
        return iconList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appName);
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeString(this.targetUrl);
        dest.writeInt(this.appIcon);
        dest.writeStringList(this.iconList);
    }

    public static class Builder {

        private ShareContent mContent;

        public Builder(){
            mContent = new ShareContent();
        }

        /**
         * 设置应用名称
         * @param appName appName
         * @return Builder
         */
        public Builder setAppName(String appName){
            mContent.appName = appName;
            return this;
        }

        /**
         * 设置分享标题
         * @param title title
         * @return Builder
         */
        public Builder setTitle(String title){
            mContent.title = title;
            return this;
        }

        /**
         * 设置分享内容
         * @param text text
         * @return Builder
         */
        public Builder setText(String text){
            mContent.text = text;
            return this;
        }

        /**
         * 设置分享跳转地址
         * @param targetUrl targetUrl
         * @return Builder
         */
        public Builder setTargetUrl(String targetUrl){
            mContent.targetUrl = targetUrl;
            return this;
        }

        /**
         * 设置分享图标
         * @param icon icon
         * @return Builder
         */
        public Builder setAppIcon(int icon){
            mContent.appIcon = icon;
            return this;
        }

        /**
         * 设置分享图标
         * @param icon icon
         * @return Builder
         */
        public Builder setIcon(String... icon){
            mContent.iconList = Arrays.asList(icon);
            return this;
        }

        /**
         * 设置分享图标
         * @param icon icon
         * @return Builder
         */
        public Builder setIcon(List<String> icon){
            mContent.iconList = icon;
            return this;
        }

        public ShareContent build(){
            return mContent;
        }
    }
}
