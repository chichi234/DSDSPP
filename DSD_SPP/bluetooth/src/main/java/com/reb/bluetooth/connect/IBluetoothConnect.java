package com.reb.bluetooth.connect;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2019-1-16 11:56
 * @package_name com.reb.bt.connect
 * @project_name DSD_SPP
 * @history At 2019-1-16 11:56 created by Reb
 */
public interface IBluetoothConnect {

    void connect(String mac);

    void disconnect();

    ConnectState getConnectState();

    void write(byte[] data);

    void write(byte[] data, int offset, int len);

    void write(String data);

    void registerCallback(IBtConnectCallback callback);

    void unRegisterCallback(IBtConnectCallback callback);
}
