package com.hexa.myvideorecorder;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.hexa.myvideorecorder.adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private boolean cameraPermission = false;
    private boolean recordAudio = false;
    private boolean readStorage = false;
    private boolean writeStorage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // request for necessary permissions if not already granted
        if (!Helper.hasPermissions(this, Helper.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Helper.PERMISSIONS, Helper.PERMISSION_ALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Helper.PERMISSION_ALL) {
            // If request is cancelled, the result arrays are empty.

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.CAMERA)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    cameraPermission = true;
                }

                if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    writeStorage = true;
                }

                if(permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    readStorage = true;
                }

                if(permissions[i].equals(Manifest.permission.RECORD_AUDIO)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    recordAudio = true;
                }
            }
            if (cameraPermission && writeStorage && readStorage && recordAudio) {
                // All permissions are granted. Continue the action or workflow
                // in your app.
            }  else {
                // Explain to the user that the feature is unavailable because
                // the features requires a permission that the user has denied.
                // At the same time, respect the user's decision. Don't link to
                // system settings in an effort to convince the user to change
                // their decision.

                Helper.displayAlert("Permissions Required",
                        "All the requested permissions are necessary in order to record and store videos",
                        MainActivity.this);
            }
            return;
        }
    }
}