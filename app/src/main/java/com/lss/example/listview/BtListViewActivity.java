package com.lss.example.listview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.TypedArrayUtils;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import com.lss.example.R;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class BtListViewActivity extends AppCompatActivity {
    public static final String TAG = "BluetoothManager";
    private static final int ACTIVITY_PERMISSION_RESULT = 99;


    String[] requiredPermissions () {
        ArrayList<String> list = new ArrayList<>();

        list.add(Manifest.permission.BLUETOOTH);
        list.add(Manifest.permission.BLUETOOTH_ADMIN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            list.add(Manifest.permission.BLUETOOTH_SCAN);
            list.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        list.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        return list.toArray(new String[0]);
    }

    SwipeRefreshLayout sl_layout;
    ListView lv_devices;
    TextView tv_log;

    BluetoothDevicesListAdapter bluetoothDevicesListAdapter;
    ArrayList<BluetoothDevice> list;
    RxBluetooth rxBluetooth;
    AdapterView.OnItemClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_list_view);

        lv_devices = findViewById(R.id.lv_devices);
        tv_log = findViewById(R.id.tv_log);
        sl_layout = findViewById(R.id.sl_layout);
        list = new ArrayList<>();

        if (checkPermission(requiredPermissions())) {
            init();
        }
    }

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    void init () {
        rxBluetooth = new RxBluetooth(this);

        listener = (parent, view, position, id) -> {
            Log.d(TAG, "onItemSelected: ");
            rxBluetooth.cancelDiscovery();
            BluetoothDevice selected = list.get(position);

            if (selected.getUuids() != null) {
                tv_log.setText("connect" + selected.getName());

                rxBluetooth.connectAsClient(selected, selected.getUuids()[0].getUuid())
                        .doOnError(throwable -> Log.d(TAG, "onError" + throwable.getMessage()))
                        .subscribe(bluetoothSocket -> bluetoothSocket.getOutputStream().write("test Msg Print".getBytes(StandardCharsets.UTF_8)));
            } else {
                tv_log.setText("uuid error");
                Log.d(TAG, list.toString());
            }
        };



        sl_layout.setOnRefreshListener(() -> {
            list.clear();
            lv_devices.setAdapter(new BluetoothDevicesListAdapter(this, R.layout.dialog_bluetooth_list_item, list));

            if (rxBluetooth.isDiscovering()) {
                rxBluetooth.cancelDiscovery();
            }

            Handler handler = new Handler();
            handler.postDelayed(()-> {
                scan();
                sl_layout.setRefreshing(false);
            }, 200);
        });

        if (checkAvailable()) {
            scan();
        }
    }


    public Boolean checkAvailable () {
        return rxBluetooth.isBluetoothEnabled() && rxBluetooth.isBluetoothAvailable();
    }

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    public void scan () {

        rxBluetooth.observeDevices()
                .subscribeOn(Schedulers.newThread())
                .doOnError(throwable -> Log.d(TAG, "onError" + throwable.getMessage()))
                .subscribe(device -> {
                    Log.d(TAG, "scan: " + device.getName() + "-" + device.getAddress());
                    list.add(device);
                    lv_devices.setAdapter(new BluetoothDevicesListAdapter(this, R.layout.dialog_bluetooth_list_item, list));
                    lv_devices.setOnItemClickListener(listener);
                });

        rxBluetooth.observeDiscovery()
                .subscribe(s -> {
                    if (Objects.equals(s, BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                        tv_log.setText("in Scannig");
                    } else {
                        tv_log.setText("Scan Finish");
                    }

                });

        rxBluetooth.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rxBluetooth != null) {
            rxBluetooth.cancelDiscovery();

        }
        rxBluetooth = null;
    }

    private Boolean checkPermission(String[] permissions) {
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, ACTIVITY_PERMISSION_RESULT);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PERMMISSION FAILED", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(this::finish, 2000);
                return;
            }
        }
    }


    static class BluetoothDevicesListAdapter extends ArrayAdapter<BluetoothDevice> {
        Context context;
        int resource;
        ArrayList<BluetoothDevice> devices;

        public BluetoothDevicesListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BluetoothDevice> devices) {
            super(context, resource, devices);
            this.context = context;
            this.resource = resource;
            this.devices = devices;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            BluetoothDevicesViewHolder bluetoothDevicesViewHolder;

            if (convertView == null) {
                convertView = ((Activity)context).getLayoutInflater().inflate(resource, null);
                bluetoothDevicesViewHolder = new BluetoothDevicesViewHolder(convertView);

                convertView.setTag(bluetoothDevicesViewHolder);
            } else {
                bluetoothDevicesViewHolder = (BluetoothDevicesViewHolder) convertView.getTag();
            }

            bluetoothDevicesViewHolder.tv_name.setText(devices.get(position).getName());
            bluetoothDevicesViewHolder.tv_addr.setText(devices.get(position).getAddress());

            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }


    static class BluetoothDevicesViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_addr;

        public BluetoothDevicesViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.txt_device);
            tv_addr = itemView.findViewById(R.id.txt_address);
        }
    }
}