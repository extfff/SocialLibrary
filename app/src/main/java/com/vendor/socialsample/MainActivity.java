package com.vendor.socialsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.vendor.social.AuthApi;
import com.vendor.social.PayApi;
import com.vendor.social.ShareApi;
import com.vendor.social.model.AuthType;
import com.vendor.social.model.ShareContent;
import com.vendor.social.model.ShareType;
import com.vendor.social.model.User;
import com.vendor.social.model.WxPayContent;
import com.vendor.social.pay.AliPay;
import com.vendor.social.pay.WxPay;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ShareApi mShareApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ShareContent content = new ShareContent.Builder()
                .setTitle("text title")  //分享标题
                .setText("text content")  //分享内容
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(getString(R.string.app_name))
                .setTargetUrl(null)  //图片地址 如果为空会取app图标
                .setTargetUrl("http://www.baidu.com")  //最终跳转位置
                .build();

        //微信分享
        findViewById(R.id.btn_share_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareApi = ShareApi.doShare(MainActivity.this, ShareType.WEIXIN, content, onShareListener);
            }
        });
        //微信朋友圈分享
        findViewById(R.id.btn_share_wx_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareApi = ShareApi.doShare(MainActivity.this, ShareType.WEIXIN_CIRCLE, content, onShareListener);
            }
        });
        //qq分享
        findViewById(R.id.btn_share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareApi = ShareApi.doShare(MainActivity.this, ShareType.QQ, content, onShareListener);
            }
        });
        //qq空间分享
        findViewById(R.id.btn_share_qq_zone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareApi = ShareApi.doShare(MainActivity.this, ShareType.QQ_ZONE, content, onShareListener);
            }
        });
        //微博分享
        findViewById(R.id.btn_share_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareApi = ShareApi.doShare(MainActivity.this, ShareType.WEIBO, content, onShareListener);
            }
        });
        //微信登录
        findViewById(R.id.btn_login_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthApi.doOauthVerify(MainActivity.this, AuthType.WEIXIN, onAuthListener);
            }
        });
        //qq登录
        findViewById(R.id.btn_login_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthApi.doOauthVerify(MainActivity.this, AuthType.QQ, onAuthListener);
            }
        });
        //微博登录
        findViewById(R.id.btn_login_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthApi.doOauthVerify(MainActivity.this, AuthType.WEIBO, onAuthListener);
            }
        });
        //微信支付
        findViewById(R.id.btn_pay_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PayApi.pay(MainActivity.this, )  //也可以和分享  登录 一样的处理 不过鉴于分享差异性比较大  建议以下写法
                WxPay wxApi = new WxPay(MainActivity.this);
                wxApi.setOnPayListener(onPayListener);

                try {
                    JSONObject jsonObject = new JSONObject("source data...");  //服务端获取 客户端拼凑的Library也有提供，不过不放出使用方法，毕竟已经过时，如需要请联系作者
                    jsonObject = jsonObject.getJSONObject("pay_message");

                    WxPayContent req = new WxPayContent();
                    req.appid = jsonObject.getString("appid");
                    req.partnerid = jsonObject.getString("partnerid");
                    req.prepayid = jsonObject.getString("prepayid");
                    req.packageValue = jsonObject.getString("packagestr");
                    req.noncestr = jsonObject.getString("noncestr");
                    req.timestamp = jsonObject.getString("timestamp");
                    req.sign = jsonObject.getString("sign");
                    wxApi.pay(req);
                } catch (JSONException ignored) {

                }
            }
        });
        //支付宝支付
        findViewById(R.id.btn_pay_alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AliPay alipayApi = new AliPay(MainActivity.this);
                alipayApi.setOnPayListener(onPayListener);

                try {
                    //服务端拼凑 客户端拼凑的Library也有提供，不过不放出使用方法，毕竟已经过时，如需要请联系作者
                    JSONObject jsonObject = new JSONObject("source data...");
                    alipayApi.pay(jsonObject.getString("pay_message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ShareApi.OnShareListener onShareListener = new ShareApi.OnShareListener() {
        @Override
        public void onShareOk(int type) {
            Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onShareFail(int type, String msg) {
            Toast.makeText(MainActivity.this, "分享失败:" + msg, Toast.LENGTH_SHORT).show();
        }
    };

    private AuthApi.OnAuthListener onAuthListener = new AuthApi.OnAuthListener() {
        @Override
        public void onComplete(int type, User user) {
            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int type, String error) {
            Toast.makeText(MainActivity.this, "登录失败:" + error, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(int type) {
            Toast.makeText(MainActivity.this, "登录取消", Toast.LENGTH_SHORT).show();
        }
    };

    private PayApi.OnPayListener onPayListener = new PayApi.OnPayListener() {
        @Override
        public void onPayOk() {
            Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPayFail(String msg) {
            Toast.makeText(MainActivity.this, "支付失败：" + msg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //如有qq、qq空间分享需要在调用页面的onActivityResult添加
        if(mShareApi != null) {
            mShareApi.onActivityResult(requestCode, resultCode, data);
        }
    }
}
