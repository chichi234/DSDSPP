// IBtCallback.aidl
package com.reb.dsd_spp;

// Declare any non-default types here with import statements

interface IBtCallback {

    void onStateChange(int state);

    void onError(int code, String msg);

    void onReceive(in byte[] data,  int len);

    void onWriteSuccess(in byte[] data, int offset, int len);
}
