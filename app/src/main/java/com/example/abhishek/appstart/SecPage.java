package com.example.abhishek.appstart;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class SecPage extends AppCompatActivity {
TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
tv = (TextView)findViewById(R.id.text4);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

    }
    @Override
    protected void onStart() {
        super.onStart();



        if (SomePage.wasInBackground) {
            Date d = new Date();
            tv.setText(" "+d.toString());

            SomePage.wasInBackground = false;
        }


    }
}
