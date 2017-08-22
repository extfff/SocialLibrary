package com.vendor.social.share;

import android.app.Activity;
import android.os.Bundle;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.vendor.social.R;
import com.vendor.social.ShareApi;
import com.vendor.social.Social;
import com.vendor.social.model.ShareType;
import com.vendor.social.utils.ResConvert;

import java.util.List;

/**
 * 分享平台公共组件模块-qq分享
 * Created by ljfan on 16/4/19.
 */
public class QQShare extends ShareApi{

    public QQShare(Activity act) {
        super(act);
    }

    @Override
    public void doShare(){
        setShareType(ShareType.QQ);

        Bundle params = new Bundle();
        params.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_KEY_TYPE, com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_TITLE, getShareContent().getTitle());
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_SUMMARY,  getShareContent().getText());
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_TARGET_URL,  getShareContent().getTargetUrl());

        List<String> iconList = getShareContent().getIconList();
        if(iconList != null && iconList.size() > 0) {  //分享图片
            params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_URL, iconList.get(0));
        }else {
            params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, ResConvert.resToFile(mActivity, getShareContent().getAppIcon()));
        }
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_APP_NAME,  getShareContent().getAppName());
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");

        Tencent tencent = Tencent.createInstance(Social.getTencentId(), mActivity);
        tencent.shareToQQ(mActivity, params, mQQCallbackListener);
    }

    private IUiListener mQQCallbackListener = new IUiListener() {

        @Override
        public void onError(UiError arg0) {
            callbackShareFail(arg0.errorMessage);
        }

        @Override
        public void onComplete(Object arg0) {
            callbackShareOk();
        }

        @Override
        public void onCancel() {
            callbackShareFail(mActivity.getString(R.string.share_cancel));
        }
    };

    /**
     * 应用调用Andriod_SDK接口时，如果要成功接收到回调，需要在调用接口的Activity的onActivityResult方法中增加如下代码:
     * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     * Tencent.onActivityResultData(requestCode,resultCode,data,listener);
     * }
     * @return 回调监听
     */
    public IUiListener getQQCallbackListener(){
        return mQQCallbackListener;
    }
}
