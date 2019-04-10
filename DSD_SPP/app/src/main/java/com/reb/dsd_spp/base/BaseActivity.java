package com.reb.dsd_spp.base;

import android.support.v4.app.FragmentActivity;

import com.reb.bluetooth.util.DebugLog;
import com.reb.dsd_spp.R;
import com.reb.dsd_spp.util.CustomDialog;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-1-11 17:33
 * @project_name DSD_BLE
 * @history At 2018-1-11 17:33 created by Reb
 */

public class BaseActivity extends FragmentActivity {

    public CustomDialog customLoadingDialog;

    public void showLoadingDialog(int titleId) {
        // 初始化加载对话框

        try {

            if (customLoadingDialog != null) {
                customLoadingDialog.setTipTitle(titleId);
            } else {
                customLoadingDialog = new CustomDialog(this, R.layout.dialog_layout, R.style.DialogTheme, getResources().getString(titleId));
            }
            customLoadingDialog.show();
        } catch (Exception e) {
            DebugLog.e(e.getMessage());
        }
    }

    public void showLoadingDialog(String title) {
        // 初始化加载对话框
        showLoadingDialog(title, false);
    }

    public boolean isDialogShowing() {
        if (customLoadingDialog != null && customLoadingDialog.isShowing()) {
            return true;
        } else return false;
    }

    public void showLoadingDialog(String title, boolean cancelable) {
        // 初始化加载对话框

        try {
            if (customLoadingDialog != null) {
                customLoadingDialog.setTipTitle(title);
                customLoadingDialog.setCancelable(cancelable);
                customLoadingDialog.setCanceledOnTouchOutside(cancelable);
            } else {
                customLoadingDialog = new CustomDialog(this, R.layout.dialog_layout, R.style.DialogTheme, title);
                customLoadingDialog.setCancelable(cancelable);
                customLoadingDialog.setCanceledOnTouchOutside(cancelable);
            }
            customLoadingDialog.show();
        } catch (Exception e) {
            DebugLog.e(e.getMessage());
        }

    }

    public void dismissLoadingDialog() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (customLoadingDialog != null) {
                        customLoadingDialog.dismiss();
                    }


                } catch (Exception e) {
                    DebugLog.e(e.getMessage());
                }
            }
        });


    }

}
