package uqac.dim.audium.adapter;

import android.content.Context;
import android.content.Intent;
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
import uqac.dim.audium.activity.admin.TrackPage;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;

public class ListViewTrackAdapter extends ArrayAdapter<Track> {
    private final List<Track> tracks;
    private final Context context;
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private final String username;

    private boolean hasIndex = true;
    private boolean hasInfos = true;

    public ListViewTrackAdapter(Context context, List<Track> tracks, String username) {
        super(context, R.layout.list_view_track_item, tracks);
        this.context = context;
        this.tracks = tracks;
        this.username = username;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.list_view_track_item, parent, false);
        }

        Track track = tracks.get(position);

        TextView tvTrackNumber = row.findViewById(R.id.tv_track_number);
        TextView tvTrackName = row.findViewById(R.id.tv_track_name);
        TextView tvArtistName = row.findViewById(R.id.tv_artist_name);
        ImageView ivTrack = row.findViewById(R.id.iv_track);
        ImageView btnTrackInfos = row.findViewById(R.id.iv_track_infos);

        if (hasInfos) {
            btnTrackInfos.setOnClickListener(view -> {
                Intent intent = new Intent(context, TrackPage.class);
                intent.putExtra("trackId", track.getId());
                intent.putExtra("albumId", track.getAlbumId());
                intent.putExtra("username", username);
                context.startActivity(intent);
            });
        } else {
            btnTrackInfos.setVisibility(View.GONE);
        }
        if (hasIndex) {
            tvTrackNumber.setText(String.valueOf(position + 1));
        } else {
            tvTrackNumber.setVisibility(View.GONE);
        }
        tvTrackName.setText(track.getName());
        Picasso.with(context).load(track.getImageUrl()).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(ivTrack);

        database.child("artists")
                .child(String.valueOf(track.getArtistId())).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        Artist artist = dataSnapshot.getValue(Artist.class);
                        if (artist != null) {
                            tvArtistName.setText(artist.getPrintableName());
                        } else {
                            tvArtistName.setText("");
                        }
                    }
                });

        return row;
    }

    public ListViewTrackAdapter setHasIndex(boolean hasIndex) {
        this.hasIndex = hasIndex;
        return this;
    }

    public ListViewTrackAdapter setHasInfos(boolean hasInfos) {
        this.hasInfos = hasInfos;
        return this;
    }
}