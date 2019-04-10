package com.reb.dsd_spp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.reb.bluetooth.scanner.ExtendedBluetoothDevice;
import com.reb.dsd_spp.R;
import com.reb.dsd_spp.db.DeviceBond;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/7 0007.
 */

public class MyDeviceAdapter extends BaseAdapter {

//    private List<ExtendedBluetoothDevice> devices = new ArrayList<>();
    private List<DeviceBond> devices = new ArrayList<>();
    private Context mContext;
    private OnDeviceClickListener mListener;

    public MyDeviceAdapter(Context context) {
        this.mContext = context;
    }

    public void clearDevices() {
        devices.clear();
        notifyDataSetChanged();
    }

    public void addOrUpdateDevice(DeviceBond deviceBond) {
//        devices.add(device);
        devices.add(deviceBond);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int i) {
        return devices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_device, null);
            vh = new ViewHolder();
            vh.mNameView = convertView.findViewById(R.id.item_device_name);
            vh.mAddressView = convertView.findViewById(R.id.item_device_address);
            vh.mConnectBtn = convertView.findViewById(R.id.item_device_connect);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
//        String name;
//        final ExtendedBluetoothDevice device = devices.get(i);
//        if (!TextUtils.isEmpty(device.name)) {
//            name = device.name;
//        } else if (!TextUtils.isEmpty(device.device.getName())) {
//            name = device.device.getName();
//        } else {
//            name = mContext.getString(R.string.unknown);
//        }
//        vh.mNameView.setText(name);
//        vh.mAddressView.setText(device.device.getAddress());
//        vh.mConnectBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mListener != null) {
//                    mListener.onMyDeviceClick(device);
//                }
//            }
//        });

        final DeviceBond device = devices.get(i);
        vh.mNameView.setText(device.getDisplay_name());
        vh.mAddressView.setText(device.getMac());
        vh.mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMyDeviceClick(device);
                }
            }
        });
        return convertView;
    }

    public void setOnDeviceClickListener(OnDeviceClickListener deviceListFragment) {
        this.mListener = deviceListFragment;
    }

    private class ViewHolder {
        TextView mNameView;
        TextView mAddressView;
        Button mConnectBtn;
    }

    public interface OnDeviceClickListener {
//        void onMyDeviceClick(ExtendedBluetoothDevice device);
        void onMyDeviceClick(DeviceBond device);
    }
}
