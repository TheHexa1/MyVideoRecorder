package com.hexa.myvideorecorder.recorder;

import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hexa.myvideorecorder.R;
import com.hexa.myvideorecorder.model.Video;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CameraActivity extends AppCompatActivity{

    private final String TAG = "CameraActivity";

    private Camera mCamera;
    private CameraPreview mPreview;
//    private SurfaceView preview;
//    private SurfaceHolder surfaceHolder;
    private MediaRecorder mediaRecorder;

    private boolean isRecording = false;

//    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private long duration = 5000; // default recording duration in milliseconds
    private static String video_title;
    private File mediaFile;
    private CountDownTimer countDownTimer;
//    private int delay = 1000; //default delay in milliseconds

    private Handler timerHandler, mHandler;
    private Runnable timerRunnable, mRunnable;

    private TextView timerTxt, recordingTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if(getIntent().getExtras() != null) {
            duration = Math.round(getIntent().getFloatExtra("duration", 5)) * 1000;
            video_title = getIntent().getStringExtra("title");
        }

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

//        recordingTxt = findViewById(R.id.recordingTxt);
//        recordingTxt.bringToFront();
        timerTxt = findViewById(R.id.timerTxt);
        timerTxt.bringToFront();
        timerTxt.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)));

//        startRecording();
//        configureVideoRecorder();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            isRecording = true;
            // to update the time textview at every second
            countDownTimer = new CountDownTimer(duration, 1000) {
                public void onTick(long millisUntilFinished) {

                    String formattedTime = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));

                    timerTxt.setText(formattedTime);
                }

                public void onFinish() {
//                    mediaRecorder.stop();
                    if(isRecording)
                        stopRecording();
                }
            };
            countDownTimer.start();
            /*timerHandler = new Handler();
            timerRunnable = new Runnable(){
                public void run(){
                    //do something

                    int remainingTime = duration - delay;
                    String formattedTime = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(remainingTime) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(remainingTime) % TimeUnit.MINUTES.toSeconds(1));

                    timerTxt.setText(formattedTime);
                    timerHandler.postDelayed(this, delay);
                }
            };
            timerHandler.postDelayed(timerRunnable, delay);*/

            // to stop the timer and recording after specified time
            /*mHandler = new Handler();
            mRunnable = new Runnable(){
                public void run(){
                    //do something
                    timerHandler.removeCallbacks(timerRunnable);
                    if(isRecording)
                        stopRecording();
                }
            };
            mHandler.postDelayed(mRunnable, duration);*/

            // inform the user that recording has started
//            setCaptureButtonText("Stop");
        } else {
            // prepare didn't work, release the camera
            releaseMediaRecorder();
            // inform user
        }
    }

    public void stopRecording(){
        // stop recording and release camera
//        mediaRecorder.sta

        mediaRecorder.stop();  // stop the recording
        releaseMediaRecorder(); // release the MediaRecorder object
        mCamera.lock();         // take camera access back from MediaRecorder

        // inform the user that recording has stopped
//        setCaptureButtonText("Capture");
        isRecording = false;
//        Log.d(TAG, "file uri: "+getOutputMediaFileUri());
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

//    private void configureVideoRecorder(){
//
//
//    }

    @Override
    protected void onPause() {
        super.onPause();
        if(timerHandler != null)
            timerHandler.removeCallbacks(timerRunnable);
        if(mHandler != null)
            mHandler.removeCallbacks(mRunnable);

        if(countDownTimer != null)
            countDownTimer.cancel(); // remove count down timer

        if(mediaRecorder != null && isRecording)
            mediaRecorder.stop(); // stop recording when camera is closed abruptly


        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    /*private void deleteUnfinishedRecordings(){

    }*/

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

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
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

        /*sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                + Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/MyCameraApp")));*/

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        if(video_title.isEmpty())
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "VID_"+ timeStamp + ".mp4"); //.mp4
        else
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    video_title + ".mp4"); //.mp4

        /*if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4"); //.mp4
        } else {
            return null;
        }*/

        // Scan newly added file for indexing
        MediaScannerConnection.scanFile(this,
                new String[] { mediaFile.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(TAG, "Scanned " + path + ":");
                        Log.i(TAG, "-> uri=" + uri);
                    }
        });

        return mediaFile;
    }

    public boolean prepareMediaRecorder(){
//                mCamera = getCameraInstance();
        mediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();

        mediaRecorder.setOrientationHint(90);
        mediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mediaRecorder.setOutputFile(getOutputMediaFile().toString());
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

        Log.d(TAG, "prepareMediaRecorder");
        // Step 5: Set the preview output
        mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
//        mediaRecorder.setMaxDuration(5000);

        /*mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
           @Override
           public void onInfo(MediaRecorder mr, int what, int extra) {
               if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                   mediaRecorder.stop();
//                   stopRecording();
                   *//*new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           mediaRecorder.stop();
//                           stopRecording();
                       }
                   }, 1000);*//*


//                   mCamera.lock();         // take camera access back from MediaRecorder
//                   Log.d(TAG, "file uri: "+getOutputMediaFileUri(2));
//                   finish();

                   *//*new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           stopRecording();
                       }
                   }, 3000);*//*
               }
           }
       });*/
        // Step 6: Prepare configured MediaRecorder
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

    /*@Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

        if(prepareMediaRecorder()){
            //
            startRecording();
        }else{
            // prepare didn't work, release the camera
            releaseMediaRecorder();
            // inform user
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }*/
}