package com.vendor.social.pay.extra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.vendor.social.Social;

public class AppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		IWXAPI api = WXAPIFactory.createWXAPI(context, Social.getWeixinId());
		api.registerApp(Social.getWeixinId()); //将该app注册到微信
	}
}
