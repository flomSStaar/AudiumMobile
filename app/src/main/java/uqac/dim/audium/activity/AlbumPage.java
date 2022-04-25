package uqac.dim.audium.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.activity.admin.ModifyAlbum;
import uqac.dim.audium.activity.admin.TrackPage;
import uqac.dim.audium.adapter.ListViewTrackAdapter;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;

public class AlbumPage extends AppCompatActivity {
    private Album album;
    private Artist artist;
    private Long albumId;
    private String username;

    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private ImageView ivAlbum;
    private ListView listView;
    private ImageButton btnModify, btnDelete;
    private TextView tvAlbumName, tvAlbumArtistName, tvAlbumDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_page);
        albumId = getIntent().getLongExtra("albumId", 0);
        username = getIntent().getStringExtra("username");

        Context c = this;

        ivAlbum = findViewById(R.id.iv_album);
        listView = findViewById(R.id.lv_album_tracks);
        btnModify = findViewById(R.id.btn_modify_album);
        btnDelete = findViewById(R.id.btn_delete_album);
        tvAlbumName = findViewById(R.id.tv_album_name);
        tvAlbumArtistName = findViewById(R.id.tv_album_artist);
        tvAlbumDescription = findViewById(R.id.tv_album_description);

        listView.setOnItemClickListener(this::onTrackItemClicked);
        btnModify.setOnClickListener(this::modifyAlbum);
        btnDelete.setOnClickListener(this::deleteAlbum);
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        database.child("albums").child(String.valueOf(albumId)).get().addOnSuccessListener(albumSnapshot -> {
            album = albumSnapshot.getValue(Album.class);
            if (album != null) {
                tvAlbumName.setText(album.getTitle());
                tvAlbumDescription.setText(album.getDescription());
                Picasso.with(getApplicationContext()).load(album.getImageUrl()).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(ivAlbum);

                database.child("artists").child(album.getArtistId().toString()).get().addOnSuccessListener(artistSnapshot -> {
                    if (artistSnapshot.exists()) {
                        artist = artistSnapshot.getValue(Artist.class);
                        if (artist != null) {
                            tvAlbumArtistName.setText(artist.getPrintableName());
                        }
                    } else {
                        tvAlbumArtistName.setText(R.string.artist_name_error);
                    }
                });
            }
        });
        database.child("tracks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Track> tracks = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Track track = snap.getValue(Track.class);
                    if (track != null && track.getAlbumId() != null)
                        if (track.getAlbumId().equals(albumId)) {
                            tracks.add(track);
                        }
                }
                if (tracks.size() != 0) {
                    listView.setAdapter(new ListViewTrackAdapter(getApplicationContext(), tracks, username).setHasInfos(false));
                } else {
                    Toast.makeText(getApplicationContext(), "This album has no tracks", Toast.LENGTH_SHORT).show(); ///Techniquement impossible
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void modifyAlbum(View view) {
        if (username != null && albumId > 0) {
            Intent modifyIntent = new Intent(getApplicationContext(), ModifyAlbum.class);
            modifyIntent.putExtra("username", username);
            modifyIntent.putExtra("albumId", albumId);
            startActivity(modifyIntent);
        } else {
            Toast.makeText(getApplicationContext(), R.string.cannot_modify_album, Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAlbum(View view) {
        /// Supprimer les idAlbum des musiques
        database.child("albums").child(albumId.toString()).get().addOnSuccessListener(albumSnapshot -> {
            Album album = albumSnapshot.getValue(Album.class);
            if (album != null) {
                database.child("albums").child(String.valueOf(albumId)).removeValue();

                List<Long> tracksId = album.getTracksId();
                if (tracksId != null) {
                    database.child("tracks").get().addOnSuccessListener(trackSnapshot -> {
                        for (DataSnapshot snap1 : trackSnapshot.getChildren()) {
                            Track track = snap1.getValue(Track.class);
                            if (track != null && tracksId.contains(track.getId())) {
                                track.setAlbumId(null);
                                database.child("tracks").child(String.valueOf(track.getId())).setValue(track);
                                List<Long> artistAlbumsId = artist.getAlbumsId();
                                artistAlbumsId.remove(albumId);
                                database.child("artists").child(artist.getId().toString()).child("albumsId").setValue(artistAlbumsId);

                            }
                        }
                    });
                }
                finish();
            }
        });
    }

    private void onTrackItemClicked(AdapterView<?> adapterView, View view, int position, long l) {
        Track track = (Track) adapterView.getItemAtPosition(position);

        Intent intent = new Intent(AlbumPage.this, TrackPage.class);
        intent.putExtra("trackId", track.getId());
        intent.putExtra("albumId", track.getAlbumId());
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
