package com.vily.ble2;

/**
 *  * description : 
 *  * Author : Vily
 *  * Date : 2018/12/17
 *  
 **/
public class BleBean {

    private String name;
    private String mac;
    private int rssi;

    public BleBean() {
    }

    public BleBean(String name, String mac, int rssi) {
        this.name = name;
        this.mac = mac;
        this.rssi = rssi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
