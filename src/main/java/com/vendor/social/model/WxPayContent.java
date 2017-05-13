package com.vendor.social.model;

/**
 * 微信支付
 * Created by ljfan on 16/5/17.
 */
public class WxPayContent extends PayBaseContent{

    public String appid;

    public String timestamp;

    public String noncestr;

    public String partnerid;

    public String prepayid;

    public String packageValue;

    public String sign;

    public String code_url;

    public String return_url;  //用于前端数据展现
}
