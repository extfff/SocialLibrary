package com.vendor.social.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.vendor.social.R;
import com.vendor.social.ShareApi;
import com.vendor.social.Social;
import com.vendor.social.model.ShareType;
import com.vendor.social.utils.BitmapConvert;
import com.vendor.social.utils.BitmapLoader;

/**
 * 分享平台公共组件模块-微信分享
 * Created by ljfan on 16/4/19.
 */
public class WeixinShare extends ShareApi{

    private static final int THUMB_SIZE = 100;

    public WeixinShare(Activity act) {
        super(act);
        setShareType(ShareType.WEIXIN);
    }

    @Override
    public void doShare(){
        //获取bitmap
        new BitmapLoader().loadIconBitmap(mActivity, getShareContent(), new BitmapLoader.OnLoadImageListener() {
            @Override
            public void onResult(Bitmap bitmap) {
                IWXAPI api = WXAPIFactory.createWXAPI(mActivity, Social.getWeixinId(), true);
                api.registerApp(Social.getWeixinId());

                if(!api.isWXAppInstalled()) {
                    Toast.makeText(mActivity, R.string.social_fail_weixin_un_install, Toast.LENGTH_SHORT).show();
                    return;
                }

                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = getShareContent().getTargetUrl();
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = getShareContent().getTitle();//不能太长，否则微信会提示出错。不过没验证过具体能输入多长。
                msg.description = getShareContent().getText();

                if(bitmap != null) {
                    Bitmap thumb = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
                    msg.thumbData = BitmapConvert.bmpToByteArray(thumb, true);
                }

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
//                    req.openId = Social.getWeixinId();
                api.sendReq(req);
            }
        });
    }

    private String buildTransaction(final String type) {
        return type == null ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
