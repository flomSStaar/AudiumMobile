package uqac.dim.audium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.activity.admin.TrackPageActivity;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.Track;

public class PlaylistPageActivity extends AppCompatActivity {

    protected String username;
    protected Playlist playlist;
    protected List<Track> tracks;
    private EditText editTitle;
    private EditText editDescription;
    private Button btnSave;
    private DatabaseReference database;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_page);
        editTitle = (EditText) findViewById(R.id.edit_playlist_title);
        editDescription = (EditText) findViewById(R.id.edit_playlist_description);
        btnSave = (Button) findViewById(R.id.btn_save_playlist);
        btnSave.setVisibility(View.INVISIBLE);

        username = getIntent().getStringExtra("username");
        Long playlistId = getIntent().getLongExtra("playlistId",0);

        database = FirebaseDatabase.getInstance().getReference();
        database.child("playlists").child(username).child(String.valueOf(playlistId)).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    playlist = dataSnapshot.getValue(Playlist.class);
                    editTitle.setText(playlist.getTitle());
                    editTitle.setEnabled(false);
                    editDescription.setText(playlist.getDescription());
                    editDescription.setEnabled(false);
                }
            }
        });

        ListView listView = ((ListView) findViewById(R.id.playlist_page_tracks));
        ArrayList<Track> tracks = new ArrayList<>();

        database.child("tracks").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                tracks.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Track t = snap.getValue(Track.class);
                    if (t != null)
                        if (playlist.getTracksId().contains(t.getId())) {
                            tracks.add(t);
                        }
                }
                if (tracks.size() != 0)
                    listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, tracks));
                else {
                    Toast.makeText(getApplicationContext(), "This playlist has no tracks", Toast.LENGTH_SHORT).show(); ///Techniquement impossible
                }
            }
        });

        listView.setOnItemClickListener((adapter, view1, position, arg) -> {
            Intent intent = new Intent(PlaylistPageActivity.this, TrackPageActivity.class);
            intent.putExtra("trackId", ((Track) listView.getItemAtPosition(position)).getId());
            intent.putExtra("albumId", ((Track) listView.getItemAtPosition(position)).getAlbumId());
            startActivity(intent);
        });

    }

    public void modifyPlaylist(View view) {
    }

    public void deletePlaylist(View view) {
    }
}
