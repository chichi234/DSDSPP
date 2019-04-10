package com.reb.dsd_spp.act;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.reb.bluetooth.connect.ConnectState;
import com.reb.bluetooth.connect.IBluetoothConnect;
import com.reb.bluetooth.connect.IBtConnectCallback;
import com.reb.bluetooth.connect.spp.SPPConnect;
import com.reb.bluetooth.util.DebugLog;
import com.reb.dsd_spp.R;
import com.reb.dsd_spp.base.BaseCommunicateFragment;
import com.reb.dsd_spp.base.BaseFragmentActivity;
import com.reb.dsd_spp.communicate.BeaconFragment;
import com.reb.dsd_spp.communicate.BeaconStartFragment;
import com.reb.dsd_spp.communicate.RelayFragment;
import com.reb.dsd_spp.communicate.SendRecFragment;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-1-11 16:21
 * @package_name com.reb.dsd_ble.ui.act
 * @project_name DSD_BLE
 * @history At 2018-1-11 16:21 created by Reb
 */

public class ConnectActivity extends BaseFragmentActivity implements BeaconStartFragment.AouthListener, BeaconFragment.SetDSDStateListener, IBtConnectCallback {
    private static final int MSG_READ_RSSI = 0x10001;
    private static final int MSG_READ_RSSI_RESULT = 0x10002;
    private static final int MSG_DEVICE_CONNECTED = 0x10003;
    private static final int MSG_DEVICE_DISCONNECTED = 0x10004;
    private static final int MSG_LINK_LOSS = 0x10005;
    private static final int MSG_WRITE_SUCCESS = 0x10006;
    private static final int MSG_RECEIVE_DATA = 0x10007;
    private static final int MSG_CONNECT_TIME_OUT = 0x10008;
    private static final int MSG_HIDE_ALERT = 0x10009;

    private IBluetoothConnect mIBluetooth;

    private RelayFragment mRelayFragment;
    private SendRecFragment mSendRecFragment;
    private BeaconFragment mBeaconFragment;
    private BeaconStartFragment mBeaconStartFragment;

    private ViewGroup mAlertLayout;
    private TextView mAlertText;
    private ProgressBar mWaitView;
    private TextView mDeviceInfoView;
    private TextView mConnectBtn;
    private RadioGroup mTabGroup;

    private String mDeviceName;
    private String mDeviceAddress;
    //    private int mRssi;
    private boolean mIsFinish = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECT_TIME_OUT:
                    mConnectBtn.setText(R.string.conn);
                    mConnectBtn.setEnabled(true);
                    showAlert(R.string.connect_failed, false);
                    sendEmptyMessageDelayed(MSG_HIDE_ALERT, 3000);
                    break;
                case MSG_HIDE_ALERT:
                    mAlertLayout.setVisibility(View.GONE);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        initData();
        initView();
        initFragment(savedInstanceState);
        setData();
    }

    private void initFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mRelayFragment = new RelayFragment();
            mSendRecFragment = new SendRecFragment();
            mBeaconFragment = new BeaconFragment();
            mBeaconFragment.setDsdStateListener(this);
            mBeaconStartFragment = new BeaconStartFragment();
            mBeaconStartFragment.setAouthListener(this);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            mRelayFragment = (RelayFragment) fm.findFragmentByTag(RelayFragment.class.getSimpleName());
            mSendRecFragment = (SendRecFragment) fm.findFragmentByTag(SendRecFragment.class.getSimpleName());
            String mCurrentTag = savedInstanceState.getString("mCurrentFragTag", null);
            if (RelayFragment.class.getSimpleName().equals(mCurrentTag)) {
                mCurrentFrag = mRelayFragment;
            } else if (SendRecFragment.class.getSimpleName().equals(mCurrentTag)) {
                mCurrentFrag = mSendRecFragment;
            } else if (BeaconFragment.class.getSimpleName().equals(mCurrentTag)) {
                mCurrentFrag = mBeaconFragment;
            }
        }
        changeFragment(mSendRecFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsFinish = true;
        mIBluetooth.unRegisterCallback(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void setData() {
        mIBluetooth = SPPConnect.getInstance();
        mIBluetooth.registerCallback(this);
        mDeviceInfoView.setText(mDeviceName/* + "(" + mRssi + "dBm)"*/);
        connect();
    }

    private void connect() {
        mConnectBtn.setText(R.string.connecting);
        mConnectBtn.setEnabled(false);
        showAlert(R.string.connecting_alert, true);
        mIBluetooth.connect(mDeviceAddress);
        mHandler.sendEmptyMessageDelayed(MSG_CONNECT_TIME_OUT, 6 * 1000);
    }

    private void initView() {
        mAlertLayout = findViewById(R.id.alert_layout);
        mAlertText = findViewById(R.id.alert_text);
        mWaitView = findViewById(R.id.alert_progress);
        mDeviceInfoView = findViewById(R.id.act_connect_device_info);
        mConnectBtn = findViewById(R.id.connect_btn);
        mTabGroup = findViewById(R.id.data_send_tab);
        mTabGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                DebugLog.i("checkId:" + checkedId + "," + R.id.beacon + ",====" + (mCurrentFrag == mBeaconFragment));
                BaseCommunicateFragment target = null;
                switch (checkedId) {
                    case R.id.relay:
                        target = mRelayFragment;
                        break;
                    case R.id.log:
                        target = mSendRecFragment;
                        break;
                    case R.id.beacon:
                        target = mBeaconStartFragment;
                        break;
                }
                if (mCurrentFrag != null && mCurrentFrag == mBeaconFragment) {
                    if (checkedId != R.id.beacon) {
                        showAlertDialog(target);
                    }
                } else {
                    changeFragment(target);
                }
            }
        });
        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectState connectState = mIBluetooth.getConnectState();
                DebugLog.i("connectState:" + connectState);
                if (connectState == ConnectState.DISCONNECT) {
                    mIBluetooth.connect(mDeviceAddress);
                } else if (connectState == ConnectState.READY_RW) {
                    mIBluetooth.disconnect();
                }
            }

        });

    }

    private void initData() {
        Intent intent = getIntent();
        mDeviceName = intent.getStringExtra("name");
        mDeviceAddress = intent.getStringExtra("address");
//        mRssi = intent.getIntExtra("rssi", -100);
        if (TextUtils.isEmpty(mDeviceName)) {
            mDeviceName = getString(R.string.unknown);
        }
    }

    private void showAlert(int stringResId, boolean showBar) {
        mHandler.removeMessages(MSG_HIDE_ALERT);
        mAlertLayout.setVisibility(View.VISIBLE);
        mAlertText.setText(stringResId);
        if (showBar) {
            mWaitView.setVisibility(View.VISIBLE);
        } else {
            mWaitView.setVisibility(View.GONE);
        }
    }

    public void close(View view) {
        finish();
    }

    @Override
    public void onAouthSuccess() {
        changeFragment(mBeaconFragment);
    }

    @Override
    public void onExitSetState() {
        if (mExitTargetFragment != null) {
            changeFragment(mExitTargetFragment);
            mExitTargetFragment = null;
        } else {
            changeFragment(mBeaconStartFragment);
        }
    }

    @Override
    public void onExitSetNoResponse() {
        if (mExitTargetFragment != null) {
            RadioButton radioButton = findViewById(R.id.beacon);
            radioButton.setChecked(true);
            mExitTargetFragment = null;
        }
    }

    private void showAlertDialog(final BaseCommunicateFragment targetFragment) {
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setCancelable(true)
                .setMessage(R.string.exit_beacon_alert)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RadioButton radioButton = findViewById(R.id.beacon);
                        radioButton.setChecked(true);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        RadioButton radioButton = findViewById(R.id.beacon);
                        radioButton.setChecked(true);
                    }
                })
                .setNeutralButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mExitTargetFragment = targetFragment;
                        mBeaconFragment.sendFinish();
                        changeFragment(mExitTargetFragment);
                    }
                })
                .create().show();
    }

    private BaseCommunicateFragment mExitTargetFragment;

    @Override
    public void onStateChange(ConnectState state) {
        DebugLog.i("connectState:" + state);
        switch (state) {
            case READY_RW:
                mConnectBtn.setText(R.string.disconn);
                mConnectBtn.setEnabled(true);
                showAlert(R.string.connect_alert, false);
                mHandler.removeMessages(MSG_CONNECT_TIME_OUT);
                mHandler.removeMessages(MSG_HIDE_ALERT);
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_ALERT, 3 * 1000);
                if (mCurrentFrag != null) {
                    ((BaseCommunicateFragment) mCurrentFrag).onDeviceConnect();
                }
//                    removeMessages(MSG_READ_RSSI);
//                    sendEmptyMessage(MSG_READ_RSSI);
                break;
            case DISCONNECT:
//                removeMessages(MSG_READ_RSSI);
//                mDeviceInfoView.setText(mDeviceName + "(-- dBm)");
                mConnectBtn.setText(R.string.conn);
                if (mCurrentFrag != null) {
                    ((BaseCommunicateFragment) mCurrentFrag).onDeviceDisConnect();
                    if (mCurrentFrag == mBeaconFragment) {
                        changeFragment(mBeaconStartFragment);
                    }
                }
                showAlert(R.string.disconnect_alert, false);
                mHandler.removeMessages(MSG_HIDE_ALERT);
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_ALERT, 3 * 1000);
                break;
            case CONNECTING:
                mConnectBtn.setText(R.string.connecting);
                mConnectBtn.setEnabled(false);
                showAlert(R.string.connecting_alert, true);
                mHandler.removeMessages(MSG_CONNECT_TIME_OUT);
                mHandler.sendEmptyMessageDelayed(MSG_CONNECT_TIME_OUT, 6 * 1000);
                break;
            case CONNECTED:
//                mConnectBtn.setText(R.string.disconn);
//                mConnectBtn.setEnabled(true);
//                showAlert(R.string.connect_alert, false);
//                mHandler.sendEmptyMessageDelayed(MSG_HIDE_ALERT, 3 * 1000);
                break;
        }
    }

    @Override
    public void onError(int code, String msg) {

    }

    @Override
    public void onReceive(byte[] data, int len) {
        if (mCurrentFrag != null) {
            ((BaseCommunicateFragment) mCurrentFrag).receive(data, len);
        }
    }

    @Override
    public void onWriteSuccess(byte[] data, int len) {
        if (mCurrentFrag != null) {
            ((BaseCommunicateFragment) mCurrentFrag).onWriteSuccess(data, true);
        }
    }
}
