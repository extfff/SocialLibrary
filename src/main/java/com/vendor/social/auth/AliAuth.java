package com.vendor.social.auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alipay.sdk.app.AuthTask;
import com.vendor.social.AuthApi;
import com.vendor.social.Social;
import com.vendor.social.model.User;
import com.vendor.social.pay.extra.alipay.AuthResult;
import com.vendor.social.pay.extra.alipay.util.OrderInfoUtil2_0;

import java.util.Map;

/**
 * 支付宝登录
 * Created by ljfan on 16/9/2.
 */
public class AliAuth extends AuthApi {

    public AliAuth(Activity act) {
        super(act);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void doOauthVerify() {
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * authInfo的获取必须来自服务端；
         */
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(
                Social.getAlipayPartner(),
                Social.getAlipaySeller(),
                Social.getAlipayRsaPrivate());
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, Social.getAlipayRsaPrivate());
        final String authInfo = info + "&" + sign;

        new AsyncTask<Void, Void, AuthResult>(){

            @Override
            protected AuthResult doInBackground(Void... params) {
                AuthResult authResult = null;
                try {
                    // 构造AuthTask 对象
                    AuthTask authTask = new AuthTask(mActivity);
                    // 调用授权接口，获取授权结果
                    Map<String, String> result = authTask.authV2(authInfo, true);
                    authResult = new AuthResult(result, true);
                }catch (Exception e){

                }

                return authResult;
            }

            @Override
            protected void onPostExecute(AuthResult result) {
                String resultStatus = result.getResultStatus();

                // 判断resultStatus 为“9000”且result_code
                // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(result.getResultCode(), "200")) {
                    // 获取alipay_open_id，调支付时作为参数extern_token 的value
                    // 传入，则支付账户为该授权账户
                    setCompleteCallBack(new User(result.getAlipayOpenId(), "", ""));
                } else {
                    setErrorCallBack("授权失败");
                }
            }
        }.execute();
    }
}
