package com.example.deneme;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

public class ListAdapter_BTLE_Devices extends ArrayAdapter<BTLE_Device> {

    Activity activity;
    int layoutResourceID;
    ArrayList<BTLE_Device> devices;
    //static ViewHolder viewHolder;


    public ListAdapter_BTLE_Devices(Activity activity, int resource, ArrayList<BTLE_Device> objects) {
        super(activity.getApplicationContext(), resource, objects);

        this.activity = activity;
        layoutResourceID = resource;
        devices = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            View convertView2;
            LayoutInflater inflater =
                    (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceID, parent, false);
            /*LayoutInflater inflater2 =
                    (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView2 = inflater2.inflate(R.layout.activity_output,parent,false);*/
            viewHolder = new ViewHolder();
            viewHolder.text1 = (TextView) convertView.findViewById(R.id.tv_macaddr);
            //viewHolder.text2 = (TextView) convertView.findViewById(R.id.outputData);
            //viewHolder.text2.setText("ses12");
            viewHolder.button = (ImageButton) convertView.findViewById(R.id.connection);

                    convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("STATUS", String.valueOf(viewHolder.text1.getText()));
                //Log.d("text2 context", String.valueOf(viewHolder.text2.getText()));
                String a = String.valueOf(viewHolder.text1.getText());
                //Log.d("text2 context", String.valueOf(viewHolder.text2.getText()));
                if (a.equals("BE:AC:10:00:00:0") || a.equals("BE:AC:10:00:00:01") || a.equals("F4:F7:F7:24:1D:6E") || a.equals("CB:14:EA:3D:76:5D")) {
                    postToAPI(v, String.valueOf(viewHolder.text1.getText()));
                }
                    else{

                    Toast toast = Toast.makeText(activity.getApplicationContext(),"There is no such device in the database", Toast.LENGTH_LONG);
                    toast.show();
                }
                Log.d("ses","onclickteyiz");
            }


        });

        BTLE_Device device = devices.get(position);
        String name = device.getName();
        String address = device.getAddress();
        int rssi = device.getRSSI();

        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        if (name != null && name.length() > 0) {
            tv_name.setText(device.getName());
        }
        else {
            tv_name.setText("No Name");
        }

        TextView tv_rssi = (TextView) convertView.findViewById(R.id.tv_rssi);
        tv_rssi.setText("RSSI: " + Integer.toString(rssi));

        TextView tv_macaddr = (TextView) convertView.findViewById(R.id.tv_macaddr);
        if (address != null && address.length() > 0) {
            tv_macaddr.setText(device.getAddress());
        }
        else {
            tv_macaddr.setText("No Address");
        }

        return convertView;
    }


    public void postToAPI(View view, String mac) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                   // Log.d("text2 context2", String.valueOf(viewHolder.text2.getText()));
                    URL url = new URL("https://distance-keeper.herokuapp.com/api/menu");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    //jsonParam.put("beaconmac", "F4:F7:F7:C9:86:B2");
                    jsonParam.put("beaconmac", mac);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    //JSONTokener tokener = new JSONTokener(String.valueOf(br));
                    JSONObject json = new JSONObject(sb.toString());




                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());
                    Log.i("MENU", String.valueOf(json.get("menu")));
                    //viewHolder.text2.setText(String.valueOf(json.get("menu")));
                    //Log.d("text2 context3", String.valueOf(viewHolder.text2.getText()));
                    //tv_output.setText(String.valueOf(json.get("menu")));
                    //tv_output.setText("ses12");

                    MainActivity.getInstance().startOutput(String.valueOf(json.get("menu")));
                    //Log.d("text2 context4", String.valueOf(viewHolder.text2.getText()));
                    /*Looper.prepare();
                    Toast toast = Toast.makeText(activity.getApplicationContext(), String.valueOf(json.get("menu")), Toast.LENGTH_LONG);
                    toast.show();*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    customButtonListener customListner;

    public interface customButtonListener {
        public void onButtonClickListner(int position,String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }
}

class ViewHolder {
    TextView text1;
    TextView text2;
    TextView text3;
    TextView text4;
    TextView text5;
    ImageButton button;
}