package com.fuj.hangcity.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by fuj
 */
public class ToastUtils {
    private static String oldMsg ; //之前显示的内容
    private static Toast toast = null ; //Toast对象
    private static long oneTime = 0 ; //第一次时间
    private static long twoTime = 0 ; //第二次时间

    /**
     * 显示Toast
     * @param context 上下文环境
     * @param message 消息
     */
    public static void showToast(Context context, String message){
        if(toast == null){
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show() ;
            oneTime = System.currentTimeMillis() ;
        } else {
            twoTime = System.currentTimeMillis() ;
            if(message.equals(oldMsg)){
                if(twoTime - oneTime > Toast.LENGTH_SHORT){
                    toast.show() ;
                }
            } else {
                oldMsg = message ;
                toast.setText(message) ;
                toast.show() ;
            }
        }
        oneTime = twoTime ;
    }
}
