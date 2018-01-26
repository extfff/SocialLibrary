package com.vendor.social.support.wxapi;

/**
 * 监听接口,监听异步任务是否完成
 * @author ljfan
 */
interface OnHttpListener{

    /**
     * 异步任务结束之后传回数据
     * @param response 网络请求响应内容
     */
    void onResponse(Response response);
}
