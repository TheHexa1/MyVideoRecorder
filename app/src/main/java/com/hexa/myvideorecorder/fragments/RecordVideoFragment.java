package com.hexa.myvideorecorder.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;
import com.hexa.myvideorecorder.Helper;
import com.hexa.myvideorecorder.MainActivity;
import com.hexa.myvideorecorder.R;
import com.hexa.myvideorecorder.adapters.SectionsPagerAdapter;
import com.hexa.myvideorecorder.recorder.CameraActivity;

/**
 * Fragment containing the view for first tab/home screen
 */
public class RecordVideoFragment extends Fragment {

    private Button btnRecord;
    private EditText recordingTimerET;
    private Slider sliderRecordingTime;
    private TextView durationTV;

    private boolean cameraPermission = false;
    private boolean recordAudio = false;
    private boolean readStorage = false;
    private boolean writeStorage = false;

    public static RecordVideoFragment newInstance(int index) {
        return new RecordVideoFragment();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        recordingTimerET = root.findViewById(R.id.recordingNameET);
        durationTV = root.findViewById(R.id.durationTV);

        sliderRecordingTime = root.findViewById(R.id.sliderRecordingTime);
        sliderRecordingTime.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                String timeStr = Helper.getFormattedDuration(Math.round(value * 1000));
                String formattedText = timeStr.split(":")[0] + " Minutes "
                        + timeStr.split(":")[1] + " Seconds";
                return formattedText;
            }
        });
        sliderRecordingTime.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String timeStr = Helper.getFormattedDuration(Math.round(value * 1000));
                String temp = timeStr.split(":")[0] + " Minutes " +
                        timeStr.split(":")[1] + " Seconds";
                durationTV.setText(temp);
            }
        });

        btnRecord = root.findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Helper.hasPermissions(getContext(), Helper.PERMISSIONS)) {
                    requestPermissions(Helper.PERMISSIONS, Helper.PERMISSION_ALL);
                }else {
                    Intent i = new Intent(getContext(), CameraActivity.class);
                    i.putExtra("duration", sliderRecordingTime.getValue());
                    i.putExtra("title", recordingTimerET.getText().toString());
                    startActivity(i);
                }
            }
        });

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                Intent i = new Intent(getContext(), CameraActivity.class);
                i.putExtra("duration", sliderRecordingTime.getValue());
                i.putExtra("title", recordingTimerET.getText().toString());
                startActivity(i);
            }  else {
                // Explain to the user that the feature is unavailable because
                // the features requires a permission that the user has denied.
                // At the same time, respect the user's decision. Don't link to
                // system settings in an effort to convince the user to change
                // their decision.

                Helper.displayAlert("Permissions Required",
                        "All the requested permissions are necessary in order to record and store videos",
                        getContext());
            }
            return;
        }
    }
}