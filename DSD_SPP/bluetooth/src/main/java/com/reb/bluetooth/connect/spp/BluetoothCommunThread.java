package com.reb.bluetooth.connect.spp;

import android.bluetooth.BluetoothSocket;
import android.os.RemoteException;

import com.reb.bluetooth.connect.ErrorCode;
import com.reb.bluetooth.connect.IBtConnectCallback;
import com.reb.bluetooth.util.DebugLog;
import com.reb.bluetooth.util.HexStringConver;
import com.reb.dsd_spp.IBtCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 蓝牙通讯线程
 */
public class BluetoothCommunThread extends Thread {

    private IBtCallback mCallback;
    private BluetoothSocket socket;
    private InputStream mmInStream;        //对象输入流
    private OutputStream mmOutStream;    //对象输出流
    public volatile boolean isRun = true;    //运行标志位

    /**
     * 构造函数
     *
     * @param callback 用于接收消息
     * @param socket
     */
    public BluetoothCommunThread(IBtCallback callback, BluetoothSocket socket) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.mCallback = callback;
        this.socket = socket;
        try {
            tmpOut = socket.getOutputStream();
            tmpIn = socket.getInputStream();
        } catch (Exception e) {
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            //发送连接失败消息
            try {
                mCallback.onError(ErrorCode.ERROR_CODE_OPEN_RW, e.getMessage());
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {

        // bytes returned from read()
        try {
            mCallback.onStateChange(3);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                byte[] buffer = new byte[1024];  // buffer store for the stream
                int bytes;
                bytes = mmInStream.read(buffer);
                DebugLog.i("Rece:" + bytes + HexStringConver.bytes2HexStr(buffer, 0, bytes));
                if (bytes > 0) {
                    try {
                        mCallback.onReceive(buffer, bytes);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                break;
            }
        }

        //关闭流
        if (mmInStream != null) {
            try {
                mmInStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mmOutStream != null) {
            try {
                mmOutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            mCallback.onStateChange(2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        if (bytes != null) {
            write(bytes, 0, bytes.length);
        } else {
            DebugLog.w("WRITE DATA IS NULL.");
        }
    }


    public void write(byte[] bytes, int off, int len) {
        try {
            mmOutStream.write(bytes, off, len);
            DebugLog.i("WRITE:" + len + HexStringConver.bytes2HexStr(bytes, 0, len));
            try {
                mCallback.onWriteSuccess(bytes, off, len);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    public boolean isConnected() {
        if (socket != null) {
            return socket.isConnected();
        }
        return false;
    }
}
