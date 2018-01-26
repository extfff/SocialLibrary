package com.vendor.social.auth;

import android.app.Activity;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.vendor.social.AuthApi;
import com.vendor.social.Social;
import com.vendor.social.model.AuthType;

/**
 * 微信登陆
 * Created by ljfan on 16/04/19.
 */
public class WeiXinAuth extends AuthApi {

    public WeiXinAuth(Activity act) {
        super(act);
        setAuthType(AuthType.WEIXIN);
    }

    @Override
    public void doOauthVerify(){
        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat";

        IWXAPI api = WXAPIFactory.createWXAPI(mActivity, Social.getWeixinId(), true);
        api.registerApp(Social.getWeixinId());

        api.sendReq(req);
    }
}
