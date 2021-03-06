package uqac.dim.audium.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.activity.chooser.AlbumChooser;
import uqac.dim.audium.activity.chooser.PlaylistChooser;
import uqac.dim.audium.firebase.FirebaseTrack;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.Track;

public class TrackPage extends AppCompatActivity {

    protected Track track;
    protected String username;
    protected Long trackId;
    protected Album album;
    protected Long albumId;
    protected Artist artist;
    private DatabaseReference database;
    private EditText editName;
    private EditText editArtist;
    private TextView editAlbum;
    private Button btnSave;
    private Button btnChangeAlbum;
    private Button btnModify;
    private Button btnDelete;
    private Button btnAddPlaylist;
    private ImageView imageView;
    private ActivityResultLauncher<Intent> albumResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_page);
        database = FirebaseDatabase.getInstance().getReference();

        albumResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::getAlbumResult);
        trackId = getIntent().getLongExtra("trackId", 0);
        albumId = getIntent().getLongExtra("albumId", 0);
        username = getIntent().getStringExtra("username");
        if (albumId == 0)
            albumId = null;

        if (albumId != null) {
            database.child("albums/" + albumId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Album a = dataSnapshot.getValue(Album.class);
                        if (a != null && a.getTracksId() != null && a.getTracksId().contains(trackId)) {
                            album = a;
                        }
                    }
                }
            });
        }

        editName = (EditText) findViewById(R.id.edit_track_name);
        editAlbum = (TextView) findViewById(R.id.track_page_album);
        editArtist = (EditText) findViewById(R.id.edit_track_artist);
        imageView = findViewById(R.id.track_image);



        editName.setEnabled(false);
        editAlbum.setEnabled(false);
        editArtist.setEnabled(false);


        btnSave = (Button) findViewById(R.id.btn_save_track);
        btnSave.setVisibility(View.INVISIBLE);
        btnSave.setOnClickListener(this::saveTrack);
        btnChangeAlbum = (Button) findViewById(R.id.btn_choose_album_track_page);
        btnChangeAlbum.setVisibility(View.INVISIBLE);
        btnChangeAlbum.setOnClickListener(this::addAlbum);

        btnModify = (Button) findViewById(R.id.modifyTrack);
        btnModify.setOnClickListener(this::modifyTrack);

        btnDelete= (Button) findViewById(R.id.btn_delete_track);
        btnDelete.setOnClickListener(this::deleteTrack);

        btnAddPlaylist = (Button) findViewById(R.id.btn_add_to_playlist);
        btnAddPlaylist.setOnClickListener(this::addTrack);

        database.child("tracks").child(String.valueOf(trackId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                track = snapshot.getValue(Track.class);
                if (track != null) {
                    editName.setText(track.getName());
                    Picasso.with(getApplicationContext()).load(track.getImageUrl()).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(imageView);
                    if (track.getAlbumId() == null)
                        editAlbum.setText("Not in an album");
                    else {
                        albumId = track.getAlbumId();
                        editAlbum.setText(albumId.toString());
                    }

                    database.child("artists").child(track.getArtistId().toString()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            artist = dataSnapshot.getValue(Artist.class);
                            if (artist != null) {
                                editArtist.setText(artist.getStageName());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void modifyTrack(View view) {
        btnSave.setVisibility(View.VISIBLE);
        if (track.getAlbumId() == null)
            btnChangeAlbum.setVisibility(View.VISIBLE);
        editName.setEnabled(true);
    }


    public void saveTrack(View view) {
        String newName = editName.getText().toString();


        database = FirebaseDatabase.getInstance().getReference();
        FirebaseTrack newTrack = new FirebaseTrack(trackId, newName, track.getUrl(), track.getUrl(), artist.getId(), albumId, track.getPlaylistsId());
        database.child("tracks").child(String.valueOf(trackId)).setValue(newTrack);
        database.child("albums").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Album a = snap.getValue(Album.class);
                    if (a != null && a.getId().equals(albumId)) {
                        album = a;
                        if (albumId != null) {
                            album.getTracksId().add(newTrack.getId());
                            database.child("albums").child(albumId.toString()).child("tracksId").setValue(album.getTracksId());
                        }
                        editName.setEnabled(false);
                        btnSave.setVisibility(View.INVISIBLE);
                        btnChangeAlbum.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void getAlbumResult(ActivityResult activityResult) {
        try {
            if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() != null) {
                Bundle extras = activityResult.getData().getExtras();
                long newAlbumId = extras.getLong("albumId");
                String albumName = extras.getString("albumName");
                if (newAlbumId != 0 && albumName != null) {
                    albumId = newAlbumId;
                    editAlbum.setText(albumName + "(" + albumId + ")");
                }
                Log.i("DIM", "Get the albumId " + albumId);
            } else if (activityResult.getResultCode() == RESULT_CANCELED) {
                if (activityResult.getData() != null && activityResult.getData().hasExtra("error")) {
                    Log.e("DIM", "An error occured with the album chooser");
                    Toast.makeText(getApplicationContext(), "An error occured with the album chooser", Toast.LENGTH_SHORT).show();
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DIM", "Cannot get the album id from AlbumChooser");
        }
    }

    public void addAlbum(View view) {
        Intent intent = new Intent(getApplicationContext(), AlbumChooser.class);
        intent.putExtra("artistId", track.getArtistId());
        intent.putExtra("artistName", artist.getStageName());
        albumResultLauncher.launch(intent);
    }

    public void deleteTrack(View view) {
        database = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReferenceFromUrl(track.getUrl());
        ref.delete();
        //Remove les ids des tracks des albums
        if (album != null) {
            List<Long> tracksId = album.getTracksId();
            tracksId.remove(trackId);
            database.child("albums").child(albumId.toString()).child("tracksId").setValue(tracksId);
        }
        List<Long> artistsTracksId = artist.getTracksId();
        artistsTracksId.remove(trackId);
        database.child("artists").child(artist.getId().toString()).child("tracksId").setValue(artistsTracksId);
        database.child("tracks").child(String.valueOf(trackId)).removeValue();

        List<Long> playlistsId = track.getPlaylistsId();
        Long id = track.getId();
        database.child("playlists").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                        for (DataSnapshot playlist : snap.getChildren()) {
                            Playlist p = playlist.getValue(Playlist.class);
                            if (playlistsId.contains(p.getId())) {
                                p.getTracksId().remove(id);
                                if (!p.getTracksId().isEmpty())
                                    database.child("playlists").child(snap.getKey()).child(String.valueOf(p.getId())).child("tracksId").setValue(p.getTracksId());
                                else
                                    database.child("playlists").child(snap.getKey()).child(String.valueOf(p.getId())).removeValue();
                            }
                        }
                    }
                }
            }
        });
        // Si derniere, supprimmer la playlist

        trackId = null;
        finish();
    }

    public void addTrack(View view) {
        Intent intent = new Intent(getApplicationContext(), PlaylistChooser.class);
        intent.putExtra("username", username);
        intent.putExtra("trackId",trackId);
        startActivity(intent);
    }
}
