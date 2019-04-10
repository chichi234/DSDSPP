package com.reb.bluetooth.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import com.reb.bluetooth.BtConfig;
import com.reb.bluetooth.scanner.ExtendedBluetoothDevice;
import com.reb.bluetooth.scanner.ScanLeCallback;
import com.reb.bluetooth.scanner.ScannerBase;
import com.reb.bluetooth.scanner.ble.API19Scanner;
import com.reb.bluetooth.scanner.ble.API21Scanner;
import com.reb.bluetooth.scanner.spp.SPPScanner;
import com.reb.bluetooth.util.BluetoothUtil;
import com.reb.bluetooth.util.DebugLog;

import java.util.Arrays;

/**
 * Created by Administrator on 2018/1/7 0007.
 */

public abstract class BaseScannerFragment extends Fragment implements ScanLeCallback {
    private static final int SCAN_PERMISSION_REQUEST_CODE = 1;

    protected View mRootView;
    private boolean mInitScaned = false;
    private ScannerBase mScanner;
    protected BleScanListener mBleScanStateListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewParent parent = mRootView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mRootView);
            }
        }
        initScanner();
        initBt();
        startScan();
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBtReceiver);
    }

    private void initScanner() {
        if (BtConfig.BT_PROFILE.equals("SPP")) {
            mScanner = new SPPScanner(getContext(), this);
        } else if (BtConfig.BT_PROFILE.equals("BLE")) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mScanner = new API19Scanner(getContext(), this);
            } else {
                mScanner = new API21Scanner(getContext(), this);
            }
        }
        mScanner.setTimeout(BtConfig.BT_SCAN_TIME);
    }

    private void initBt() {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mBtReceiver, filter);
    }

    private BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if (newState == BluetoothAdapter.STATE_ON) {
                    if (!mInitScaned) {
                        startScan();
                    }
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DebugLog.i(Arrays.toString(grantResults) + "=====mInitScaned:" + mInitScaned);
        if (requestCode == SCAN_PERMISSION_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    return;
                }
            }
            if (!mInitScaned) {
                startScan();
            }
        }
    }

    public void startScan() {
        if (!BluetoothUtil.getBluetoothAdapter(getContext()).isEnabled()) {
            BluetoothUtil.getBluetoothAdapter(getContext()).enable();
        } else if (BluetoothUtil.requirScanPermission(getActivity(), this, SCAN_PERMISSION_REQUEST_CODE)) {
            mInitScaned = true;
            mScanner.startScan();
        }
    }

    public boolean isScanning() {
        if (mScanner != null) {
            return mScanner.isScanning();
        } else {
            return false;
        }
    }

    public void stopScan() {
        mScanner.stopScan();
    }

    @Override
    public void onScanStop() {
        if (this.mBleScanStateListener != null) {
            this.mBleScanStateListener.onScanStop();
        }
    }

    @Override
    public void onScanStart() {
        if (this.mBleScanStateListener != null) {
            this.mBleScanStateListener.onScanStart();
        }
    }

    public void setBleScanStateListener(BleScanListener scanStateListener) {
        this.mBleScanStateListener = scanStateListener;
    }


    public interface BleScanListener {
        void onScanStart();

        void onScanStop();

        void onDeviceSelect(ExtendedBluetoothDevice device);
    }
}
