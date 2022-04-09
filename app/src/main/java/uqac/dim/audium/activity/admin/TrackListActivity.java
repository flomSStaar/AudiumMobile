package uqac.dim.audium.activity.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Track;

public class TrackListActivity extends AppCompatActivity {
    private DatabaseReference database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        ArrayList<Track> tracks = new ArrayList<>();
        ListView artistListView = ((ListView) findViewById(R.id.tracks_list));
        database = FirebaseDatabase.getInstance().getReference();
        database.child("tracks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tracks.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Track t = snap.getValue(Track.class);
                    tracks.add(t);
                }
                artistListView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, tracks));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addTracks(View view) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("ids/lastTrackId").get()
                .addOnSuccessListener(dataSnapshot -> {
                    Long lastTrackId = dataSnapshot.getValue(Long.class);
                    if (lastTrackId != null) {
                        Track track = new Track("Track" + lastTrackId, 16L, lastTrackId);
                        db.getReference("tracks/").child(String.valueOf(lastTrackId)).setValue(track);
                        db.getReference("ids/lastTrackId").setValue(++lastTrackId);
                    }
                });
    }
}
