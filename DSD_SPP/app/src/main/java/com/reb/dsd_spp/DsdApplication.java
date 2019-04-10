package com.reb.dsd_spp;

import android.app.Application;

import com.reb.bluetooth.connect.BaseConnect;
import com.reb.bluetooth.connect.spp.SPPConnect;


/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class DsdApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BaseConnect.init(this);
        SPPConnect.getInstance();
    }
}
