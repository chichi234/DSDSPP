package com.reb.bluetooth.connect.spp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.reb.bluetooth.util.BluetoothUtil;
import com.reb.dsd_spp.IBtCallback;
import com.reb.dsd_spp.IBtConnectAidl;


public class BluetoothClientService extends Service {
    public BluetoothCommunThread communThread;
    public IBtCallback mIBtCallback;
    private BluetoothClientConnThread mThread;
    private int mState = 2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (communThread != null) {
            communThread.cancel();
            communThread = null;
        }
    }

    private IBtConnectAidl.Stub mBinder = new IBtConnectAidl.Stub() {
        @Override
        public void connect(String mac) throws RemoteException {
            if (mState == 2) {
                BluetoothAdapter adapter = BluetoothUtil.getBluetoothAdapter(BluetoothClientService.this);
                if (adapter != null) {
                    adapter.cancelDiscovery();
                    BluetoothDevice remoteDevice = adapter.getRemoteDevice(mac);
                    mThread = new BluetoothClientConnThread(callbackWrapper,remoteDevice);
                    mThread.start();
                }
            } else {
                mIBtCallback.onStateChange(mState);
            }
        }

        @Override
        public void disconnect() throws RemoteException {
            if (communThread != null) {
                communThread.cancel();
            }
        }

        @Override
        public int getConnectState() throws RemoteException {
            return mState;
        }

        @Override
        public void writeByte(byte[] data) throws RemoteException {
            if (communThread != null) {
                communThread.write(data);
            }
        }

        @Override
        public void writeByteLen(byte[] data, int offset, int len) throws RemoteException {
            if (communThread != null) {
                communThread.write(data, offset, len);
            }
        }

        @Override
        public void write(String data) throws RemoteException {
            if (communThread != null) {
                communThread.write(data.getBytes());
            }
        }

        @Override
        public void setCallback(IBtCallback callback) throws RemoteException {
            mIBtCallback = callback;
        }
    };

    private IBtCallback.Stub callbackWrapper = new IBtCallback.Stub() {
        @Override
        public void onStateChange(int state) throws RemoteException {
            mState = state;
            mIBtCallback.onStateChange(state);
            if (state == 1 && mThread != null) {
                communThread = new BluetoothCommunThread(this, mThread.getSocket());
                communThread.start();
                mThread = null;
            } else if (state == 2 && communThread != null) {
                communThread.cancel();
                communThread = null;
            }
        }

        @Override
        public void onError(int code, String msg) throws RemoteException {
            mIBtCallback.onError(code, msg);
        }

        @Override
        public void onReceive(byte[] data, int len) throws RemoteException {
            mIBtCallback.onReceive(data, len);
        }

        @Override
        public void onWriteSuccess(byte[] data, int offset, int len) throws RemoteException {
            mIBtCallback.onWriteSuccess(data, offset, len);
        }

    };
}
