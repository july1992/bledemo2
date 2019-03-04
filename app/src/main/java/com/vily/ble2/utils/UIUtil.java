package com.vily.ble2.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.vily.ble2.MyApplication;


public class UIUtil {

    /**
     * dip2px
     */
    public static int dip2px(int dip) {
        float density = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    /**
     * px2dip
     */
    public static int px2dip(int px) {
        float density = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }

    /**
     * px转换为sp
     */
    public static int px2sp(float pxValue) {
        float fontScale = MyApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * sp转换为px
     */
    public static int sp2px(float spValue) {
        float fontScale = MyApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取资源
     */
    public static String getString(int stringId) {
        return MyApplication.getContext().getResources().getString(stringId);
    }

    public static Drawable getDrawable(int did) {
        return MyApplication.getContext().getResources().getDrawable(did);
    }

    public static int getColor(int cid) {
        return MyApplication.getContext().getResources().getColor(cid);
    }

    public static Context getContext() {
        return MyApplication.getContext();
    }


    public static void hideKeyboard(Activity context, EditText editText) {
        try {

            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive(editText)) {
                editText.requestFocus();
                inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runInMainThread(Runnable runnable){
        if(android.os.Process.myTid() == getMainThreadId()){
            runnable.run();
        }else{
            getHandler().post(runnable);
        }
    }
    public static int getMainThreadId(){
        return MyApplication.getMainThreadId();
    }
    public static Handler getHandler(){
        return MyApplication.getHandler();
    }

}
