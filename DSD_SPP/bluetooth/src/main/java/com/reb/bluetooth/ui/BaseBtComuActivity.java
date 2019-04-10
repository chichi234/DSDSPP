package com.reb.bluetooth.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.reb.bluetooth.BtConfig;
import com.reb.bluetooth.connect.ConnectState;
import com.reb.bluetooth.connect.IBluetoothConnect;
import com.reb.bluetooth.connect.IBtConnectCallback;
import com.reb.bluetooth.connect.spp.SPPConnect;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2019-1-16 18:27
 * @package_name com.reb.bluetooth.ui
 * @project_name DSD_SPP
 * @history At 2019-1-16 18:27 created by Reb
 */
public class BaseBtComuActivity extends Activity implements IBtConnectCallback{

    private IBluetoothConnect mIBluetooth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BtConfig.BT_PROFILE.equals("SPP")) {
            mIBluetooth = SPPConnect.getInstance();
        }
    }

    @Override
    public void onStateChange(ConnectState state) {

    }

    @Override
    public void onError(int code, String msg) {

    }

    @Override
    public void onReceive(byte[] data, int len) {

    }

    @Override
    public void onWriteSuccess(byte[] data, int len) {

    }
}
