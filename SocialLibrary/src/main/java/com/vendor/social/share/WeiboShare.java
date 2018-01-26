package com.vendor.social.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.vendor.social.ShareApi;
import com.vendor.social.Social;
import com.vendor.social.model.ShareType;
import com.vendor.social.support.weibo.StatusesAPI;
import com.vendor.social.support.weibo.WbBaseActivity;
import com.vendor.social.utils.BitmapLoader;

/**
 * 分享平台公共组件模块－微博分享
 * Created by ljfan on 16/4/19.
 */
public class WeiboShare extends ShareApi {

    private static IWeiboShareAPI mWeiboShareAPI;

    public WeiboShare(Activity act) {
        super(act);
        setShareType(ShareType.WEIBO);
    }

    @Override
    public void doShare() {
        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mActivity, Social.getWeiboAppKey());

        final String content = getShareContent().getTitle() + "\n" + getShareContent().getText() + "\n" + getShareContent().getTargetUrl();

        // 如果未安装微博客户端，设置下载微博对应的回调
        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
            // 创建授权认证信息
            AuthInfo authInfo = new AuthInfo(mActivity,
                    Social.getWeiboAppKey(),
                    Social.getWeiboRedirectUrl(),
                    Social.getWeiboScope());

            SsoHandler ssoHandler = new SsoHandler(mActivity, authInfo);
            ssoHandler.authorize(new WeiboAuthListener() {

                @Override
                public void onWeiboException(WeiboException arg0) {

                }

                @Override
                public void onComplete(Bundle values) {
                    final Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);

                    //获取bitmap
                    new BitmapLoader().loadIconBitmap(mActivity, getShareContent(), new BitmapLoader.OnLoadImageListener() {
                        @Override
                        public void onResult(Bitmap bitmap) {
                            if (bitmap != null) {
                                if (accessToken != null && accessToken.isSessionValid()) {
                                    new StatusesAPI(mActivity, Social.getWeiboAppKey(), accessToken).upload(content, bitmap, null, null, new RequestListener() {

                                        @Override
                                        public void onWeiboException(WeiboException arg0) {
                                            callbackShareFail(arg0.getMessage());
                                        }

                                        @Override
                                        public void onComplete(String arg0) {
                                            callbackShareOk();
                                        }
                                    });
                                }
                            } else {
                                callbackShareFail("ImageLoader load image fail");
                            }
                        }
                    });
                }

                @Override
                public void onCancel() {

                }
            });
        } else {
            mWeiboShareAPI.registerApp();
            WbBaseActivity.allocActivity(mActivity, getShareContent());
        }
    }

    public static IWeiboShareAPI getWeiboShareAPI() {
        return mWeiboShareAPI;
    }
}
