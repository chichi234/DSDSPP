package com.reb.bluetooth.connect.spp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.RemoteException;
import android.util.Log;

import com.reb.bluetooth.BtConfig;
import com.reb.bluetooth.connect.ErrorCode;
import com.reb.dsd_spp.IBtCallback;

import java.io.IOException;

/**
 * 蓝牙客户端连接线程
 */
public class BluetoothClientConnThread extends Thread {

	private BluetoothDevice serverDevice;		//服务器设备
	private IBtCallback mConnectCallback;	//用于向客户端Service回传消息的handler
	private BluetoothSocket socket;		//通信Socket

	/**
	 * 构造函数
	 * @param connectCallback
	 *          连接回调
	 * @param serverDevice
	 *          要连接的设备
	 */
	BluetoothClientConnThread(IBtCallback connectCallback, BluetoothDevice serverDevice) {
		this.mConnectCallback = connectCallback;
		this.serverDevice = serverDevice;
	}

	@Override
	public void run() {
		try {
			//UUID匹配
			socket = serverDevice.createRfcommSocketToServiceRecord(BtConfig.SPP_UUID);
			mConnectCallback.onStateChange(0);
			socket.connect();
			mConnectCallback.onStateChange(1);
			Log.e("Socket connect", String.valueOf(socket.isConnected()));


		} catch (Exception ex) {
			try {
				//如果失败，则关闭socket
				socket.close();
				ex.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//发送连接失败消息
			try {
				mConnectCallback.onError(ErrorCode.ERROR_CODE_CONNECT, ex.getMessage());
				mConnectCallback.onStateChange(2);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public BluetoothSocket getSocket() {
		return socket;
	}
}
