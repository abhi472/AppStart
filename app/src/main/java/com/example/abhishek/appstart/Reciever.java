package com.example.abhishek.appstart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by abhishek on 17/3/16.
 */
public class Reciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"broadcast recieved",Toast.LENGTH_SHORT).show();
    }
}
