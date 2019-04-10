package com.reb.bluetooth.scanner;


/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-5 15:45
 * @package_name com.reb.light.ble.scanner
 * @project_name Light
 * @history At 2018-9-5 15:45 created by Reb
 */
public interface ScanLeCallback {
    /**
     * Trigger a callback for every Bluetooth advertisement found that matches the filter criteria.
     * If no filter is active, all advertisement packets are reported.
     */
    public static final int CALLBACK_TYPE_ALL_MATCHES = 1;

    /**
     * A result callback is only triggered for the first advertisement packet received that matches
     * the filter criteria.
     */
    public static final int CALLBACK_TYPE_FIRST_MATCH = 2;

    /**
     * Receive a callback when advertisements are no longer received from a device that has been
     * previously reported by a first match callback.
     */
    public static final int CALLBACK_TYPE_MATCH_LOST = 4;

    void onScanResult(int callbackType, ExtendedBluetoothDevice device, int rssi, byte[] scanRecord);

    void onScanStart();

    void onScanStop();
}
