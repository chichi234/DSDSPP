// IBtConnectAidl.aidl
package com.reb.dsd_spp;

// Declare any non-default types here with import statements
import com.reb.dsd_spp.IBtCallback;

interface IBtConnectAidl {

    void connect(String mac);

    void disconnect();

    int getConnectState();

    void writeByte(in byte[] data);

    void writeByteLen(in byte[] data, int offset, int len);

    void write(String data);

    void setCallback(IBtCallback callback);
}
