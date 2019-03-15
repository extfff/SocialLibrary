package com.vendor.social.support.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.vendor.social.R;
import com.vendor.social.Social;
import com.vendor.social.auth.WeiXinAuth;
import com.vendor.social.model.User;
import com.vendor.social.share.WeixinShare;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 微信回调
 * Created by ljfan on 16/04/19.
 */
public class WXBaseActivity extends Activity implements IWXAPIEventHandler, OnHttpListener {

    private static final String TAG = "WXBaseActivity";

    public static final int CODE_AUTH = 1;
    public static final int CODE_USER_INFO = 2;

    private IWXAPI api;

    private HttpBiz mHttpBiz;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Social.getWeixinId(), false);
        api.handleIntent(getIntent(), this);

    }

    @Override
    public void onReq(BaseReq arg0) {

    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e(TAG, "Wx onResp");

        //登陆返回
        if(resp instanceof SendAuth.Resp){
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    if(mHttpBiz == null){
                        mHttpBiz = new HttpBiz();
                    }
                    mHttpBiz.doGet(CODE_AUTH, Social.getWeixinId(), Social.getWeixinSecret(), ((SendAuth.Resp) resp).code, this);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    WeiXinAuth.setCancelCallBack();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    WeiXinAuth.setErrorCallBack(resp.errStr);
                    break;
                default:
                    WeiXinAuth.setErrorCallBack(resp.errStr);
                    break;
            }
        }else {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    WeixinShare.callbackShareOk();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    WeixinShare.callbackShareFail(getString(R.string.share_cancel));
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    WeixinShare.callbackShareFail(resp.errStr);
                    break;
                default:
                    WeixinShare.callbackShareFail(resp.errStr);
                    break;
            }
        }

        this.finish();
    }

    @Override
    public void onResponse(Response response) {  // 获取用户信息返回
        Log.e(TAG, "Request user info callback");

        if(!response.isResponseOk()){
            Toast.makeText(this, response.errorMsg, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(response.data);
            if (jsonObject.has("errcode")) {  // 发生错误了
                String errorMsg = jsonObject.getString("errmsg");
                Log.e(TAG, errorMsg);
                WeiXinAuth.setErrorCallBack(errorMsg);
                finish();
            } else {
                String openId = jsonObject.getString("openid");

                switch (response.requestCode){
                    case CODE_AUTH:
                        String unionid = jsonObject.getString("unionid");
                        String access_token = jsonObject.getString("access_token");
                        String refresh_token = jsonObject.getString("refresh_token");

                        mHttpBiz.doGet(CODE_USER_INFO, access_token, openId, this); //通过授权信息获取用户信息
                        break;
                    case CODE_USER_INFO:
//                    openid	普通用户的标识，对当前开发者帐号唯一
//                    nickname	普通用户昵称
//                    sex	普通用户性别，1为男性，2为女性
//                    province	普通用户个人资料填写的省份
//                    city	普通用户个人资料填写的城市
//                    country	国家，如中国为CN
//                    headimgurl	用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
//                    privilege	用户特权信息，json数组，如微信沃卡用户为（chinaunicom）
//                    unionid	用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。

                        String nickname = jsonObject.getString("nickname");
                        String headimgurl = jsonObject.getString("headimgurl");

                        WeiXinAuth.setCompleteCallBack(new User(openId, nickname, headimgurl));
                        finish();
                        break;
                }
            }

        } catch (JSONException e) {
            String errorMsg = "Json parse error";
            Log.e(TAG, errorMsg);
            WeiXinAuth.setErrorCallBack(errorMsg);
            finish();
        }
    }
}