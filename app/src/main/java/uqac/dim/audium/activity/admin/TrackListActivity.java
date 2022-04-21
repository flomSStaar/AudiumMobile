package uqac.dim.audium.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.utils.ListViewTrackAdapter;

public class TrackListActivity extends AppCompatActivity {
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private String username;

    private ListView lvTracks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        lvTracks = ((ListView) findViewById(R.id.tracks_list));
        ((Button) findViewById(R.id.btn_add_track)).setOnClickListener(this::addTrack);
        lvTracks.setOnItemClickListener(this::onTrackClick);

        username = getIntent().getStringExtra("username");

        List<Track> tracks = new ArrayList<>();
        database.child("tracks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tracks.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Track t = snap.getValue(Track.class);
                    tracks.add(t);
                }
                lvTracks.setAdapter(new ListViewTrackAdapter(tracks, getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onTrackClick(AdapterView<?> adapterView, View view, int position, long arg) {
        Intent intent = new Intent(TrackListActivity.this, TrackPageActivity.class);
        Track track = (Track) lvTracks.getItemAtPosition(position);
        intent.putExtra("trackId", track.getId());
        intent.putExtra("albumId", track.getAlbumId());
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void addTrack(View view) {
        startActivity(new Intent(getApplicationContext(), AddTrackActivity.class));
    }
}
