package uqac.dim.audium.activity.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import uqac.dim.audium.adapter.ListViewArtistAdapter;

public class ArtistList extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context c = this;
        setContentView(R.layout.activity_artist_list);
        ArrayList<Artist> l = new ArrayList<>();
        ListView artistListView = ((ListView) findViewById(R.id.artists_list));
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("artists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                l.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Artist a = snap.getValue(Artist.class);
                    if (a != null) {
                        l.add(a);
                    }
                }
                //artistListView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, l));
                artistListView.setAdapter(new ListViewArtistAdapter(l, c));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        artistListView.setOnItemClickListener((adapter, view, position, arg) -> {
            Intent intent = new Intent(ArtistList.this, ArtistProfile.class);
            intent.putExtra("artistId", ((Artist) artistListView.getItemAtPosition(position)).getId());
            intent.putExtra("username", getIntent().getStringExtra("username"));
            startActivity(intent);
        });
    }

    public void addArtist(View view) {
        Intent intent = new Intent(getApplicationContext(), AddArtist.class);
        startActivity(intent);
    }

}
