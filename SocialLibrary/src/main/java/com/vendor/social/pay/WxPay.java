package com.vendor.social.pay;

import android.app.Activity;
import android.util.Log;
import android.util.Xml;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.vendor.social.PayApi;
import com.vendor.social.Social;
import com.vendor.social.model.PayBaseContent;
import com.vendor.social.model.WxPayContent;
import com.vendor.social.pay.extra.MD5;
import com.vendor.social.pay.extra.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 微信支付
 * Created by Supreme on 15/7/13.
 */
public class WxPay extends PayApi {

    private IWXAPI msgApi;
    private PayReq req;
    private StringBuffer sbParams;

    public WxPay(Activity act) {
        super(act);

        req = new PayReq();
        sbParams = new StringBuffer();
    }

    @Override
    public void pay(final String payInfo){
        throw new IllegalStateException("error");
    }

    /**
     * 这一套后台已经帮你处理好金额之类的了
     * @param payInfo 支付sdk
     */
    @Override
    public void pay(PayBaseContent payInfo) {
        final WxPayContent content = (WxPayContent)payInfo;

        Social.setWeixinPay(content.appid, content.partnerid, content.prepayid);
        msgApi = WXAPIFactory.createWXAPI(mAct, Social.getWeixinId());
        msgApi.registerApp(Social.getWeixinId());

        new AsyncTaskEx<Void, Void, WxPayContent>() {

            @Override
            protected WxPayContent doInBackground(Void... params) {
                return content;
            }

            @Override
            protected void onPostExecute(WxPayContent result) {
                PayReq req = new PayReq();
                req.appId = result.appid;
                req.partnerId = result.partnerid;
                req.prepayId = result.prepayid;
                req.packageValue = result.packageValue;
                req.nonceStr = result.noncestr;
                req.timeStamp = result.timestamp;
                req.sign = result.sign;

                msgApi.registerApp(result.appid);
                msgApi.sendReq(req);
            }
        }.execute();
    }

    @Override
    public void pay(String subject, final String body, final String price) {
        msgApi = WXAPIFactory.createWXAPI(mAct, Social.getWeixinId());
        msgApi.registerApp(Social.getWeixinId());

        new AsyncTaskEx<Void, Void, Map<String,String>>() {

            @Override
            protected Map<String,String> doInBackground(Void... params) {

                String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
                String entity = genProductArgs(body, price);

                Log.e("orion",entity);

                byte[] buf = Util.httpPost(url, entity);

                assert buf != null;
                String content = new String(buf);
                Log.e("orion", content);

                return decodeXml(content);
            }

            @Override
            protected void onPostExecute(Map<String,String> result) {
                sbParams.append("prepay_id\n").append(result.get("prepay_id")).append("\n\n");

                req.appId = Social.getWeixinId();
                req.partnerId = Social.getWeixinMchId();
                req.prepayId = result.get("prepay_id");
                req.packageValue = "Sign=WXPay";
                req.nonceStr = genNonceStr();
                req.timeStamp = String.valueOf(genTimeStamp());

                List<NameValuePair> signParams = new LinkedList<>();
                signParams.add(new BasicNameValuePair("appid", req.appId));
                signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
                signParams.add(new BasicNameValuePair("package", req.packageValue));
                signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
                signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
                signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

                req.sign = sign(signParams, false);

                sbParams.append("sign\n").append(req.sign).append("\n\n");

                Log.e("orion", signParams.toString());

                msgApi.registerApp(Social.getWeixinId());
                msgApi.sendReq(req);
            }
        }.execute();
    }

    private String genProductArgs(String body, String money) {
        try {
            List<NameValuePair> packageParams = new LinkedList<>();
            packageParams.add(new BasicNameValuePair("appid", Social.getWeixinId()));
            packageParams.add(new BasicNameValuePair("body", body));
            packageParams.add(new BasicNameValuePair("mch_id", Social.getWeixinMchId()));
            packageParams.add(new BasicNameValuePair("nonce_str", genNonceStr()));
            packageParams.add(new BasicNameValuePair("notify_url", getNotifyUrl()));
            packageParams.add(new BasicNameValuePair("out_trade_no", getOutTradeNo()));
            packageParams.add(new BasicNameValuePair("spbill_create_ip","127.0.0.1"));
            String m = (int)(Float.valueOf(money) * 100) + "";  //微信金额是按照分来的
            packageParams.add(new BasicNameValuePair("total_fee", m));
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));

            String sign = sign(packageParams, true);
            packageParams.add(new BasicNameValuePair("sign", sign));


            String xmlstring =toXml(packageParams);

            return new String(xmlstring.getBytes(), "ISO8859-1");

        } catch (Exception e) {
            Log.i(getClass().getSimpleName(), "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    public Map<String,String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName=parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if(!"xml".equals(nodeName)){
                            //实例化student对象
                            xml.put(nodeName,parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion",e.toString());
        }
        return null;
    }

    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<").append(params.get(i).getName()).append(">");


            sb.append(params.get(i).getValue());
            sb.append("</").append(params.get(i).getName()).append(">");
        }
        sb.append("</xml>");

        Log.e("orion",sb.toString());
        return sb.toString();
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 生成签名
     * @param params 参数
     * @param isPackageSign sign type
     * @return 签名后的字符串
     */
    private String sign(List<NameValuePair> params, boolean isPackageSign) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Social.getWeixinPaySecret());

        if(!isPackageSign){
            sbParams.append("sign str\n").append(sb.toString()).append("\n\n");
        }

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.e("orion",packageSign);
        return packageSign;
    }
}
