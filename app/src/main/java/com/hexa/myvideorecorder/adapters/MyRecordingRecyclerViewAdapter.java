package com.hexa.myvideorecorder.adapters;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hexa.myvideorecorder.Helper;
import com.hexa.myvideorecorder.R;
import com.hexa.myvideorecorder.VideoPlayer;
import com.hexa.myvideorecorder.model.Video;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Video}.
 */
public class MyRecordingRecyclerViewAdapter extends RecyclerView.Adapter<MyRecordingRecyclerViewAdapter.ViewHolder> {

    private final List<Video> mValues;
    private Context mContext;

    public MyRecordingRecyclerViewAdapter(List<Video> items, Context mContext) {
        mValues = items;
        this.mContext = mContext;
        Log.d("Adapter", "videos: "+mValues.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recordings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.vName.setText(mValues.get(position).getName());
        holder.vDuration.setText(Helper.getFormattedDuration(mValues.get(position).getDuration()));
        holder.vTimeStamp.setText(mValues.get(position).getTimeStamp().toString());

        holder.vCardVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, VideoPlayer.class);
                i.putExtra("videoURI", holder.mItem.getVideoURI());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView vName;
        public final TextView vDuration;
        public final TextView vTimeStamp;
        public final CardView vCardVideo;
        public Video mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            vName = view.findViewById(R.id.vName);
            vDuration =  view.findViewById(R.id.vDuration);
            vTimeStamp = view.findViewById(R.id.vTimeStamp);
            vCardVideo = view.findViewById(R.id.vCardVideo);
        }
    }
}