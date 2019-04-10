package com.reb.bluetooth.scanner.spp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.reb.bluetooth.scanner.ExtendedBluetoothDevice;
import com.reb.bluetooth.scanner.ScanLeCallback;
import com.reb.bluetooth.scanner.ScannerBase;
import com.reb.bluetooth.scanner.ble.ScanFilter;
import com.reb.bluetooth.util.BluetoothUtil;
import com.reb.bluetooth.util.DebugLog;

import java.util.ArrayList;
import java.util.List;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2019-1-15 13:39
 * @package_name com.reb.bt.scanner.spp
 * @project_name DSD_SPP
 * @history At 2019-1-15 13:39 created by Reb
 */
public class SPPScanner extends ScannerBase {

    private boolean mIsListener = false;
    private List<BluetoothDevice> devices = new ArrayList<>();

    public SPPScanner(Context context, ScanLeCallback scanCallback) {
        super(context, scanCallback);
    }

    @Override
    public ScannerBase setScanFilter(ScanFilter scanFilter) {
        return this;
    }

    @Override
    public boolean isScanning() {
        BluetoothAdapter adapter = BluetoothUtil.getBluetoothAdapter(mContext);
        return adapter.isDiscovering();
    }

    @Override
    public void startScan() {
        devices.clear();
        BluetoothAdapter adapter = BluetoothUtil.getBluetoothAdapter(mContext);
        if (!mIsListener) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            mContext.registerReceiver(mReceiver, filter);
            mIsListener = true;
        }
        if (!adapter.isDiscovering()) {
            boolean ret = adapter.startDiscovery();
            DebugLog.d("BT discovery ret:" + ret);
        } else {
            mScanLeCallback.onScanStart();
        }
        stopScanDelay(mTimeout);
    }

    @Override
    public void stopScan() {
        BluetoothAdapter adapter = BluetoothUtil.getBluetoothAdapter(mContext);
        if (!adapter.isDiscovering()) {
            if (mIsListener) {
                mContext.unregisterReceiver(mReceiver);
                mIsListener = false;
            }
            mScanLeCallback.onScanStop();
            devices.clear();
        }
        adapter.cancelDiscovery();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            DebugLog.d("action:" + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothClass btClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                Short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short)-100);
                filter(device, btClass, name, rssi);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mIsListener) {
                    mContext.unregisterReceiver(mReceiver);
                    mIsListener = false;
                }
                mScanLeCallback.onScanStop();
                devices.clear();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mScanLeCallback.onScanStart();
            }
        }

        private void filter(BluetoothDevice device, BluetoothClass btClass, String name, Short rssi) {
            for (int i = 0; i < devices.size(); i++) {
                if (devices.get(i).getAddress().equals(device.getAddress())) {
                    return;
                }
            }
            devices.add(device);
            ExtendedBluetoothDevice extendedBluetoothDevice = new ExtendedBluetoothDevice(device, name, rssi);
            mScanLeCallback.onScanResult(ScanLeCallback.CALLBACK_TYPE_FIRST_MATCH, extendedBluetoothDevice, rssi, null);
        }
    };


}
