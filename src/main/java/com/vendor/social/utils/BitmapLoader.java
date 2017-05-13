package com.vendor.social.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.vendor.social.model.ShareContent;

import java.util.List;

/**
 * bitmap加载
 * Created by vendor on 2017/4/7.
 */
public class BitmapLoader {

    private Context mContext;

    private ShareContent mShareContent;

    /**
     * 选取规则 如果iconList不为空 取appIcon
     */
    public void loadIconBitmap(Context context, ShareContent content, OnLoadImageListener l){
        mContext = context;
        mShareContent = content;

        String icon;
        List<String> iconList = mShareContent.getIconList();
        if(iconList != null && iconList.size() > 0){
            icon = iconList.get(0);
        }else {
            icon = "drawable://" +  mShareContent.getAppIcon();
        }

        loadImage(icon, l);
    }

    private void loadImage(final String icon, final OnLoadImageListener l){
        Glide.with(mContext)
            .load("http://somefakeurl.com/fakeImage.jpeg")
            .asBitmap()
            .fitCenter()
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if(resource != null){
                        l.onResult(resource);
                    } else if(!icon.startsWith("drawable://")){
                        loadImage("drawable://" +  mShareContent.getAppIcon(), l);
                    } else {
                        l.onResult(null);
                    }
                }
            });
    }

    public interface OnLoadImageListener{

        void onResult(Bitmap bitmap);
    }
}
