package com.vendor.social;

/**
 * 社会化组件初始化配置
 * Created by ljfan on 16/4/19.
 */
public class SocialConfig {

    static SocialConfig sInstance;

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
    public String mPartner;
    //商户收款账号
    public String mSeller;
    //商户私钥，pkcs8格式
    public String mRsaPrivate;

    /**
     * 获取app实例
     * @return app实例
     */
    public static SocialConfig getInstance() {
        if (sInstance == null) {
            sInstance = new SocialConfig();
        }

        return sInstance;
    }

    public static void setSinaWeibo(String appKey, String redirectUrl, String scope) {
        getInstance().mWeiboAppKey = appKey;
        getInstance().mWeiboRedirectUrl = redirectUrl;
        getInstance().mWeiboScope = scope;
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
     * 设置微信分享
     * @param id id
     * @param secret secret
     */
    public static void setWeixin(String id, String secret) {
        getInstance().mWeixinId = id;
        getInstance().mWeixinSecret = secret;
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
     * 设置微信支付
     * @param partner 商户PID
     * @param seller 商户收款账号
     * @param rsaPrivate 商户私钥，pkcs8格式
     */
    public static void setAliPay(String partner, String seller, String rsaPrivate) {
        getInstance().mPartner = partner;
        getInstance().mSeller = seller;
        getInstance().mRsaPrivate = rsaPrivate;
    }

    /**
     * 商户PID
     * @return partner
     */
    public static String getAlipayPartner() {
        return getInstance().mPartner;
    }

    /**
     * 商户收款账号
     * @return seller
     */
    public static String getAlipaySeller() {
        return getInstance().mSeller;
    }

    /**
     * 商户私钥，pkcs8格式
     * @return rsaPrivate
     */
    public static String getAlipayRsaPrivate() {
        return getInstance().mRsaPrivate;
    }
}
