package com.vily.ble2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 20;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 30;
    private static final int REQUEST_ENABLE_BT = 40;

    private EditText mEt_ip;
    private EditText mEt_value;
    private Button mBtn_send;
    private Button mBtn_send_loop;
    private Button mBtn_cancel;

    private ProgressBar mPb_progress;

    private boolean loop = false;

    private List<BleBean> mList = new ArrayList<>();
    private RecyclerView mRv_recycle;
    private BleAdapter mBleAdapter;
    private long mStartTime = 0;
    private int mTime = 100;
    private EditText mEt_time;

    private int mCount = 0;

    private boolean first = true;
    private String mTrim2;
    private String mValue;

    private boolean scan = true;
    private BluetoothClient mClient;
    private SearchRequest mRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClient = new BluetoothClient(MainActivity.this);
        initView();
        initPermission();
        checkBleDevice();
        initData();

    }

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //判断是否需要向用户解释为什么需要申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            }
            //请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }

        if (!isLocationEnable(MainActivity.this)) {
            Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
        }


    }

    public static final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsProvider) return true;
        return false;
    }

    private void initView() {
        mEt_ip = findViewById(R.id.et_ip);
        mEt_value = findViewById(R.id.et_value);

        mBtn_send = findViewById(R.id.btn_send);
        mBtn_send_loop = findViewById(R.id.btn_send_loop);
        mBtn_cancel = findViewById(R.id.btn_cancel);

        mPb_progress = findViewById(R.id.pb_progress);

        mRv_recycle = findViewById(R.id.rv_recycle);

        mEt_time = findViewById(R.id.et_time);

    }

    private void initData() {

        String ip = SharedPreferencesUtil.getString(MainActivity.this, "ip", "");
        String value = SharedPreferencesUtil.getString(MainActivity.this, "value", "");
        if (!TextUtils.isEmpty(ip)) {

            mEt_ip.setText(ip);
        }
        if (!TextUtils.isEmpty(value)) {
            mEt_value.setText(value);
        }

        mBtn_send.setOnClickListener(this);
        mBtn_send_loop.setOnClickListener(this);
        mBtn_cancel.setOnClickListener(this);

        // 初始化蓝牙扫描
        // 先扫BLE设备3次，每次3s
// 再扫经典蓝牙5s
// 再扫BLE设备2s



        mRv_recycle.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRv_recycle.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        mBleAdapter = new BleAdapter();
        mRv_recycle.setAdapter(mBleAdapter);


    }


    @Override
    public void onClick(View v) {

        String timeStr = mEt_time.getText().toString().trim();
        int time = Integer.parseInt(timeStr);
        mTime = time;
        initPermission();
        checkBleDevice();
        mTrim2 = mEt_ip.getText().toString().trim();
        mValue = mEt_value.getText().toString().trim();


        Log.i(TAG, "senBle: ------");
        if (TextUtils.isEmpty(mTrim2)) {

            Toast.makeText(getApplicationContext(), "ip不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mValue)) {

            Toast.makeText(getApplicationContext(), "value 不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferencesUtil.saveString(MainActivity.this, "ip", mTrim2);
        SharedPreferencesUtil.saveString(MainActivity.this, "value", mValue);

        mRequest = new SearchRequest.Builder()
                .searchBluetoothLeDevice(mTime, 1)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(mTime) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(mTime)      // 再扫BLE设备2s
                .build();

        switch (v.getId()) {

            case R.id.btn_send:   //  发送
                scan = true;
                loop = false;


                senBle();

                break;
            case R.id.btn_send_loop:   // 定时发送
                scan = true;
                loop = true;
                senBle();
                break;
            case R.id.btn_cancel:  // 停止定时发送
                loop = false;
                scan = false; // 停止扫描
                mList.clear();

                mClient.stopSearch();
                mBtn_send.setEnabled(true);
                mBtn_send_loop.setEnabled(true);

                break;
            default:
                break;
        }
    }

    private synchronized void senBle() {

        Log.i(TAG, "senBle: --------ip:" + mTrim2 + "-----value:" + mValue);

        mClient.search(mRequest, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                mPb_progress.setVisibility(View.VISIBLE);
                mList.clear();
                mBleAdapter.setNewData(null);
                mBtn_send.setEnabled(false);
                mBtn_send_loop.setEnabled(false);
                Log.i(TAG, "onScanStarted: -------" + mStartTime);

            }

            @Override
            public void onDeviceFounded(SearchResult bleDevice) {

                Log.i(TAG, "onDeviceFounded: ------------------bleDevice:"+bleDevice.rssi);
                // 扫描到一个符合扫描规则的BLE设备
                if (bleDevice != null) {
                    if (!TextUtils.isEmpty(bleDevice.device.getAddress())) {
                        BleBean bleBean = new BleBean(bleDevice.getName(), bleDevice.getAddress(), bleDevice.rssi);
                        mList.add(bleBean);
                        mBleAdapter.addData(bleBean);
                    }
                }


            }

            @Override
            public void onSearchStopped() {
                Log.i(TAG, "onSearchStopped: -------走这里了吗:");
                // 扫描结束，列出所有扫描到的符合扫描规则的BLE设备（主线程）
                mPb_progress.setVisibility(View.INVISIBLE);


                if (scan) {
                    // 去网络请求
                    if (mList != null && mList.size() > 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                request(mList);
                            }
                        }).start();
                    } else {
                        if (loop) {

                            SystemClock.sleep(500);
                            senBle();
                        } else {
                            mBtn_send.setEnabled(true);
                            mBtn_send_loop.setEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void onSearchCanceled() {
                Log.i(TAG, "onSearchCanceled: -------走这里了吗:");
                mPb_progress.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void request(List<BleBean> list) {
        String json = JSON.toJSONString(list);

        try {
            final String encode = URLEncoder.encode(json, "utf-8");

            Log.i(TAG, "request: -----------ecode:" + encode);


            OkHttpUtils
                    .get()
                    .url(mEt_ip.getText().toString().trim())
                    .addParams("data", encode)
                    .addParams("value", mEt_value.getText().toString().trim() + ":第" + (mCount++) + "次")
                    .build()
                    .execute(new Callback() {
                        @Override
                        public Object parseNetworkResponse(Response response, int id) throws Exception {
                            return null;

                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG, "onError: -------失败");
                                    Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                                    if (loop) {
                                        SystemClock.sleep(500);
                                        senBle();
                                    } else {
                                        mBtn_send.setEnabled(true);
                                        mBtn_send_loop.setEnabled(true);
                                    }

                                }
                            });

                        }

                        @Override
                        public void onResponse(Object response, int id) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG, "onResponse: ----------成功");
                                    Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                                    if (loop) {
                                        SystemClock.sleep(500);
                                        senBle();
                                    } else {
                                        mBtn_send.setEnabled(true);
                                        mBtn_send_loop.setEnabled(true);
                                    }
                                }
                            });

                        }
                    });


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void checkBleDevice() {
        //首先获取BluetoothManager
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        //获取BluetoothAdapter
        if (bluetoothManager != null) {
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter != null) {
                if (!mBluetoothAdapter.isEnabled()) {
                    //调用enable()方法直接打开蓝牙
                    if (!mBluetoothAdapter.enable()) {
                        Log.i("tag", "蓝牙打开失败");
                        mBluetoothAdapter.enable();
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
            } else {
                Log.i("tag", "同意申请");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (isLocationEnable(this)) {
                //定位已打开的处理

//                senBle();
            } else {
                //定位依然没有打开的处理
                Toast.makeText(getApplicationContext(), "去打开蓝牙和GPS定位", Toast.LENGTH_SHORT).show();
            }
        } else super.onActivityResult(requestCode, resultCode, data);

    }
}
