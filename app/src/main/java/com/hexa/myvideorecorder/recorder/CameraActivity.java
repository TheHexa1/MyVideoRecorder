package com.hexa.myvideorecorder.recorder;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hexa.myvideorecorder.Helper;
import com.hexa.myvideorecorder.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CameraActivity extends AppCompatActivity{

    private final String TAG = "CameraActivity";

    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;

    private boolean isRecording = false;
    private long duration = 5000; // default recording duration in milliseconds

    private static String video_title;
    private File mediaFile;

    private CountDownTimer countDownTimer;
    private TextView timerTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if(getIntent().getExtras() != null) {
            duration = Math.round(getIntent().getFloatExtra("duration", 5)) * 1000;
            video_title = getIntent().getStringExtra("title");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        timerTxt = findViewById(R.id.timerTxt);
        timerTxt.bringToFront();
        timerTxt.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startRecording();
            }
        }, 1000);
    }

    public void startRecording(){
        // initialize video camera
        if (prepareMediaRecorder()) {
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording
            mediaRecorder.start();
            isRecording = true;

            // to update the time text-view at every second
            countDownTimer = new CountDownTimer(duration, 1000) {
                public void onTick(long millisUntilFinished) {

                    String formattedTime = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));

                    timerTxt.setText(formattedTime);
                }

                public void onFinish() {
                    if(isRecording)
                        stopRecording();
                }
            };
            countDownTimer.start();

        } else {
            // prepare didn't work, release the camera
            releaseMediaRecorder();
        }
    }

    public void stopRecording(){
        mediaRecorder.stop();  // stop the recording
        releaseMediaRecorder(); // release the MediaRecorder object
        mCamera.lock();         // take camera access back from MediaRecorder
        isRecording = false;
        finish();
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(countDownTimer != null)
            countDownTimer.cancel(); // remove count down timer

        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        if(video_title.isEmpty())
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "VID_"+ timeStamp + ".mp4"); //.mp4
        else
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    video_title + ".mp4"); //.mp4

        // Scan newly added file for indexing
        indexNewFile(mediaFile);

        return mediaFile;
    }

    public void indexNewFile(File file){
        MediaScannerConnection.scanFile(this,
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(TAG, "Scanned " + path + ":");
                        Log.i(TAG, "-> uri=" + uri);
                    }
                });
    }

    // configure media recorder
    public boolean prepareMediaRecorder(){

        Log.d(TAG, "prepareMediaRecorder");

        if(mCamera == null)
            mCamera = getCameraInstance();

        mediaRecorder = new MediaRecorder();

        // Unlock and set camera to MediaRecorder
        mCamera.unlock();

        mediaRecorder.setOrientationHint(90);
        mediaRecorder.setCamera(mCamera);

        // Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Set a CamcorderProfile (requires API Level 8 or higher)
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Set output file: also check if SDCard is mounted or not
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mediaRecorder.setOutputFile(getOutputMediaFile().toString());
        }else{
            Helper.displayAlert("Storage not available!", "App is not able to detect storage space. " +
                    "Storage space is required to save video recordings.", getApplicationContext());
            finish();
        }

        // Set the preview output
        mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Prepare configured MediaRecorder
        try {
            mediaRecorder.prepare();
            Log.d(TAG, "mediaRecorder Prepared!");
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(countDownTimer != null)
            countDownTimer.cancel(); // remove count down timer

        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mediaRecorder != null && isRecording)
                    mediaRecorder.stop(); // stop recording when camera is closed abruptly (i.e. onBackPressed)
                finish();
            }
        }, 1000);

    }
}