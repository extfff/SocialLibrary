package com.vendor.social.support.wxapi;

/**
 * 网络请求基类biz
 * Created by ljfan on 16/04/19.
 */
class HttpBiz {

    public static final String WEIXIN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    public static final String WEIXIN_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";

    private HttpManager mHttpManager;

    public HttpBiz(){
        mHttpManager = new HttpManager();
    }

    /**
     * 获取open_id
     * @param appId
     * @param secret
     * @param code
     */
    public void doGet(int requestCode, String appId, String secret, String code, OnHttpListener l){
        mHttpManager.request(requestCode, String.format(WEIXIN_URL, appId, secret, code), l);
    }

    /**
     * 获取用户信息
     * @param access_token
     * @param openid
     */
    public void doGet(int requestCode, String access_token, String openid, OnHttpListener l){
        mHttpManager.request(requestCode, String.format(WEIXIN_USER_INFO_URL, access_token, openid), l);
    }
}
