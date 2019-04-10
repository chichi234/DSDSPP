package com.reb.bluetooth.connect;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2019-1-16 17:40
 * @package_name com.reb.bluetooth.connect
 * @project_name DSD_SPP
 * @history At 2019-1-16 17:40 created by Reb
 */
public class BaseConnect implements IBluetoothConnect{

    protected static Application mContext;
    protected List<IBtConnectCallback> mCallbacks = new ArrayList<>();

    public static void init(Application application) {
        mContext = application;
    }

    @Override
    public void connect(String mac) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public ConnectState getConnectState() {
        return ConnectState.DISCONNECT;
    }

    @Override
    public void write(byte[] data) {

    }

    @Override
    public void write(byte[] data, int offset, int len) {

    }

    @Override
    public void write(String data) {

    }

    @Override
    public void registerCallback(IBtConnectCallback callback) {
        mCallbacks.add(callback);
    }

    @Override
    public void unRegisterCallback(IBtConnectCallback callback) {
        mCallbacks.remove(callback);
    }
}
