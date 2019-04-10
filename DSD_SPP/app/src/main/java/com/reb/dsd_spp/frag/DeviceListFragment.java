package com.reb.dsd_spp.frag;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.reb.bluetooth.scanner.ExtendedBluetoothDevice;
import com.reb.bluetooth.ui.BaseScannerFragment;
import com.reb.bluetooth.util.DebugLog;
import com.reb.dsd_spp.R;
import com.reb.dsd_spp.act.ConnectActivity;
import com.reb.dsd_spp.adapter.DeviceAdapter;
import com.reb.dsd_spp.adapter.MyDeviceAdapter;
import com.reb.dsd_spp.base.BaseActivity;
import com.reb.dsd_spp.db.DeviceBond;
import com.reb.dsd_spp.db.SQLiteDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-6 10:27
 * @package_name com.reb.ble.ui
 * @project_name Light
 * @history At 2018-9-6 10:27 created by Reb
 */
public class DeviceListFragment extends BaseScannerFragment implements MyDeviceAdapter.OnDeviceClickListener, DeviceAdapter.OnDevicePairRequest {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMDDHHmmss");
    private DeviceAdapter mAdapter;
    private MyDeviceAdapter mMyAdapter;
//    private String[] mMyDevices;
//    private SharedPreferences mShare;
    private Button mScanBtn;
    private TextView mNoDevice;
    private ExtendedBluetoothDevice mBondingDevice;
    private SQLiteDbHelper mSqLiteDbHelper;
    private List<DeviceBond> mMyDevices;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.frag_devices, null);
        initMyDevice();
        initView();
        registerReceiver();
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mReceiver);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getContext().registerReceiver(mReceiver, filter);
    }

    private void initView() {
        ListView myDeviceList = mRootView.findViewById(R.id.my_devices);
        ListView deviceList = mRootView.findViewById(R.id.available_devices);
        mScanBtn = mRootView.findViewById(R.id.scan);
        mAdapter = new DeviceAdapter(getContext());
        deviceList.setAdapter(mAdapter);
        mAdapter.setOnDevicePairRequestListener(this);
//        deviceList.setOnItemClickListener(mOnItemClickListener);
        mMyAdapter = new MyDeviceAdapter(getContext());
        myDeviceList.setAdapter(mMyAdapter);
        mMyAdapter.setOnDeviceClickListener(this);
        mScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanning()) {
                    stopScan();
                } else {
                    startScan();
                }
            }
        });
        mScanBtn.post(new Runnable() {
            @Override
            public void run() {
                mScanBtn.setText(isScanning() ? R.string.scanner_action_stop_scanning : R.string.scanner_action_scan);
                if (mMyAdapter.getCount() == 0) {
                    if (mNoDevice != null) {
//                        mNoDevice.setText("Scanning...");
                        mNoDevice.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mNoDevice = mRootView.findViewById(R.id.noDevice);
    }

    private void initMyDevice() {
//        mShare = getContext().getSharedPreferences("myDevices", Context.MODE_PRIVATE);
//        String myDevicesStr = mShare.getString("MY_DEVICES", "");
//        mMyDevices = myDevicesStr.split(";");
//        for (int i = 0; i < mMyDevices.length; i++) {
//            DebugLog.e("my_devices_" + i + ": " + mMyDevices[i]);
//        }

        mSqLiteDbHelper = new SQLiteDbHelper(getContext());
        mMyDevices = mSqLiteDbHelper.getDeviceBonds();
    }

    @Override
    public void startScan() {
        if (mAdapter != null) {
            mAdapter.clearDevices();
        }
        if (mMyAdapter != null) {
            mMyAdapter.clearDevices();
        }
        super.startScan();
    }

    @Override
    public void onScanStart() {
        super.onScanStart();
        DebugLog.i("onScanStart," + isScanning() + "..." + mScanBtn);
        if (mScanBtn != null) {
            mScanBtn.setText(isScanning() ? R.string.scanner_action_stop_scanning : R.string.scanner_action_scan);
        }
//        if (mNoDevice != null) {
//            mNoDevice.setText("Scanning...");
//            mNoDevice.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onScanStop() {
        super.onScanStop();
        if (mScanBtn != null) {
            mScanBtn.setText(isScanning() ? R.string.scanner_action_stop_scanning : R.string.scanner_action_scan);
        }
//        if (mMyAdapter != null && mNoDevice != null) {
//            if (mMyAdapter.getCount() > 0) {
//                mNoDevice.setVisibility(View.GONE);
//            } else {
//                mNoDevice.setText("There is no my device. \nplease pair and add to my device");
//                mNoDevice.setVisibility(View.VISIBLE);
//            }
//        }
    }

    @Override
    public void onScanResult(int callbackType, ExtendedBluetoothDevice device, int rssi, byte[] scanRecord) {
        if (mAdapter != null && mMyAdapter != null) {
            if (isMyDevice(device)) {
//                mMyAdapter.addOrUpdateDevice(device);
//                mNoDevice.setVisibility(View.GONE);
            } else {
                mAdapter.addOrUpdateDevice(device);
            }
        }
    }

//    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            stopScan();
//            if (DeviceListFragment.this.mBleScanStateListener != null) {
//                DeviceListFragment.this.mBleScanStateListener.onDeviceSelect((ExtendedBluetoothDevice) parent.getItemAtPosition(position));
//            }
//        }
//    };

    private boolean isMyDevice(ExtendedBluetoothDevice device) {
//        for (int i = 0; i < mMyDevices.length; i++) {
//            if (device.device.getAddress().equals(mMyDevices[i])) {
//                return true;
//            }
//        }
//        return false;
        for (int i = 0; i < mMyDevices.size(); i++) {
            if (mMyDevices.get(i).getMac().equals(device.device.getAddress())) {
                return true;
            }
        }
        return false;
    }

//    @Override
//    public void onMyDeviceClick(ExtendedBluetoothDevice device) {
//        stopScan();
//        Intent intent = new Intent(getActivity(), ConnectActivity.class);
//        intent.putExtra("name", device.name);
//        intent.putExtra("address", device.device.getAddress());
//        startActivity(intent);
//    }

    @Override
    public void onMyDeviceClick(DeviceBond device) {
        stopScan();
        Intent intent = new Intent(getActivity(), ConnectActivity.class);
        intent.putExtra("name", device.getName());
        intent.putExtra("address", device.getMac());
        startActivity(intent);
    }

    @Override
    public void onRequestPairBt(ExtendedBluetoothDevice device) {
        stopScan();
        if (device.isBonded) {
            DeviceBond deviceBond = new DeviceBond(device.device.getAddress(), device.name, sdf.format(new Date()), device.name, 1);
            mMyDevices.add(deviceBond);
            mMyAdapter.addOrUpdateDevice(deviceBond);
            mNoDevice.setVisibility(View.GONE);
            mSqLiteDbHelper.insertDevice(deviceBond);
        } else {
            mBondingDevice = null;
            boolean bondRet = device.device.createBond();
            DebugLog.i("start pair process, ret:" + bondRet);
            if (bondRet) {
                mBondingDevice = device;
                // show dialog
                if (getActivity() != null) {
                    ((BaseActivity) getActivity()).showLoadingDialog("正在绑定", false);
                }
            } else {
                Toast.makeText(getContext(), "绑定失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                DebugLog.i("bondState:" + bondState);
                if (mBondingDevice != null && device.getAddress().equals(mBondingDevice.device.getAddress())) {
                    if (bondState == BluetoothDevice.BOND_BONDED) {
                        if (getActivity() != null) {
                            ((BaseActivity) getActivity()).dismissLoadingDialog();
                        }
                        if (mAdapter != null) {
                            // update bondState
                            mAdapter.updateBondState(device.getAddress());
                        }
                    } else if (bondState == BluetoothDevice.BOND_NONE) {
                        if (getActivity() != null) {
                            ((BaseActivity) getActivity()).dismissLoadingDialog();
                        }
                        Toast.makeText(getContext(), "绑定失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
}
