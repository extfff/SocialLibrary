package com.vendor.social.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.vendor.social.model.ShareContent;

import java.util.List;

/**
 * bitmap加载
 * Created by vendor on 2017/4/7.
 */
public class BitmapLoader {

    private Context mContext;

    private ShareContent mShareContent;

    private OnLoadImageListener mOnLoadImageListener;

    /**
     * 选取规则 如果iconList不为空 取appIcon
     */
    public void loadIconBitmap(Context context, ShareContent content, OnLoadImageListener l){
        mContext = context;
        mShareContent = content;

        List<String> iconList = mShareContent.getIconList();
        if(iconList != null && iconList.size() > 0){
            loadImage(iconList.get(0), l);
        }else {
            l.onResult(ResConvert.resToBitmap(context, mShareContent.getAppIcon()));
        }
    }

    private void loadImage(final String icon, OnLoadImageListener l){
        mOnLoadImageListener = l;

        Glide.with(mContext).asBitmap().load(icon).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if(mOnLoadImageListener != null) {
                    mOnLoadImageListener.onResult(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                if(mOnLoadImageListener != null) {
                    if (!icon.startsWith("drawable://")) {
                        loadImage("drawable://" + mShareContent.getAppIcon(), mOnLoadImageListener);
                    } else {
                        mOnLoadImageListener.onResult(null);
                    }
                }
                mOnLoadImageListener = null;  //下载失败会触发重试 我们不需要这个操作
            }
        });
    }

    public interface OnLoadImageListener{

        void onResult(Bitmap bitmap);
    }
}
