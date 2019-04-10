package com.reb.bluetooth.connect;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2019-1-16 17:05
 * @package_name com.reb.bt.connect
 * @project_name DSD_SPP
 * @history At 2019-1-16 17:05 created by Reb
 */
public enum  ConnectState {
    CONNECTING(0),
    CONNECTED(1),
    DISCONNECT(2),
    READY_RW(3);
    private int mCode;

    ConnectState(int code) {
        mCode = code;
    }

    public static ConnectState getStateByInt(int code) {
        switch (code) {
            case 0:
                return CONNECTING;
            case 1:
                return CONNECTED;
            case 3:
                return READY_RW;
            default:
                return DISCONNECT;
        }
    }
}
