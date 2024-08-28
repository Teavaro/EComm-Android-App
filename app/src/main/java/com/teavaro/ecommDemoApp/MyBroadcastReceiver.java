package com.teavaro.ecommDemoApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle the broadcast event
        Log.d("MyBroadcastReceiver", "Broadcast received");
    }
}