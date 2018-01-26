package com.vendor.social.model;

/**
 * 登录用户实体
 * Created by ljfan on 16/4/19.
 */
public class User {

    public String openid;

    public String nickName;

    public String avatar;

    public User(String openid, String nickName, String avatar){
        this.openid = openid;
        this.nickName = nickName;
        this.avatar = avatar;
    }
}
