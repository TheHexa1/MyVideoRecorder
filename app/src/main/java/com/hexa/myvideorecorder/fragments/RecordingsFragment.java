package com.hexa.myvideorecorder.fragments;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hexa.myvideorecorder.R;
import com.hexa.myvideorecorder.adapters.MyRecordingRecyclerViewAdapter;
import com.hexa.myvideorecorder.model.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a list of video recordings.
 */
public class RecordingsFragment extends Fragment {

    private int mColumnCount = 2;
    private int videosCount = 0;

    private static final String TAG = "RecordingsFragment";

    private Cursor videoCursor;
    private List<Video> mVideos;
    private RecyclerView recyclerView;

    private String flag = "";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecordingsFragment() {
    }

    //
    public static RecordingsFragment newInstance(int columnCount) {
        return new RecordingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideos = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();

        flag = "onResume";
        videoCursor = getVideoCursor();

        if(videosCount < videoCursor.getCount()) {
            Log.d(TAG, "OnResume:Number of videos: " + videoCursor.getCount());

            try {
                if (videoCursor.moveToFirst()) {
                    addVideos(videoCursor, flag);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recording_list, container, false);

        flag = "onCreate";
        videoCursor = getVideoCursor();
        videosCount = videoCursor.getCount();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            recyclerView.setAdapter(new MyRecordingRecyclerViewAdapter(getVideos(videoCursor)));
        }
        return view;
    }

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
        Log.d(TAG, "OnCreate:Number of videos: "+videoCursor.getCount());

        try {
            while (videoCursor.moveToNext()) {
                addVideos(videoCursor, flag);
            }
        }catch (Exception e){
            Log.e(TAG, "Error: "+e.getMessage());
        }finally {
            videoCursor.close();
        }

        return mVideos;
    }

    public void addVideos(Cursor videoCursor, String flag){

        String vName, vPath;
        int vDuration, column_index;
        Date vtimeStamp;

        column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
        vName = videoCursor.getString(column_index);
        column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
        vtimeStamp = new Date(Long.parseLong(videoCursor.getString(column_index)));
        column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        vPath = videoCursor.getString(column_index);

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(vPath);
        vDuration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        Log.d(TAG, "video duration: "+vDuration);

        if(flag.equals("onResume"))
            mVideos.add(0, new Video(vName, vDuration, vtimeStamp));
        else
            mVideos.add(new Video(vName, vDuration, vtimeStamp));
    }
}