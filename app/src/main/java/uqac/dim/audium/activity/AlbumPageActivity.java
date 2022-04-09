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
import uqac.dim.audium.activity.admin.TrackListActivity;
import uqac.dim.audium.activity.admin.TrackPageActivity;
import uqac.dim.audium.firebase.FirebaseAlbum;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;

public class AlbumPageActivity extends AppCompatActivity {
    private Long albumId;
    private List<Long> tracksId;
    private Artist artist;
    private Album album;
    private DatabaseReference database;
    private EditText editTitle;
    private EditText editDescription;
    private EditText editArtist;
    private Button btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_page);
        albumId = getIntent().getLongExtra("albumId", 0);

        editTitle = (EditText) findViewById(R.id.edit_album_title);
        editDescription = (EditText) findViewById(R.id.edit_album_description);
        editArtist = (EditText) findViewById(R.id.edit_album_stagename);
        btnSave = (Button) findViewById(R.id.btn_save_album);
        btnSave.setVisibility(View.INVISIBLE);

        database = FirebaseDatabase.getInstance().getReference();
        database.child("albums").child(String.valueOf(albumId)).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                album = dataSnapshot.getValue(Album.class);
                if (album != null) {
                    editTitle.setText(album.getTitle());
                    editTitle.setEnabled(false);
                    editDescription.setText(album.getDescription());
                    editDescription.setEnabled(false);
                    editArtist.setText(album.getArtistId().toString());
                    editArtist.setEnabled(false);

                    database.child("artists/" + album.getArtistId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                                artist = dataSnapshot.getValue(Artist.class);
                            else {
                                // Verif a faire
                            }
                        }
                    });
                }
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
                    if (t != null && t.getAlbumId() != null)
                        if (t.getAlbumId().equals(albumId)) {
                            tracks.add(t);
                        }
                }
                if (tracks.size() != 0)
                    listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, tracks));
                else {
                    Toast.makeText(getApplicationContext(), "This album has no tracks", Toast.LENGTH_SHORT).show(); ///Techniquement impossible
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listView.setOnItemClickListener((adapter, view1, position, arg) -> {
            Intent intent = new Intent(AlbumPageActivity.this, TrackPageActivity.class);
            intent.putExtra("trackId", ((Track) listView.getItemAtPosition(position)).getId());
            intent.putExtra("albumId", ((Track) listView.getItemAtPosition(position)).getAlbumId());
            startActivity(intent);
        });


    }

    public void modifyAlbum(View view) {
        editTitle.setEnabled(true);
        editDescription.setEnabled(true);
        btnSave.setVisibility(View.VISIBLE);
    }

    public void saveAlbum(View view) {
        String newTitle = editTitle.getText().toString();
        String newDescription = editDescription.getText().toString();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseAlbum newAlbum = new FirebaseAlbum(albumId, newTitle, newDescription, album.getDescription(), album.getArtistId(), album.getTracksId());
        db.getReference("albums/").child(String.valueOf(albumId)).setValue(newAlbum);
        editTitle.setEnabled(false);
        editDescription.setEnabled(false);
        btnSave.setVisibility(View.INVISIBLE);
    }

    public void deleteAlbum(View view) {
        database = FirebaseDatabase.getInstance().getReference();

        /// Supprimer les idAlbum des musiques
        database.child("albums").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Album a = snap.getValue(Album.class);
                    if (a != null && a.getId().equals(albumId)) {
                        tracksId = a.getTracksId();
                        database.child("albums").child(String.valueOf(albumId)).removeValue();
                        database.child("tracks").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                    Track t = snap.getValue(Track.class);
                                    if(tracksId.contains(t.getId())){
                                        t.setAlbumId(null);
                                        database.child("tracks").child(String.valueOf(t.getId())).setValue(t);
                                        List<Long> artistAlbumsId = artist.getAlbumsId();
                                        artistAlbumsId.remove(albumId);
                                        database.child("artists").child(artist.getId().toString()).child("albumsId").setValue(artistAlbumsId);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        finish();
    }
}
