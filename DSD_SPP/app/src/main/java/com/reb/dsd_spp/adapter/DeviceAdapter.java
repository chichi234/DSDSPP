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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/7 0007.
 */

public class DeviceAdapter extends BaseAdapter {

    private List<ExtendedBluetoothDevice> devices = new ArrayList<>();
    private Context mContext;
    private OnDevicePairRequest mListener;

    public DeviceAdapter(Context context) {
        this.mContext = context;
    }


    public void clearDevices() {
        devices.clear();
        notifyDataSetChanged();
    }

    public void addOrUpdateDevice(ExtendedBluetoothDevice device) {
//        final int indexInNotBonded = devices.indexOf(device);
//        if (indexInNotBonded >= 0) {
//            // update
//            ExtendedBluetoothDevice previousDevice = devices.get(indexInNotBonded);
//            previousDevice.rssi = device.rssi;
//        } else {
//            // add
            devices.add(device);
//        }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device, null);
            vh = new ViewHolder();
            vh.mNameView = convertView.findViewById(R.id.item_device_name);
            vh.mAddressView = convertView.findViewById(R.id.item_device_address);
            vh.mBondView = convertView.findViewById(R.id.item_device_bondState);
            vh.mPairBtn = convertView.findViewById(R.id.item_device_connect);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final ExtendedBluetoothDevice device = devices.get(i);
        String name;
        if (!TextUtils.isEmpty(device.name)) {
            name = device.name;
        } else if (!TextUtils.isEmpty(device.device.getName())) {
            name = device.device.getName();
        } else {
            name = mContext.getString(R.string.unknown);
        }
        vh.mNameView.setText(name);
        vh.mAddressView.setText(device.device.getAddress());
        vh.mBondView.setText(device.isBonded ? "Paired" : "No Paired");
        vh.mPairBtn.setText(device.isBonded ? "Add To My Devices" : "Pair");
        vh.mPairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (device.isBonded) {
                    devices.remove(device);
                    notifyDataSetChanged();
                }
                if (mListener != null) {
                    mListener.onRequestPairBt(device);
                }
            }
        });
        return convertView;
    }

    public void updateBondState(String mac) {
        for (int i = 0; i < devices.size(); i++) {
            if (mac.equals(devices.get(i).device.getAddress())) {
                devices.get(i).isBonded = true;
                break;
            }
        }
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView mNameView;
        TextView mAddressView;
        TextView mBondView;
        Button mPairBtn;
    }

    public void setOnDevicePairRequestListener (OnDevicePairRequest listener) {
        mListener = listener;
    }

    public interface OnDevicePairRequest {
        void onRequestPairBt(ExtendedBluetoothDevice device);
    }
}
