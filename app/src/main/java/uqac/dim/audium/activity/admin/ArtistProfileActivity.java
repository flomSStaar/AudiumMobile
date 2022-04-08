package uqac.dim.audium.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import uqac.dim.audium.activity.AlbumPageActivity;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.entity.User;

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

    public void showAlbums(View view) {
        ListView listView = ((ListView) findViewById(R.id.artist_albums_tracks_list));

        ArrayList<Album> albums = new ArrayList<>();


        database = FirebaseDatabase.getInstance().getReference();
        database.child("albums").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                albums.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Album a = snap.getValue(Album.class);
                    if(a.getArtistId().equals(artist.getId()))
                        albums.add(a);
                }
                if(albums.size()!=0)
                    listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, albums));
                else{
                    Toast.makeText(getApplicationContext(), "This artist has no albums", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(ArtistProfileActivity.this, AlbumPageActivity.class);
                intent.putExtra("idAlbum", ((Album) listView.getItemAtPosition(position)).getId());
                startActivity(intent);
            }
        });


    }

    public void showMusics(View view) {
        ListView listView = ((ListView) findViewById(R.id.artist_albums_tracks_list));
        ArrayList<Track> tracks = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference();
        database.child("tracks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tracks.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Track t = snap.getValue(Track.class);
                    if(t.getArtist().equals(artist.getId()))
                        tracks.add(t);
                }
                if(tracks.size()!=0)
                    listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, tracks));
                else{
                    Toast.makeText(getApplicationContext(), "This artist has no tracks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addAlbum(View view) {
        Intent intent = new Intent(getApplicationContext(), AddAlbumActivity.class);
        intent.putExtra("id",artist.getId());
        startActivity(intent);
    }
}
