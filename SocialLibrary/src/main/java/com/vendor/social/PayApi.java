package com.vendor.social;

import android.app.Activity;

import com.vendor.social.model.PayBaseContent;
import com.vendor.social.model.PayType;
import com.vendor.social.pay.AliPay;
import com.vendor.social.pay.WxPay;

/**
 * 支付基类
 * Created by Supreme on 15/7/13.
 */
public abstract class PayApi {

    protected Activity mAct;

    private static OnPayListener mPayResultListener;  //只允许一个实例化回调

    private String mOutTradeNo;
    private String mNotifyUrl;

    public PayApi(Activity act){
        mAct = act;
    }

    /**
     * 支付
     * @param act act
     * @param payType {@link PayType}
     * @param subject 标题
     * @param body 内容
     * @param price 金额
     * @param l 回调
     * @return 支付实体
     */
    public static PayApi pay(Activity act, int payType, String subject, String body, String price, OnPayListener l){
        PayApi payApi;
        switch (payType){
            case PayType.WEIXIN:
                payApi = new WxPay(act);
                break;
            case PayType.ALIPAY:
                payApi = new AliPay(act);
                break;
            default:
                throw new IllegalStateException("error pay type");
        }

        payApi.setOnPayListener(l);
        payApi.pay(subject, body, price);

        return payApi;
    }


    /**
     * 调用支付sdk
     * @param payInfo 支付sdk
     */
    public abstract void pay(String payInfo);


    /**
     * 调用支付sdk
     * @param payInfo 支付sdk
     */
    public abstract void pay(PayBaseContent payInfo);

    /**
     * 调用支付sdk
     * @param subject 标题
     * @param body 内容
     * @param price 金额
     */
    public abstract void pay(String subject, String body, String price);

    /**
     * 设置商户订单号，该值在商户端应保持唯一
     */
    public void setOutTradeNo(String tradeNo) {
        this.mOutTradeNo = tradeNo;
    }

    /**
     * 获取商户订单号，该值在商户端应保持唯一
     * @return 订单号
     */
    public String getOutTradeNo() {
        return mOutTradeNo;
    }

    /**
     * 设置商户订单号，该值在商户端应保持唯一
     */
    public void setNotifyUrl(String notifyUrl) {
        this.mNotifyUrl = notifyUrl;
    }

    /**
     * 获取商户订单号，该值在商户端应保持唯一
     * @return 订单号
     */
    public String getNotifyUrl() {
        return mNotifyUrl;
    }

    /**
     * 设置支付回调
     * @param l
     */
    public void setOnPayListener(OnPayListener l){
        mPayResultListener = l;
    };

    /**
     * 返回支付成功
     */
    public static void callbackPayOk(){
        if(mPayResultListener != null){
            mPayResultListener.onPayOk();
        }
    }

    /**
     * 返回支付失败
     */
    public static void callbackPayFail(String code, String msg){
        if(mPayResultListener != null){
            mPayResultListener.onPayFail(code, msg);
        }
    }

    /**
     * 支付回调
     */
    public interface OnPayListener {

        /**
         * 支付回调-成功支付
         */
        void onPayOk();

        /**
         * 支付回调-支付失败
         */
        void onPayFail(String code, String msg);
    }
}
