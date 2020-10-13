package com.hexa.myvideorecorder.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hexa.myvideorecorder.Helper;
import com.hexa.myvideorecorder.R;
import com.hexa.myvideorecorder.model.Video;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Video}.
 */
public class MyRecordingRecyclerViewAdapter extends RecyclerView.Adapter<MyRecordingRecyclerViewAdapter.ViewHolder> {

    private final List<Video> mValues;

    public MyRecordingRecyclerViewAdapter(List<Video> items) {
        mValues = items;
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

//        Glide.with(context).asBitmap().load(Uri.fromFile(new File(holder.mItem.getPath()))).into(holder.vThumb);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView vName;
        public final TextView vDuration;
        public final TextView vTimeStamp;
//        public final ImageView vThumb;
        public Video mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            vName = (TextView) view.findViewById(R.id.vName);
            vDuration = (TextView) view.findViewById(R.id.vDuration);
            vTimeStamp = view.findViewById(R.id.vTimeStamp);
//            vThumb = view.findViewById(R.id.vThumb);
        }

       /* @Override
        public String toString() {
            return super.toString() + " '" + vDuration.getText() + "'";
        }*/

    }


}