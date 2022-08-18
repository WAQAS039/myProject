package com.example.learnquran.connection;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class InternetConnection {
    static AlertDialog networkDialog;
    // check internet connection
    public static void checkInternet(Context con, Activity activity){
        // network dialog
        networkDialog = new MaterialAlertDialogBuilder(con)
                .setTitle("No Internet")
                .setMessage("Check your Connection")
                .setCancelable(false)
                .create();
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                if(activity != null){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            networkDialog.dismiss();
                        }
                    });
                }
                super.onAvailable(network);
            }
            @Override
            public void onLost(@NonNull Network network) {
                if(activity != null){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            networkDialog.show();
                        }
                    });
                }
                super.onLost(network);
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
                final boolean unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
            }
        };
        ConnectivityManager connectivityManager =
                (ConnectivityManager) con.getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }
}
