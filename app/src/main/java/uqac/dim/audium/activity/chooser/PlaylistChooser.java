package uqac.dim.audium.activity.chooser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.activity.admin.ArtistListActivity;
import uqac.dim.audium.activity.admin.ArtistProfileActivity;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.Track;

public class PlaylistChooser extends AppCompatActivity {

    private ListView listView;
    private String username;
    private Long trackId;
    private List<Playlist> playlists;
    private DatabaseReference database;
    private TextView tv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_chooser);
        username = getIntent().getStringExtra("username");
        trackId = getIntent().getLongExtra("trackId",0);
        tv = findViewById(R.id.playlist_list_title);

        database = FirebaseDatabase.getInstance().getReference();

        listView = findViewById(R.id.list_playlists);
        loadPlaylists();

        listView.setOnItemClickListener((adapter, view, position, arg) -> {
            Playlist playlist = (Playlist) listView.getItemAtPosition(position);

            // Add de l'id dans la playlist
            database.child("playlists").child(username).child(String.valueOf(playlist.getId())).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        Playlist p = dataSnapshot.getValue(Playlist.class);
                        p.getTracksId().add(trackId);
                        database.child("playlists").child(username).child(String.valueOf(p.getId())).child("tracksId").setValue(p.getTracksId());
                    }
                }
            });
            //Add de l'id dans la track;

            database.child("tracks").child(String.valueOf(trackId)).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Track t = dataSnapshot.getValue(Track.class);
                        if(t.getPlaylistsId() !=null) {
                            t.getPlaylistsId().add(playlist.getId());
                            database.child("tracks").child(String.valueOf(trackId)).child("playlistsId").setValue(t.getPlaylistsId());
                        } else{
                            List<Long> list = new ArrayList<>();
                            list.add(playlist.getId());
                            database.child("tracks").child(String.valueOf(trackId)).child("playlistsId").setValue(list);

                        }
                    }
                }
            });

            finish();
        });
    }

    private void loadPlaylists() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("playlists").child(username).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        List<Playlist> playlists = new ArrayList<>();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            Playlist p = d.getValue(Playlist.class);
                            if (p != null && !p.getTracksId().contains(trackId)) {
                                playlists.add(p);
                            }
                            if (playlists.isEmpty())
                                tv.setText("No playlists");
                        }
                        listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, playlists));
                    }
                });
            }
}

