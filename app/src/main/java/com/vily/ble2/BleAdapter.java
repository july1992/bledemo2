package com.vily.ble2;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 *  * description : 
 *  * Author : Vily
 *  * Date : 2018/12/17
 *  
 **/
public class BleAdapter extends BaseQuickAdapter<BleBean,BaseViewHolder> {

    public BleAdapter() {
        this(R.layout.item_adapter);
    }

    public BleAdapter(int layoutResId) {
        super(R.layout.item_adapter);
    }

    public BleAdapter(int layoutResId, @Nullable List<BleBean> data) {
        super(R.layout.item_adapter, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BleBean item) {
        TextView tv_name = helper.getView(R.id.tv_name);
        TextView tv_mac = helper.getView(R.id.tv_mac);
        TextView tv_riis = helper.getView(R.id.tv_riis);

        if(item!=null && !TextUtils.isEmpty(item.getName())){

            tv_name.setText(item.getName());

        }
        if(item!=null && !TextUtils.isEmpty(item.getMac())){

            tv_mac.setText(item.getMac());

        }
        if(item!=null ){

            tv_riis.setText(item.getRssi()+"");

        }

    }
}
