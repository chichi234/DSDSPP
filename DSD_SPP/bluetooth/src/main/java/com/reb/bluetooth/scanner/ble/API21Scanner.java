package com.reb.bluetooth.scanner.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.ParcelUuid;

import com.reb.bluetooth.scanner.ExtendedBluetoothDevice;
import com.reb.bluetooth.scanner.ScanLeCallback;
import com.reb.bluetooth.scanner.ScannerBase;
import com.reb.bluetooth.util.BluetoothUtil;
import com.reb.bluetooth.util.DebugLog;

import java.util.ArrayList;
import java.util.List;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-5 13:59
 * @package_name com.reb.light.ble.scanner
 * @project_name Light
 * @history At 2018-9-5 13:59 created by Reb
 */
@SuppressLint("NewApi")
public class API21Scanner extends ScannerBase {

    private BluetoothLeScanner mScanner;
    private List<android.bluetooth.le.ScanFilter> mScanFilters = new ArrayList<>();
    private List<BluetoothDevice> mDevices = new ArrayList<>();

    public API21Scanner(Context context, ScanLeCallback scanLeCallback) {
        super(context, scanLeCallback);
    }

    @Override
    public ScannerBase setScanFilter(ScanFilter scanFilter) {
        mScanFilters.clear();
        android.bluetooth.le.ScanFilter scanOsFilter = new  android.bluetooth.le.ScanFilter.Builder()
                .setDeviceAddress(scanFilter.getDeviceAddress())
                .setDeviceName(scanFilter.getDeviceName())
                .setServiceUuid(new ParcelUuid(scanFilter.getServiceUuid()))
                .build();
        mScanFilters.add(scanOsFilter);
        return this;
    }

    @Override
    public void startScan() {
        if (this.mScanner == null) {
            this.mScanner = BluetoothUtil.getBluetoothAdapter(mContext).getBluetoothLeScanner();
            if (mScanner == null) {
                DebugLog.e("can't get scanner");
                return;
            }
        }
        this.mDevices.clear();
//        this.mScanner.startScan(mScanFilters, new ScanSettings.Builder().setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH).build(), mScanCallback);
        this.mScanner.startScan(mScanCallback);
        stopScanDelay(mTimeout);
        this.mScanLeCallback.onScanStart();
        this.mIsScanning = true;
    }

    @Override
    public void stopScan() {
        this.mScanner.stopScan(mScanCallback);
        this.mScanLeCallback.onScanStop();
        this.mIsScanning = false;
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String macAddress = device.getAddress();
            // 如果之前添加过，忽略
            for (int i = 0, loop = mDevices.size(); i < loop; i++) {
                if (macAddress.equals(mDevices.get(i).getAddress())) {
                    return;
                }
            }
            mDevices.add(device);
            byte[] scanRecord = result.getScanRecord().getBytes();
            int rssi = result.getRssi();
            ExtendedBluetoothDevice exDevice = ScanRecordParser.decodeDeviceAdvData(scanRecord, device, rssi, mScanFilters.get(0).getServiceUuid().getUuid());
            API21Scanner.this.mScanLeCallback.onScanResult(ScanLeCallback.CALLBACK_TYPE_FIRST_MATCH, exDevice, rssi, scanRecord);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
        }

        @Override
        public void onScanFailed(int errorCode) {
            DebugLog.i("errorCode:" + errorCode);
            if (errorCode == 0 || errorCode == 1) {
                API21Scanner.this.mIsScanning = true;
                API21Scanner.this.mScanLeCallback.onScanStart();
            } else {
                API21Scanner.this.mIsScanning = false;
                API21Scanner.this.mScanLeCallback.onScanStop();
            }
        }
    };
}
