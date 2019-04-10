package com.reb.dsd_spp.db;

import android.content.ContentValues;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2019-3-6 21:01
 * @package_name com.reb.bluetooth.db
 * @project_name DSD_SPP
 * @history At 2019-3-6 21:01 created by Reb
 */
public class DeviceBond {
    private String mac;
    private String name;
    private String insert_time;
    private String display_name;
    private int type = 1;

    public DeviceBond() {
    }

    public DeviceBond(String mac, String name, String insert_time, String display_name, int type) {
        this.mac = mac;
        this.name = name;
        this.insert_time = insert_time;
        this.display_name = display_name;
        this.type = type;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }

    public ContentValues convertContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("mac_add", mac);
        cv.put("name", name);
        cv.put("insert_time", insert_time);
        cv.put("display_name", display_name);
        cv.put("type", type);
        return cv;
    }
}
