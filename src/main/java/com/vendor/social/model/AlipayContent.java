package com.vendor.social.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 支付宝支付基类
 * Created by ljfan on 16/5/14.
 */
public class AlipayContent extends PayBaseContent {

    public String body; //奥康女鞋 冬季新品英伦风潮流及踝靴 蕾丝系带单鞋牛皮细跟女鞋子",

    public String subject; //奥康女鞋 冬季新品英伦风潮流及踝靴 蕾丝系带单鞋牛皮细..."

    public String sign_type; //RSA"

    public String notify_url; //http://t.aipaike.com:8000/payment/notify/async/20160514170000015T.jhtml"

    public String out_trade_no; //20160514170000015T"

    public String sign; //23b3454eb52697967f7523d4e508eac3"

    public String _input_charset; //utf-8"

    public String exter_invoke_ip; //121.40.245.191"

    public String it_b_pay; //14368m"

    public String extra_common_param; //aok"

    public String total_fee; //0.20"

    public String service; //mobile.securitypay.pay"

    public String paymethod; //directPay"

    public String partner; //2088021328757915"

    public String seller_id; //2088021328757915"

    public String payment_type; //1"

    public String return_url;

    @Override
    public String toString() {
        try {
            return "_input_charset=" + "\"" + _input_charset + "\"" + "&" +
                    "body=" + "\"" + body + "\"" + "&" +
                    "exter_invoke_ip=" + "\"" + exter_invoke_ip + "\"" + "&" +
                    "extra_common_param=" + "\"" + extra_common_param + "\"" + "&" +
                    "it_b_pay=" + "\"" + it_b_pay + "\"" + "&" +
                    "notify_url=" + "\"" + notify_url + "\"" + "&" +
                    "out_trade_no=" + "\"" + out_trade_no + "\"" + "&" +
                    "partner=" + "\"" + partner + "\"" + "&" +
                    "payment_type=" + "\"" + payment_type + "\"" + "&" +
                    "paymethod=" + "\"" + paymethod + "\"" + "&" +
                    "seller_id=" + "\"" + seller_id + "\"" + "&" +
                    "service=" + "\"" + service + "\"" + "&" +
                    "subject=" + "\"" + subject + "\"" + "&" +
                    "total_fee=" + "\"" + total_fee + "\"" + "&" +
                    "return_url=\"m.alipay.com\"" + "&" +
                    "sign=" + "\"" + URLEncoder.encode(sign, "UTF-8") + "\"" + "&" +
                    "sign_type=" + "\"" + sign_type + "\"";
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}