package com.reb.dsd_spp.util;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.reb.dsd_spp.R;


/**
 * @author 409176
 */
public class CustomDialog extends Dialog {
    private static int default_width = 160;
    private static int default_height = 120;
    private TextView loadingTip;

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int layout, int style, String message) {
        this(context, default_width, default_height, layout, style, message);
    }

    public CustomDialog(Context context, int width, int height, int layout, int style, String message) {
        super(context, style);
        setContentView(layout);
        this.loadingTip = (TextView) findViewById(R.id.message);
        loadingTip.setText(message);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        float density = getDensity(context);
        params.width = (int) (width * density);
        params.height = (int) (height * density);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public void setTipTitle(int titleId) {
        loadingTip.setText(titleId);
    }

    public void setTipTitle(String title) {
        loadingTip.setText(title);
    }

    public float getDensity(Context context) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        return dm.density;
    }
}
