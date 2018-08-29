package com.fuj.hangcity.tools;

import android.os.Environment;

import java.io.File;

/**
 * Created by fuj
 */
public interface Constant {
    String BUNDLE_INFO_ITEM_TYPE = "type";
    String BUNDLE_INFO_EXTRA_URL = "url";
    String BUNDLE_INFO_DETAIL_EXTRA_IMG = "image";

    String EXTERNAL_STORAGE_DIR_SRC = "src";
    String EXTERNAL_BASE_PATH = Environment.getExternalStorageDirectory() + File.separator + "com.fuj.hangcity" + File.separator;

    String LOG_DIR = EXTERNAL_BASE_PATH + "log" + File.separator;
    String PLUGIN_DIR = EXTERNAL_BASE_PATH + "plugin" + File.separator;
    String PATCH_DIR = EXTERNAL_BASE_PATH + "patch" + File.separator;

    String TAG = "hangcity";

    String SEX_MALE = "male";
    String SEX_FEMALE = "female";
}
