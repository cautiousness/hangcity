package com.fuj.hangcity.model.user;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by fuj
 */
public class UserSettings extends BmobObject {
    public static final String USER_ID = "userId";
    public static final String AVATAR = "avatar";

    private String userId;
    private BmobFile avatar;

    public String getUserId() {
        return userId;
    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "UserSettings{" + "， " + getObjectId() +
                "userId='" + userId + '\'' +
                ", avatar=" + avatar.getFilename() + ", " + avatar.getFileUrl() + ", " + avatar.getUrl() +
                '}';
    }
}
