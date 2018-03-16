package com.vendor.social.pay;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.vendor.social.PayApi;
import com.vendor.social.R;
import com.vendor.social.Social;
import com.vendor.social.model.PayBaseContent;
import com.vendor.social.pay.extra.alipay.PayResult;
import com.vendor.social.pay.extra.alipay.SignUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 支付宝支付
 * Created by ljfan on 16/7/13.
 * modify by ljfan on 17/9/2.
 */
public class AliPay extends PayApi {

    public AliPay(Activity act) {
        super(act);
    }

    @Override
    public void pay(PayBaseContent payInfo) {
        pay(payInfo.toString());
    }

    @Override
    public void pay(String subject, String body, String price) {
        // 订单
        String orderInfo = createOrderInfo(subject, body, price);

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
        pay(payInfo);
    }

    /**
     * 服务端拼凑完成的字符串
     * @param payInfo 字符串
     */
    @Override
    public void pay(final String payInfo){
        new AsyncTaskEx<Void, Void, PayResult>(){

            @Override
            protected PayResult doInBackground(Void... params) {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mAct);
                PayResult payResult = null;

                try {
                    Map<String, String> result = alipay.payV2(payInfo, true);
                    payResult = new PayResult(result);
                }catch (Exception e){

                }

                return payResult;
            }

            @Override
            protected void onPostExecute(PayResult result) {
                // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
//                String resultInfo = result.getResult();  // 同步返回需要验证的信息
                String resultStatus = result.getResultStatus();

                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    callbackPayOk();
                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        callbackPayFail(resultStatus, mAct.getString(R.string.pay_fail_wait));
                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        callbackPayFail(resultStatus, mAct.getString(R.string.pay_fail));
                    }
                }
            }
        }.execute();
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public String getSDKVersion() {
        PayTask payTask = new PayTask(mAct);
        return payTask.getVersion();
    }

    /**
     * 创建订单信息
     * @param subject 标题
     * @param body 内容
     * @param price 金额
     * @return 订单信息
     */
    private String createOrderInfo(String subject, String body, String price) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + Social.getAlipayPartner() + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + Social.getAlipaySeller() + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + getNotifyUrl() + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    private String sign(String content) {
        return SignUtils.sign(content, Social.getAlipayRsaPrivate());
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
