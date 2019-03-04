package com.vily.ble2.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/17
 * Time:下午1:35
 */

public class SharedPreferencesUtil {

    private static SharedPreferences sp;

    private final static String SP_NAME = "config";

    /**
     * 保存boolean状态
     * @param key
     * @param value
     */
    public static void saveBoolean(Context context, String key, boolean value){
        if (sp==null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }


    /**
     * 获取boolean状态
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defValue){
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    /**
     * 保存String类型信息
     * @param key
     * @param value
     */
    public static void saveString(Context context, String key, String value){
        if (sp==null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }


    /**
     * 获取String类型信息
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String key, String defValue){
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }


    public static void saveint(Context context, String key, int value){
        if (sp==null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }


    /**
     * 获取String类型信息
     * @param key
     * @param defValue
     * @return
     */
    public static int getint(Context context, String key, int defValue){
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defValue);
    }

}
