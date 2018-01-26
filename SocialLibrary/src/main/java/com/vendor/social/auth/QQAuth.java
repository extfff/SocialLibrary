package com.vendor.social.auth;

import android.app.Activity;

import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.vendor.social.AuthApi;
import com.vendor.social.Social;
import com.vendor.social.model.AuthType;
import com.vendor.social.model.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * qq登陆
 * Created by ljfan on 16/04/19.
 */
public class QQAuth extends AuthApi {

    private static final String SCOPE = "get_user_info,get_simple_userinfo,get_user_profile";

    private Tencent mTencent;

    public QQAuth(Activity act) {
        super(act);

        setAuthType(AuthType.QQ);
    }

    @Override
    public void doOauthVerify() {
        mTencent = com.tencent.tauth.Tencent.createInstance(Social.getTencentId(), mActivity);

        mTencent.login(mActivity, SCOPE, listener);
    }

    private IUiListener listener = new IUiListener() {

        @Override
        public void onError(UiError uiError) {
            setErrorCallBack(uiError.errorMessage);
        }

        @Override
        public void onComplete(Object arg0) {
            try {
                JSONObject jsonObject = new JSONObject(arg0.toString());
                String access_token = jsonObject.getString("access_token");
                String openId = jsonObject.getString("openid");
                String expires_in = jsonObject.getString("expires_in");

                mTencent.getQQToken().setOpenId(openId);
                mTencent.getQQToken().setAccessToken(access_token, expires_in);

                UserInfo info = new UserInfo(mActivity, mTencent.getQQToken());
                info.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        try {
                            JSONObject jsonObject = new JSONObject(o.toString());
                            String nickname = jsonObject.getString("nickname");
                            String avatar = jsonObject.getString("figureurl_2");
                            setCompleteCallBack(new User(mTencent.getOpenId(), nickname, avatar));
                        } catch (JSONException e) {
                            setErrorCallBack(e.getMessage());
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                        setErrorCallBack(uiError.errorMessage);
                    }

                    @Override
                    public void onCancel() {
                        setCancelCallBack();
                    }
                });
            } catch (JSONException e) {
                setErrorCallBack(e.getMessage());
            }
        }

        @Override
        public void onCancel() {
            setCancelCallBack();
        }
    };

    public Tencent getTencent(){
        return mTencent;
    }

    public IUiListener getUIListener(){
        return listener;
    }
}
