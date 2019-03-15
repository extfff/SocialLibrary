package com.vendor.social.support.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.vendor.social.PayApi;
import com.vendor.social.Social;

/**
 * 微信支付  继承此类  并类名为WXPayEntryActivity
 */
public class WXPayBaseEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "WXPayBaseEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = WXAPIFactory.createWXAPI(this, Social.getWeixinId());

        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		Log.e(TAG, "Wx onReq");
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.e(TAG, "Wx onResp");

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			//支付成功 通知Wxpay回调
			if(resp.errCode == BaseResp.ErrCode.ERR_OK){
				PayApi.callbackPayOk();
			}else{
				PayApi.callbackPayFail(String.valueOf(resp.errCode), resp.errStr);
			}
		}

        finish();
	}
}