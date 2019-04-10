package com.reb.bluetooth.connect;

import com.reb.dsd_spp.IBtCallback;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2019-1-16 13:42
 * @package_name com.reb.bt.connect
 * @project_name DSD_SPP
 * @history At 2019-1-16 13:42 created by Reb
 */
public interface IBtConnectCallback {
    void onStateChange(ConnectState state);

    void onError(int code, String msg);

    void onReceive(byte[] data, int len);

    void onWriteSuccess(byte[] data, int len);

}
