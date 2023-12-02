package com.reb.bluetooth.util;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;

public class BluetoothUtil {

    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        BluetoothAdapter adapter;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //android4.3之前直接用BluetoothAdapter.getDefaultAdapter()就能得到BluetoothAdapter
            adapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bm.getAdapter();
        }
        return adapter;
    }

    public static void reopenBt(Context context) {
        final BluetoothAdapter adapter = getBluetoothAdapter(context);
        if (adapter.isEnabled()) {
            adapter.disable();
        }
        new Handler(context.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.enable();
            }
        }, 2000);
    }

    public static boolean requirScanPermission(Activity activity, int requestCode) {
        boolean isGranted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isGranted = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isGranted &= activity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
            isGranted &= activity.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
            if(!isGranted) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}, requestCode);
            }
        }else if (!isGranted) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
        }
        return isGranted;
    }

    public static boolean requirScanPermission(Activity activity, Fragment fragment, int requestCode) {
        boolean isGranted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isGranted = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isGranted &= activity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
            isGranted &= activity.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
            if(!isGranted) {
                fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}, requestCode);
            }
        }else if (!isGranted) {
            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
        }
        return isGranted;
    }
}
