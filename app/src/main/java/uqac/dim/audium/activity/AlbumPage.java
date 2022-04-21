package uqac.dim.audium.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.activity.admin.TrackPage;
import uqac.dim.audium.firebase.FirebaseAlbum;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.view.adapter.ListViewTrackAdapter;

public class AlbumPage extends AppCompatActivity {
    private Long albumId;
    private String username;
    private List<Long> tracksId;
    private Artist artist;
    private Album album;
    private DatabaseReference database;
    private EditText editTitle;
    private EditText editDescription;
    private EditText editArtist;
    private Button btnSave;
    private ImageView imageView;
    private Button btnEdit;
    private Button btnDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_page);
        albumId = getIntent().getLongExtra("albumId", 0);
        username = getIntent().getStringExtra("username");

        Context c = this;

        editTitle = (EditText) findViewById(R.id.edit_album_title);
        editDescription = (EditText) findViewById(R.id.edit_album_description);
        editArtist = (EditText) findViewById(R.id.edit_album_stagename);
        btnSave = (Button) findViewById(R.id.btn_save_album);
        btnSave.setVisibility(View.INVISIBLE);
        btnSave.setOnClickListener(this::saveAlbum);
        btnEdit = (Button) findViewById(R.id.edit_album);
        btnEdit.setOnClickListener(this::modifyAlbum);
        btnDelete = (Button) findViewById(R.id.delete_album);
        btnDelete.setOnClickListener(this::deleteAlbum);
        imageView = findViewById(R.id.image_album);

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
                    Picasso.with(getApplicationContext()).load(album.getImagePath()).error(R.drawable.ic_notes).into(imageView);

                    database.child("artists/" + album.getArtistId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                                artist = dataSnapshot.getValue(Artist.class);
                            else {
                                //TODO A faire
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
                if (tracks.size() != 0) {
                    listView.setAdapter(new ListViewTrackAdapter(tracks, c, username));
                } else {
                    Toast.makeText(getApplicationContext(), "This album has no tracks", Toast.LENGTH_SHORT).show(); ///Techniquement impossible
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listView.setOnItemClickListener((adapter, view1, position, arg) -> {
            Intent intent = new Intent(AlbumPage.this, TrackPage.class);
            intent.putExtra("trackId", ((Track) listView.getItemAtPosition(position)).getId());
            intent.putExtra("albumId", ((Track) listView.getItemAtPosition(position)).getAlbumId());
            intent.putExtra("username", username);
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
        FirebaseAlbum newAlbum = new FirebaseAlbum(albumId, newTitle, newDescription, album.getImagePath(), album.getArtistId(), album.getTracksId());
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
                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                    Track t = snap.getValue(Track.class);
                                    if (tracksId.contains(t.getId())) {
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
