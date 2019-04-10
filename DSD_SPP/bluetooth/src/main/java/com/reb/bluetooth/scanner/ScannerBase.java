package com.reb.bluetooth.scanner;

import android.content.Context;
import android.os.Handler;

import com.reb.bluetooth.BtConfig;
import com.reb.bluetooth.scanner.ble.ScanFilter;

/**
 * 扫描默认5s
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-5 16:53
 * @package_name com.reb.light.ble.scanner
 * @project_name Light
 * @history At 2018-9-5 16:53 created by Reb
 */
public abstract class ScannerBase {
    protected Context mContext;
    protected ScanLeCallback mScanLeCallback;
    protected long mTimeout = BtConfig.BT_SCAN_TIME;
    private Handler mHandler;
    protected boolean mIsScanning = false;

    public ScannerBase(Context context, ScanLeCallback scanCallback) {
        this.mContext = context;
        this.mScanLeCallback = scanCallback;
        this.mHandler = new Handler(context.getMainLooper());
    }

    public ScannerBase setTimeout(long mills) {
        this.mTimeout = mills;
        return this;
    }

    protected void stopScanDelay(long timeoutMills) {
        if (timeoutMills > 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            }, timeoutMills);
        }
    }

    public boolean isScanning() {
        return mIsScanning;
    }

    public abstract ScannerBase setScanFilter(ScanFilter scanFilter);

    public abstract void startScan();

    public abstract void stopScan();
}
