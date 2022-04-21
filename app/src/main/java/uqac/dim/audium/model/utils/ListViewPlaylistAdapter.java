package uqac.dim.audium.model.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Playlist;

public class ListViewPlaylistAdapter extends ArrayAdapter<Playlist> {
    private List<Playlist> playlists;
    private Context context;
    private String playlistTitle;
    private DatabaseReference database;

    public ListViewPlaylistAdapter(List<Playlist> playlists, Context context) {
        super(context, R.layout.list_view_track_item, playlists);
        this.context = context;
        this.playlists = new ArrayList<>();
        this.playlists = playlists;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.list_view_track_item, parent, false);
        TextView tv = row.findViewById(R.id.tv_track_name);
        database = FirebaseDatabase.getInstance().getReference();
        database.child("playlists").child(String.valueOf(playlists.get(position).getUsername())).child(String.valueOf(playlists.get(position).getId())).child("title").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    playlistTitle = dataSnapshot.getValue(String.class);
                    tv.setText(playlistTitle);
                }
            }
        });

        return row;
    }
}