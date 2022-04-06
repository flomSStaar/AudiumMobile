package uqac.dim.audium.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Artist;

public class ArtistProfileActivity extends AppCompatActivity {

    DatabaseReference database;
    Artist artist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_profile);

        ((Button) findViewById(R.id.btn_delete_artist)).setOnClickListener(this::deleteArtist);

        Intent i = getIntent();
        database = FirebaseDatabase.getInstance().getReference();
        database.child("artists").child(String.valueOf(i.getLongExtra("id", 0))).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                artist = snapshot.getValue(Artist.class);
                ((TextView) findViewById(R.id.tv_stage_name)).setText(artist.getStageName());
                ((TextView) findViewById(R.id.tv_artist_first_name)).setText(artist.getFirstName());
                ((TextView) findViewById(R.id.tv_artist_last_name)).setText(artist.getLastName());
                ((TextView) findViewById(R.id.tv_artist_age)).setText(String.valueOf(artist.getAge()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deleteArtist(View view) {
        database = FirebaseDatabase.getInstance().getReference();
        database.child("artists").child(String.valueOf(artist.getId())).removeValue();
        artist = null;
    }
}
