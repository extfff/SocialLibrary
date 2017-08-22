package com.vendor.social;

import android.app.Activity;
import android.content.Intent;

import com.tencent.tauth.Tencent;
import com.vendor.social.model.ShareContent;
import com.vendor.social.model.ShareType;
import com.vendor.social.share.QQShare;
import com.vendor.social.share.QQZoneShare;
import com.vendor.social.share.WeiboShare;
import com.vendor.social.share.WeixinCircleShare;
import com.vendor.social.share.WeixinShare;

/**
 * 分享平台公共组件模块
 * Created by ljfan on 16/4/19.
 */
public class ShareApi {

    public Activity mActivity;

    private static int mShareType;
    
    private static OnShareListener mShareListener;

    private ShareContent mShareContent;

    public ShareApi(Activity act) {
        mActivity = act;
    }

    public void setShareType(int shareType){
        mShareType = shareType;
    }

    public void doShare(){}

    /**
     * 执行登陆操作
     * @param act activity
     * @param shareType {@link ShareType}
     * @param l 回调监听
     */
    public static ShareApi doShare(Activity act, int shareType, ShareContent content, OnShareListener l){
        ShareApi shareApi;

        switch (shareType){
            case ShareType.QQ:
                shareApi = new QQShare(act);
                break;
            case ShareType.QQ_ZONE:
                shareApi = new QQZoneShare(act);
                break;
            case ShareType.WEIBO:
                shareApi = new WeiboShare(act);
                break;
            case ShareType.WEIXIN:
                shareApi = new WeixinShare(act);
                break;
            case ShareType.WEIXIN_CIRCLE:
                shareApi = new WeixinCircleShare(act);
                break;
            default:
                throw new IllegalStateException("error share type !");
        }

        shareApi.setShareContent(content);
        shareApi.setOnShareListener(l);
        shareApi.doShare();

        return shareApi;
    }

    /**
     * 设置分享内容
     * @return ShareContent ShareContent
     */
    public ShareContent getShareContent(){
        if(mShareContent == null){
            mShareContent = new ShareContent.Builder().build();
        }

        return mShareContent;
    }

    /**
     * 设置分享内容
     * @param content {@link ShareContent}
     */
    public void setShareContent(ShareContent content){
        mShareContent = content;
    }

    /**
     * 应用分享成功回调
     * @param requestCode requestCode
     * @param resultCode resultCode
     * @param data data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (mShareType){
            case ShareType.QQ:
                if(this instanceof QQShare) {
                    Tencent.onActivityResultData(requestCode, resultCode, data, ((QQShare) this).getQQCallbackListener());
                }
                break;
            case ShareType.QQ_ZONE:
                if(this instanceof QQZoneShare) {
                    Tencent.onActivityResultData(requestCode, resultCode, data, ((QQZoneShare) this).getQQCallbackListener());
                }
                break;
            case ShareType.WEIBO:

                break;
        }
    }

    /**
     * 设置分享回调
     * @param l l
     */
    public void setOnShareListener(OnShareListener l){
        mShareListener = l;
    }

    /**
     * 返回分享成功
     */
    public static void callbackShareOk(){
        if(mShareListener != null){
            mShareListener.onShareOk(mShareType);
        }
    }

    /**
     * 返回分享失败
     * @param msg 错误详情
     */
    public static void callbackShareFail(String msg){
        if(mShareListener != null){
            mShareListener.onShareFail(mShareType, msg);
        }
    }

    /**
     * 分享回调
     */
    public interface OnShareListener {

        /**
         * 分享回调-成功分享
         */
        void onShareOk(int type);

        /**
         * 分享回调-支付分享
         */
        void onShareFail(int type, String msg);
    }
}
