package com.hexa.myvideorecorder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.hexa.myvideorecorder.Helper;
import com.hexa.myvideorecorder.R;
import com.hexa.myvideorecorder.recorder.CameraActivity;

/**
 * Fragment containing the view for first tab/home screen
 */
public class RecordVideoFragment extends Fragment {

    private Button btnRecord;
    private EditText recordingTimerET;
    private Slider sliderRecordingTime;
    private TextView durationTV;

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
                Intent i = new Intent(getContext(), CameraActivity.class);
                i.putExtra("duration", sliderRecordingTime.getValue());
                i.putExtra("title", recordingTimerET.getText().toString());
                startActivity(i);
            }
        });

        return root;
    }
}