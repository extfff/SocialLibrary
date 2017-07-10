# SocialLibrary
欢迎大家使用。<br>
Library更新了jar包，截止2017-05-13，除微博最新的aar貌似要主工程才可以集成以外，其他的都是最新的。<br><br>
<B>主要提供内容：</B><br>
支付：
微信支付，支付宝支付，银联如需请联系，暂不封装入<br>
登录：
支付宝授权，微信授权，qq授权<br>
分享：
qq分享，qq空间分析，微博分享，微信分享，微信朋友圈分享
<br><br><br>
<B>调用步骤：</B><br><br>
**1、AndroidManifest.xml配置**

	<!-- 微博 -->
        <meta-data
            android:name="WEIBO_APP_KEY"
            android:value="${WEIBO_APP_KEY}"/>
        <meta-data
            android:name="WEIBO_REDIRECT_URL"
            android:value="${WEIBO_REDIRECT_URL}"/>
        <meta-data
            android:name="WEIBO_SCOPE"
            android:value="${WEIBO_SCOPE}"/>
        <!-- QQ -->
        <meta-data
            android:name="TENCENT_ID"
            android:value="${TENCENT_ID}"/>
        <!-- 微信 -->
        <meta-data
            android:name="WEIXIN_ID"
            android:value="${WEIXIN_ID}"/>
        <meta-data
            android:name="WEIXIN_SECRET"
            android:value="${WEIXIN_SECRET}"/>
        <!-- 微信支付 -->
        <meta-data
            android:name="WEIXIN_PAY_MCH_ID"
            android:value="${WEIXIN_PAY_MCH_ID}"/>
        <meta-data
            android:name="WEIXIN_PAY_SECRET"
            android:value="${WEIXIN_PAY_SECRET}"/>
        <!-- 支付宝支付 -->
        <meta-data
            android:name="ALIPAY_PARTNER"
            android:value="${ALIPAY_PARTNER}"/>
        <meta-data
            android:name="ALIPAY_SELLER"
            android:value="${ALIPAY_SELLER}"/>
        <meta-data
            android:name="ALIPAY_RSA_PRIVATE"
            android:value="${ALIPAY_RSA_PRIVATE}"/>

**2、build.gradle配置**

在android的节点底下添加（如不要多渠道配置，可直接第一步的<code>android:value</code>配置值即可）

	buildTypes {
        debug {
            signingConfig signingConfigs.myConfigs
            manifestPlaceholders = [  //debug环境
                    //微博分享
                    WEIBO_APP_KEY: "请输入申请的密钥",
                    WEIBO_REDIRECT_URL: "请输入申请的密钥",
                    WEIBO_SCOPE: "请输入申请的密钥",

                    //腾讯qq分享
                    TENCENT_ID: "请输入申请的密钥",

                    //微信分享
                    WEIXIN_ID: "请输入申请的密钥",
                    WEIXIN_SECRET: "请输入申请的密钥",

                    //微信支付
                    //商户号
                    WEIXIN_PAY_MCH_ID: "请输入申请的密钥",
                    //API密钥，在商户平台设置
                    WEIXIN_PAY_SECRET: "请输入申请的密钥",

                    //支付宝支付
                    //商户PID
                    ALIPAY_PARTNER: "请输入申请的密钥",
                    //商户收款账号
                    ALIPAY_SELLER: "请输入申请的密钥",
                    //商户私钥，pkcs8格式
                    ALIPAY_RSA_PRIVATE: "请输入申请的密钥"
            ]
        }

        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            manifestPlaceholders = [  //release环境
                    //...同debug
            ]
        }


**3、调用第三方登录-适用全部情况**

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

	
	//如有微信登录需要配置如下环境
	//在项目包名底下添加wxapi包,并新建WXEntryActivity extends WXBaseActivity，并在AndroidManifest.xml中声明此页面（具体写法可看Library的AndroidManifest.xml中的注释）

**4、调用支付宝支付**

	public static void alipay(Activity act, String entity, PayApi.OnPayListener l){
        AliPay alipayApi = new AliPay(act);
        alipayApi.setOnPayListener(l);

        try {
            JSONObject jsonObject = new JSONObject(entity);
            alipayApi.pay(jsonObject.getString("pay_message"));  //服务端拼凑 客户端拼凑的Library也有提供，不过不放出使用方法，毕竟已经过时，如需要请联系作者
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

**5、调用微信支付**

	public static void wxpay(final Activity act, String entity, PayApi.OnPayListener l){
        WxPay wxApi = new WxPay(act);
        wxApi.setOnPayListener(l);

        try {
            JSONObject jsonObject = new JSONObject(entity);  //服务端获取 客户端拼凑的Library也有提供，不过不放出使用方法，毕竟已经过时，如需要请联系作者
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

	
	//微信支付需要配置如下环境
	//在项目包名底下添加wxapi包,并新建WXPayEntryActivity extends WXPayBaseActivity，并在AndroidManifest.xml中声明此页面（具体写法可看Library的AndroidManifest.xml中的注释）

**6、调用第三方分享-适用全部情况**

	mShareApi = new ShareApi(this);
            mShareApi.setOnShareListener(this);

            String title = App.getInstance().isLogined() ? App.getInstance().getUser().realname : getString(R.string.app_name);

            final ShareContent shareContent = new ShareContent.Builder()
                    .setAppName(getString(R.string.app_name))
                    .setTitle(getString(R.string.share_circle_title, title, mCircle.name))
                    .setText(mCircle.introduce)
                    .setTargetUrl(String.format(AppConfig.SHARE_CIRCLE_URL, mCircle.circle_id))
                    .setAppIcon(R.mipmap.ic_launcher)
                    .setIcon(mCircle.imgs.get(0).thumbnail)
                    .build();

	mShareApi = ShareApi.doShare(CircleDetailActivity.this, ShareType.QQ, shareContent, CircleDetailActivity.this);

	
	//如有微信分享需要配置如下环境
	//在项目包名底下添加wxapi包,并新建WXEntryActivity extends WXBaseActivity，并在AndroidManifest.xml中声明此页面（具体写法可看Library的AndroidManifest.xml中的注释）

有问题联系：QQ群 254202293