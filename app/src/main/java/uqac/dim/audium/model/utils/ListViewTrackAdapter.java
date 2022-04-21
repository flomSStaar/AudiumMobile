package uqac.dim.audium.model.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Track;

public class ListViewTrackAdapter extends ArrayAdapter<Track> {
    private final List<Track> trackList;
    private final Context context;
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public ListViewTrackAdapter(List<Track> tracks, Context context) {
        super(context, R.layout.list_view_track_item, tracks);
        this.context = context;
        this.trackList = tracks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.list_view_track_item, parent, false);
        }

        TextView tvTrackNumber = row.findViewById(R.id.tv_track_number);
        TextView tvTrackName = row.findViewById(R.id.tv_track_name);
        TextView tvArtistName = row.findViewById(R.id.tv_artist_name);
        ImageView ivTrack = row.findViewById(R.id.iv_track);

        Track track = trackList.get(position);
        tvTrackNumber.setText(String.valueOf(position + 1));
        tvTrackName.setText(track.getName());
        Picasso.with(context).load(track.getImageUrl()).error(R.drawable.ic_notes).into(ivTrack);

        database.child("artists")
                .child(String.valueOf(trackList.get(position).getArtistId()))
                .child("stageName").get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        String artistName = dataSnapshot.getValue(String.class);
                        if (artistName != null) {
                            tvArtistName.setText(artistName);
                        } else {
                            tvArtistName.setText("");
                        }
                    }
                });

        return row;
    }
}