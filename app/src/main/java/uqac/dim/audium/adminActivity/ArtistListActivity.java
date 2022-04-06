package uqac.dim.audium.adminActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import uqac.dim.audium.model.entity.Artist;

public class ArtistListActivity extends AppCompatActivity {

    DatabaseReference database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);
        ArrayList<Artist> l = new ArrayList<>();
        ListView artistListView = ((ListView) findViewById(R.id.artistList));
        database = FirebaseDatabase.getInstance().getReference();
        database.child("artists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                l.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Artist a = snap.getValue(Artist.class);
                    l.add(a);
                }
                artistListView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, l));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(ArtistListActivity.this, ArtistProfileActivity.class);
                intent.putExtra("id", ((Artist)artistListView.getItemAtPosition(position)).getId());
                startActivity(intent);
            }
        });
    }

    public void addArtists(View view) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("ids/lastArtistId").get()
                .addOnSuccessListener(dataSnapshot -> {
                    Long lastArtistId = dataSnapshot.getValue(Long.class);
                    if (lastArtistId != null) {
                        Artist artist = new Artist("t", "t", "ouioui", 10, lastArtistId);
                        db.getReference("artists/").child(String.valueOf(lastArtistId)).setValue(artist);
                        db.getReference("ids/lastArtistId").setValue(++lastArtistId);
                    }
                });
    }

}
