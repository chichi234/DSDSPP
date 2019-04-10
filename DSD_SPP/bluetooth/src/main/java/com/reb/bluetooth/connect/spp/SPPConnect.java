package com.reb.bluetooth.connect.spp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.reb.bluetooth.connect.BaseConnect;
import com.reb.bluetooth.connect.ConnectState;
import com.reb.bluetooth.connect.IBluetoothConnect;
import com.reb.bluetooth.connect.IBtConnectCallback;
import com.reb.dsd_spp.IBtCallback;
import com.reb.dsd_spp.IBtConnectAidl;

import java.util.ArrayList;
import java.util.List;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2019-1-16 14:50
 * @package_name com.reb.bt.connect.spp
 * @project_name DSD_SPP
 * @history At 2019-1-16 14:50 created by Reb
 */
public class SPPConnect extends BaseConnect {

    private IBtConnectAidl mService;
    private static BaseConnect mInstance;
    private static Handler mHandler;

    public static BaseConnect getInstance() {
        if (mInstance == null) {
            mInstance = new SPPConnect(mContext);
        }
        return mInstance;
    }

    private SPPConnect(Application application) {
        mHandler = new Handler(application.getMainLooper());
        Intent intent = new Intent(mContext, BluetoothClientService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void connect(String mac) {
        try {
            if (mService != null) {
                mService.connect(mac);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            if (mService != null) {
                mService.disconnect();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ConnectState getConnectState() {
        try {
            if (mService != null) {
               int state = mService.getConnectState();
               return ConnectState.getStateByInt(state);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ConnectState.DISCONNECT;
    }

    @Override
    public void write(byte[] data) {
        try {
            if (mService != null) {
                mService.writeByte(data);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String data) {
        try {
            if (mService != null) {
                mService.write(data);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            if (mService != null) {
                mService.disconnect();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (mContext != null) {
            mContext.unbindService(mServiceConnection);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IBtConnectAidl.Stub.asInterface(service);
            try {
                mService.setCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private IBtCallback.Stub mCallback = new IBtCallback.Stub(){

        @Override
        public void onStateChange(final int state) throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (IBtConnectCallback callback: mCallbacks) {
                        callback.onStateChange(ConnectState.getStateByInt(state));
                    }
                }
            });
        }

        @Override
        public void onError(final int code, final String msg) throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (IBtConnectCallback callback: mCallbacks) {
                        callback.onError(code, msg);
                    }
                }
            });
        }

        @Override
        public void onReceive(final byte[] data, final int len) throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (IBtConnectCallback callback: mCallbacks) {
                        callback.onReceive(data, len);
                    }
                }
            });
        }

        @Override
        public void onWriteSuccess(final byte[] data, final int offset, final int len) throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (IBtConnectCallback callback: mCallbacks) {
                        callback.onWriteSuccess(data, len);
                    }
                }
            });
        }
    };
}
