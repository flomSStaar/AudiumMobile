package uqac.dim.audium.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;

public class AlbumPageActivity extends AppCompatActivity {

    protected Long albumId;
    protected Album album;
    DatabaseReference database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_page);
        albumId=getIntent().getLongExtra("idAlbum",0);

        database = FirebaseDatabase.getInstance().getReference();
        database.child("albums").child(String.valueOf(albumId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                album = snapshot.getValue(Album.class);
                if(album!=null) {
                    ((TextView) findViewById(R.id.album_page_title)).setText(album.getTitle());
                    ((TextView) findViewById(R.id.album_page_description)).setText(album.getDescription());
                    ((TextView) findViewById(R.id.album_page_stagename)).setText(album.getArtistId().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ListView listView = ((ListView) findViewById(R.id.album_page_tracks));
        ArrayList<Track> tracks = new ArrayList<>();
        database.child("tracks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tracks.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Track t = snap.getValue(Track.class);
                    if(t.getAlbums().contains(albumId)) {
                        tracks.add(t);
                    }
                }
                if(tracks.size()!=0)
                    listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, tracks));
                else{
                    Toast.makeText(getApplicationContext(), "This album has no tracks", Toast.LENGTH_SHORT).show(); ///Techniquement impossible
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
