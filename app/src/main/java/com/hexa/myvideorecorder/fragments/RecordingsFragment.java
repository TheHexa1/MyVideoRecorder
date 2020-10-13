package com.hexa.myvideorecorder.fragments;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hexa.myvideorecorder.R;
import com.hexa.myvideorecorder.adapters.MyRecordingRecyclerViewAdapter;
import com.hexa.myvideorecorder.model.Video;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class RecordingsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private int videosCount = 0;

    private static final String TAG = "RecordingsFragment";

    private Cursor videoCursor;
    private List<Video> mVideos;

    private File mediaStorageDir;
    private RecyclerView recyclerView;
//    private int videosCount;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecordingsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecordingsFragment newInstance(int columnCount) {
        RecordingsFragment fragment = new RecordingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }


        mVideos = new ArrayList<>();

//        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        // refresh fragment to check video updates
       /* FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();*/
    }

    @Override
    public void onResume() {
        super.onResume();

        videoCursor = getVideoCursor();

        /*if(videoCursor != null) {
            videoCursor = getVideoCursor();
            mVideos = getVideos(videoCursor);
        }*/

        if(videosCount < videoCursor.getCount()) {
            String vName, vPath;
            int vDuration, column_index;
            Date vtimeStamp;

            Log.d("Recording Fragment", "OnResume:Number of videos: " + videoCursor.getCount());

            try {
                if (videoCursor.moveToFirst()) {
                    column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                    vName = videoCursor.getString(column_index);
//                    column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
//                    vDuration = Integer.parseInt(videoCursor.getString(column_index));
                    column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
                    vtimeStamp = new Date(Long.parseLong(videoCursor.getString(column_index)));
                    column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    vPath = videoCursor.getString(column_index);

                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(vPath);
                    vDuration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    Log.d("Recording Fragment", "video duration: "+vDuration);

                    mVideos.add(0, new Video(vName, vDuration, vtimeStamp));
                    videosCount = videoCursor.getCount();
                }
            } catch (Exception e) {
                Log.e("RecordingsFragment", "Error: " + e.getMessage());
            } finally {
                videoCursor.close();
            }

            if (recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyItemInserted(0);
                recyclerView.smoothScrollToPosition(0);
            }
        }

//        final File file = mediaStorageDir.listFiles()[mediaStorageDir.listFiles().length-1];//new File("file:///storage/emulated/0/Pictures/MyCameraApp/VID_20201012_212308.mp4");
//        recyclerView.setAdapter(new MyRecordingRecyclerViewAdapter(getVideos(mediaStorageDir)));
        /*MediaScannerConnection.scanFile(getContext(),
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(TAG, "Scanned " + path + ":");
                        Log.i(TAG, "-> uri=" + uri);

                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

                        mmr.setDataSource(getContext(), uri);

                        mVideos.add(new Video(file.getName(),
                                Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)),
                                convertStringToDate(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE))));

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }
                        });
                    }
                });*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recording_list, container, false);

        videoCursor = getVideoCursor();
        videosCount = videoCursor.getCount();
//        getVideos(mediaStorageDir);
       /* final String[] params = { MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN};
        final String selection = MediaStore.Video.Media.DATA +" like?";
        final String[] selectionArgs = new String[]{"%MyCameraApp%"};*/

        /*if(mediaStorageDir.listFiles().length > 0) {
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{mediaStorageDir.listFiles()[0].getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i(TAG, "Scanned " + path + ":");
                            Log.i(TAG, "-> uri=" + uri);
                            videoCursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    params, selection, selectionArgs, null);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(new MyRecordingRecyclerViewAdapter(getVideos(videoCursor)));
                                }
                            });

//        videosCount = videoCursor.getCount();
                        }
                    });
        }else{*/
        /*videoCursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                params, selection, selectionArgs, null);*/
//        }

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            recyclerView.setAdapter(new MyRecordingRecyclerViewAdapter(getVideos(videoCursor)));
//            }
        }


        return view;
    }

    /*public List<Video> getVideos(File mediaStorageDir){
        String vName, vPath;
        int vDuration;
        Date vtimeStamp;// = Calendar.getInstance().getTime();
        mVideos = new ArrayList<>();

        final File[] filesList = mediaStorageDir.listFiles();
        Log.d(TAG, "number of files: "+filesList.length);

//        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        for(File f: filesList){
            Log.d(TAG, "file name: "+f.getName());
            Log.d(TAG, "file absolute path: "+f.getAbsolutePath());
            Log.d(TAG, "file uri: "+Uri.fromFile(f));

            mmr.setDataSource(getContext(), Uri.fromFile(f));
            vName = f.getName();
            vDuration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            vtimeStamp = convertStringToDate(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));

            Log.d(TAG, "file creation time: "+mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
            mVideos.add(new Video(vName, vDuration, vtimeStamp));
        }

        return mVideos;
    }*/

    /*private Date convertStringToDate(String dateStr){
//        String dtStart = "2010-10-15T09:27:37Z";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'.000Z'");
        try {
            return format.parse(dateStr);
//            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public Cursor getVideoCursor() {
        final String[] params = { MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN};
        final String selection = MediaStore.Video.Media.DATA +" like?";
        final String[] selectionArgs = new String[]{"%MyCameraApp%"};

        return getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                params, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
    }

    public List<Video> getVideos(Cursor videoCursor){
        String vName, vPath;
        int vDuration, column_index;
        Date vtimeStamp;
        mVideos = new ArrayList<>();

        Log.d("Recording Fragment", "Oncreate:Number of videos: "+videoCursor.getCount());

        try {
            while (videoCursor.moveToNext()) {
                column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                vName = videoCursor.getString(column_index);
//                column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
//                vDuration = Integer.parseInt(videoCursor.getString(column_index));
                column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
                vtimeStamp = new Date(Long.parseLong(videoCursor.getString(column_index)));
                column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                vPath = videoCursor.getString(column_index);

                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(vPath);
                vDuration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                Log.d("Recording Fragment", "video duration: "+vDuration);

                mVideos.add(new Video(vName, vDuration, vtimeStamp));
            }
        }catch (Exception e){
            Log.e("RecordingsFragment", "Error: "+e.getMessage());
        }finally {
            videoCursor.close();
        }

        return mVideos;
    }
}