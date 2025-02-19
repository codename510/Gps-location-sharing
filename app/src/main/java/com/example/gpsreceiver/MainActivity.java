package com.example.gpsreceiver;

import android.Manifest; import android.content.Context; import android.content.pm.PackageManager; import android.net.wifi.WifiManager; import android.os.AsyncTask; import android.os.Bundle; import android.text.format.Formatter; import android.util.Log; import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity; import androidx.core.app.ActivityCompat;

import java.io.BufferedReader; import java.io.InputStreamReader; import java.net.InetAddress; import java.net.NetworkInterface; import java.net.Socket; import java.util.Enumeration;

public class MainActivity extends AppCompatActivity { private static final int PORT = 5050; private TextView locationText; private String phoneIp;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    locationText = findViewById(R.id.locationText);
    phoneIp = getPhoneIp();
    requestPermissions();
}

private void requestPermissions() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    } else {
        startReceivingLocation();
    }
}

private void startReceivingLocation() {
    new ReceiveLocationTask().execute();
}

private class ReceiveLocationTask extends AsyncTask<Void, String, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        while (true) {
            try {
                Socket socket = new Socket(phoneIp, PORT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String location = reader.readLine();
                publishProgress(location);
                reader.close();
                socket.close();
            } catch (Exception e) {
                Log.e("GPS Receiver", "Error receiving location", e);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        locationText.setText("Location: " + values[0]);
    }
}

private String getPhoneIp() {
    try {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (!address.isLoopbackAddress() && address.getAddress().length == 4) {
                    return address.getHostAddress();
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return "192.168.1.100"; // Default fallback IP
}

                                                    }
