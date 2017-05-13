package com.vendor.social.support.wxapi;

/**
 * 请求响应体
 * Created by ljfan on 16/9/2.
 */
class Response {

    public String data;

    public int requestCode;

    public int responseCode;

    public String errorMsg;

    public OnHttpListener httpListener;

    public boolean isResponseOk(){
        return responseCode == 200;
    }
}
