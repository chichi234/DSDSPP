package com.reb.bluetooth;

import java.util.UUID;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2019-1-15 15:01
 * @package_name com.reb.bt
 * @project_name DSD_SPP
 * @history At 2019-1-15 15:01 created by Reb
 */
public class BtConfig {
    public static final String BT_PROFILE = "SPP";
    public static final long BT_SCAN_TIME = 16000;
    public static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
}
