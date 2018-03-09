package com.hafezi.games.spaceshooter2d.Utility;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hafezi.games.spaceshooter2d.R;

import java.util.ArrayList;

/**
 * Created by Mojtaba Hafezi on 25.02.2018.
 */
/***************************************************************************************
 *    @Citation
 *    Title: ArrayAdapter
 *    Author: Google LLC
 *    Date: 2018
 *    Code version: 1.0
 *    Change of original code: Major
 *    Original idea for: basis for the adapter
 *    Available at: https://developer.android.com/reference/android/widget/ArrayAdapter.html
 *    Last access: 09.03.2018
 ***************************************************************************************/
//This class is required so that BluetoothDevices can be used in the ListView
public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    private ArrayList<BluetoothDevice> devices;

    public DeviceAdapter( Context context, ArrayList<BluetoothDevice> devices) {

        super(context, 0, devices);
        this.devices = devices;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        BluetoothDevice device = devices.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device, parent, false);
        }

        if (device != null) {
            TextView deviceName = (TextView) convertView.findViewById(R.id.tvName);

            if (deviceName != null) {
                deviceName.setText(device.getName());
                deviceName.setTextColor(Color.CYAN);
            }
        }
        return convertView;
    }

}
