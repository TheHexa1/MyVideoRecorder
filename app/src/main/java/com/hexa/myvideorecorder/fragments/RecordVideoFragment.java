package com.hexa.myvideorecorder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.hexa.myvideorecorder.Helper;
import com.hexa.myvideorecorder.R;
import com.hexa.myvideorecorder.recorder.CameraActivity;

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Fragment containing the view for first tab/home screen
 */
public class RecordVideoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    static final int REQUEST_VIDEO_CAPTURE = 1;

    private Button btnRecord;
    private EditText recordingTimerET;
    private Slider sliderRecordingTime;
    private TextView durationTV;

    public static RecordVideoFragment newInstance(int index) {
        RecordVideoFragment fragment = new RecordVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
//        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        recordingTimerET = root.findViewById(R.id.recordingNameET);

        durationTV = root.findViewById(R.id.durationTV);
//        String temp = "Video will be recorded for 0 Minutes 15 Seconds";
//        durationTV.setText(temp);

        sliderRecordingTime = root.findViewById(R.id.sliderRecordingTime);
        sliderRecordingTime.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
//                        DateFormat format = DateFormat.getTimeInstance();
//                        format.format()
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
        /*final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

    /*public void startRecording(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            videoView.setVideoURI(videoUri);
        }
    }*/

}