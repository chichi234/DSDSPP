package com.reb.bluetooth.scanner.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.reb.bluetooth.scanner.ExtendedBluetoothDevice;
import com.reb.bluetooth.scanner.ScanLeCallback;
import com.reb.bluetooth.scanner.ScannerBase;
import com.reb.bluetooth.util.BluetoothUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-5 17:36
 * @package_name com.reb.light.ble.scanner
 * @project_name Light
 * @history At 2018-9-5 17:36 created by Reb
 */
public class API19Scanner extends ScannerBase {
    private ScanFilter mScanFilter;
    private List<BluetoothDevice> mDevices = new ArrayList<>();

    public API19Scanner(Context context, ScanLeCallback scanCallback) {
        super(context, scanCallback);
    }

    @Override
    public ScannerBase setScanFilter(ScanFilter scanFilter) {
        this.mScanFilter = scanFilter;
        return this;
    }

    @Override
    public void startScan() {
        this.mDevices.clear();
        boolean isStart = BluetoothUtil.getBluetoothAdapter(mContext).startLeScan(mScanCallback);
        if (isStart) {
            this.mScanLeCallback.onScanStart();
            this.mIsScanning = true;
            stopScanDelay(mTimeout);
        } else {
            mScanLeCallback.onScanStop();
            this.mIsScanning = false;
        }
    }

    @Override
    public void stopScan() {
        BluetoothUtil.getBluetoothAdapter(mContext).stopLeScan(mScanCallback);
        mScanLeCallback.onScanStop();
        this.mIsScanning = false;
    }

    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String macAddress = device.getAddress();
            // 如果之前添加过，忽略
            for (int i = 0, loop = mDevices.size(); i < loop; i++) {
                if (macAddress.equals(mDevices.get(i).getAddress())) {
                    return;
                }
            }
            mDevices.add(device);
            if (filter(device, scanRecord)) {
                ExtendedBluetoothDevice exDevice = ScanRecordParser.decodeDeviceAdvData(scanRecord, device, rssi, mScanFilter.getServiceUuid());
                mScanLeCallback.onScanResult(ScanLeCallback.CALLBACK_TYPE_FIRST_MATCH, exDevice, rssi, scanRecord);
            }
        }
    };

    private boolean filter(BluetoothDevice device, byte[] scanRecord) {
        if (mScanFilter != null) {
            // name
            if (mScanFilter.getDeviceName() != null) {
                String name = ScanRecordParser.decodeDeviceName(scanRecord);
                if (!mScanFilter.getDeviceName().equals(name)) {
                    return false;
                }
            }
            // service
            if (mScanFilter.getServiceUuid() != null) {
                if (!ScanRecordParser.decodeDeviceAdvData(scanRecord, mScanFilter.getServiceUuid())) {
                    return false;
                }
            }
            // macAddress
            if (mScanFilter.getDeviceAddress() != null && device.getAddress().equals(mScanFilter.getDeviceAddress())) {
                return false;
            }
        }
        return true;
    }
}
