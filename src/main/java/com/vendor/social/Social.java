package com.vendor.social;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * 社会化组件初始化配置
 * Created by ljfan on 16/4/19.
 */
public class Social {

    static Social sInstance;

    //微博分享
    private String mWeiboAppKey;
    private String mWeiboRedirectUrl;
    private String mWeiboScope;

    //腾讯qq分享
    private String mTencentId;

    //微信分享
    private String mWeixinId;
    private String mWeixinSecret;

    //微信支付
    //商户号
    public String mWeixinPayMchId;
    //API密钥，在商户平台设置
    public String mWeixinPaySecret;

    //支付宝支付
    //商户PID
    public String mAlipayPartner;
    //商户收款账号
    public String mAlipaySeller;
    //商户私钥，pkcs8格式
    public String mAlipayRsaPrivate;

    private Social(){

    }

    /**
     * 初始化配置 请在application类中调用
     * @param context context
     */
    public static void init(Context context){
        context = context.getApplicationContext();
        if(sInstance == null){
            sInstance = new Social();
        }

        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(appInfo != null && appInfo.metaData != null) {
            sInstance.mWeiboAppKey = appInfo.metaData.getString("WEIBO_APP_KEY", "");
            sInstance.mWeiboRedirectUrl = appInfo.metaData.getString("WEIBO_REDIRECT_URL", "");
            sInstance.mWeiboScope = appInfo.metaData.getString("WEIBO_SCOPE", "");
            sInstance.mTencentId = appInfo.metaData.getString("TENCENT_ID", "");
            if(TextUtils.isEmpty(sInstance.mTencentId)) {  //腾讯的可能会被误认为int类型
                sInstance.mTencentId = String.valueOf(appInfo.metaData.getInt("TENCENT_ID"));
            }
            sInstance.mWeixinId = appInfo.metaData.getString("WEIXIN_ID", "");
            sInstance.mWeixinSecret = appInfo.metaData.getString("WEIXIN_SECRET", "");
            sInstance.mWeixinPayMchId = appInfo.metaData.getString("WEIXIN_PAY_MCH_ID", "");
            sInstance.mWeixinPaySecret = appInfo.metaData.getString("WEIXIN_PAY_SECRET", "");
            sInstance.mAlipayPartner = appInfo.metaData.getString("ALIPAY_PARTNER", "");
            sInstance.mAlipaySeller = appInfo.metaData.getString("ALIPAY_SELLER", "");
            sInstance.mAlipayRsaPrivate = appInfo.metaData.getString("ALIPAY_RSA_PRIVATE", "");
        } else {
            throw new IllegalStateException("error load social config");
        }
    }

    /**
     * 获取app实例
     * @return app实例
     */
    public static Social getInstance() {
        if (sInstance == null) {
            sInstance = new Social();
        }

        return sInstance;
    }

    /**
     * 微博key
     * @return key
     */
    public static String getWeiboAppKey() {
        return getInstance().mWeiboAppKey;
    }

    /**
     * 微博回调
     * @return url
     */
    public static String getWeiboRedirectUrl() {
        return getInstance().mWeiboRedirectUrl;
    }

    /**
     * 微博 Scope
     * @return Scope
     */
    public static String getWeiboScope() {
        return getInstance().mWeiboScope;
    }

    public static void setTencent(String apiId) {
        getInstance().mTencentId = apiId;
    }

    /**
     * 腾讯 id
     * @return id
     */
    public static String getTencentId() {
        return getInstance().mTencentId;
    }

    /**
     * 微信 id
     * @return id
     */
    public static String getWeixinId() {
        return getInstance().mWeixinId;
    }

    /**
     * 微信 Secret
     * @return key
     */
    public static String getWeixinSecret() {
        return getInstance().mWeixinSecret;
    }

    /**
     * 设置微信支付
     * @param weixinId 微信appid
     * @param mchId 商户号
     * @param secret apikey
     */
    public static void setWeixinPay(String weixinId, String mchId, String secret) {
        getInstance().mWeixinId = weixinId;
        getInstance().mWeixinPayMchId = mchId;
        getInstance().mWeixinPaySecret = secret;
    }

    /**
     * 商户号
     * @return mchId
     */
    public static String getWeixinMchId() {
        return getInstance().mWeixinPayMchId;
    }

    /**
     * apikey
     * @return apikey
     */
    public static String getWeixinPaySecret() {
        return getInstance().mWeixinPaySecret;
    }

    /**
     * 商户PID
     * @return partner
     */
    public static String getAlipayPartner() {
        return getInstance().mAlipayPartner;
    }

    /**
     * 商户收款账号
     * @return seller
     */
    public static String getAlipaySeller() {
        return getInstance().mAlipaySeller;
    }

    /**
     * 商户私钥，pkcs8格式
     * @return rsaPrivate
     */
    public static String getAlipayRsaPrivate() {
        return getInstance().mAlipayRsaPrivate;
    }
}
