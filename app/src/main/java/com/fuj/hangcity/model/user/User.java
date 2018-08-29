package com.fuj.hangcity.model.user;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by fuj
 */
public class User extends BmobUser {
    private String signature;
    private BmobRelation favorite;
    private String sex;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public BmobRelation getFavorite() {
        return favorite;
    }

    public void setFavorite(BmobRelation favorite) {
        this.favorite = favorite;
    }


    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }


}
