# SocialLibrary
欢迎大家使用。<br>
Library更新了jar包，截止2017-05-13，除微博最新的aar貌似要主工程才可以集成以外，其他的都是最新的。<br><br>
<B>主要提供内容：</B><br>
支付：<br><br>
微信支付，支付宝支付，银联如需请联系，暂不封装入<br><br>
登录:<br>
支付宝授权，微信授权，qq授权<br><br>
分享:<br>
qq分享，qq空间分析，微博分享，微信分享，微信朋友圈分享
<br><br><br><br>
<B>调用实例：</B><br><br>
//第三方登录<br>
AuthApi.doOauthVerify(this, AuthType.WEIXIN, new AuthApi.OnAuthListener() {
                    @Override
                    public void onComplete(int type, com.sicinfo.sippl.social.model.User user) {
                        dismissProgress();
                        DaoSharedPreferences.getInstance().setLoginOpenId(user.openid);
                        mUserBiz.loginWx(user.openid, user.nickName, user.avatar, onHttpListener);
                    }

                    @Override
                    public void onError(int type, String error) {
                        ToastUtil.show(LoginActivity.this, error);
                        dismissProgress();
                    }

                    @Override
                    public void onCancel(int type) {
                        dismissProgress();
                    }
                });
//支付宝支付<br>
public static void alipay(Activity act, String entity, PayApi.OnPayListener l){
        AliPay alipayApi = new AliPay(act);
        alipayApi.setOnPayListener(l);

        try {
            JSONObject jsonObject = new JSONObject(entity);
            alipayApi.pay(jsonObject.getString("pay_message"));  //服务队拼凑
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//微信支付<br>
public static void wxpay(final Activity act, String entity, PayApi.OnPayListener l){
        WxPay wxApi = new WxPay(act);
        wxApi.setOnPayListener(l);

        try {
            JSONObject jsonObject = new JSONObject(entity);
            jsonObject = jsonObject.getJSONObject("pay_message");

            WxPayContent req = new WxPayContent();
            req.appid = jsonObject.getString("appid");
            req.partnerid = jsonObject.getString("partnerid");
            req.prepayid = jsonObject.getString("prepayid");
            req.packageValue = jsonObject.getString("packagestr");
            req.noncestr = jsonObject.getString("noncestr");
            req.timestamp = jsonObject.getString("timestamp");
            req.sign = jsonObject.getString("sign");
            wxApi.pay(req);
        } catch (JSONException e) {
            ToastUtil.show(act, R.string.pay_error);
        }
    }

//分享<br>
mShareApi = new ShareApi(this);
            mShareApi.setOnShareListener(this);

            String title = App.getInstance().isLogined() ? App.getInstance().getUser().realname : getString(R.string.app_name);

            final ShareContent shareContent = new ShareContent.Builder()
                    .setAppName(getString(R.string.app_name))
                    .setTitle(getString(R.string.share_circle_title, title, mCircle.name))
                    .setText(mCircle.introduce)
                    .setTargetUrl(String.format(AppConfig.SHARE_CIRCLE_URL, mCircle.circle_id))
                    .setAppIcon(R.mipmap.ic_launcher)
                    .setIcon(mCircle.imgs.get(0).thumbnail)  //友圈必定有一张图
                    .build();

mShareApi = ShareApi.doShare(CircleDetailActivity.this, ShareType.QQ, shareContent, CircleDetailActivity.this);