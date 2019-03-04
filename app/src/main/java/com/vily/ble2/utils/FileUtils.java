package com.vily.ble2.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Scanner;

/**
 *  * description : 
 *  * Author : Vily
 *  * Date : 2019/3/4
 *  
 **/
public class FileUtils {

    private static final String TAG = "FileUtils";

    public static void write(int rssi) {

        int index = SharedPreferencesUtil.getint(UIUtil.getContext(), "index", 0);
        try {
        String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/aaa/";
        File folde=new File(path);
            Log.i(TAG, "write: -------1");
        if (!folde.exists() || !folde.isDirectory()) {
            Log.i(TAG, "write: --------2");
            folde.mkdirs();
        }
        File file=new File(path,"aa.csv");
        if(!file.exists()){
            file.createNewFile();
        }


            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            bw.write("mac"+  index + " : "+ rssi);
            bw.write(",");   // 列换行
//            bw.newLine();      // 行换行
            index++;
            SharedPreferencesUtil.saveint(UIUtil.getContext(),"index",index);
            bw.close();
        } catch (Exception e) {

            e.printStackTrace();
        }


    }
}
