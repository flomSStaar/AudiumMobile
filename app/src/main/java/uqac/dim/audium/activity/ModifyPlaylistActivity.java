package uqac.dim.audium.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.firebase.FirebasePlaylist;
import uqac.dim.audium.firebase.FirebaseUtils;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.utils.ListViewTrackAdapter;

public class ModifyPlaylistActivity extends AppCompatActivity {
    private EditText editTitle, editDescription;
    private ImageView ivPlaylist;
    private ListView listPlaylistTracks;

    private String username;
    private Playlist playlist;
    private final List<Track> playlistTracks = new ArrayList<>();
    private Uri localFileImageUri;

    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private final StorageReference storage = FirebaseStorage.getInstance().getReference();
    private ActivityResultLauncher<String> imageResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_modify);

        ImageButton btnSave = findViewById(R.id.btn_save_playlist);
        ImageButton btnBack = findViewById(R.id.btn_back);
        editTitle = findViewById(R.id.edit_playlist_title);
        editDescription = findViewById(R.id.edit_playlist_description);
        LinearLayout layoutChangeImage = findViewById(R.id.layout_change_image);
        ivPlaylist = findViewById(R.id.iv_playlist);
        listPlaylistTracks = findViewById(R.id.list_playlist_tracks);

        btnSave.setOnClickListener(this::savePlaylist);
        btnBack.setOnClickListener(this::back);
        layoutChangeImage.setOnClickListener(this::changeImage);

        imageResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::getImageResult);
        load();
    }

    private void load() {
        username = getIntent().getStringExtra("username");
        Long playlistId = getIntent().getLongExtra("playlistId", -1);
        //TODO verification

        if (username == null || playlistId <= 0) {
            finish();
            Toast.makeText(this, getString(R.string.cannot_modify_playlist), Toast.LENGTH_SHORT).show();
            return;
        }

        database.child("playlists").child(username).child(String.valueOf(playlistId)).get().addOnSuccessListener(playlistSnapshot -> {
            if (playlistSnapshot.exists()) {
                playlist = playlistSnapshot.getValue(Playlist.class);
                if (playlist != null) {
                    editTitle.setText(playlist.getTitle());
                    editDescription.setText(playlist.getDescription());
                    Picasso.with(getApplicationContext()).load(playlist.getImageUrl()).error(R.drawable.ic_notes).into(ivPlaylist);

                    database.child("tracks").get().addOnSuccessListener(trackSnapshot -> {
                        playlistTracks.clear();
                        for (DataSnapshot snap : trackSnapshot.getChildren()) {
                            Track t = snap.getValue(Track.class);
                            if (t != null)
                                if (playlist.getTracksId().contains(t.getId())) {
                                    playlistTracks.add(t);
                                }
                        }
                        if (playlistTracks.size() != 0)
                            listPlaylistTracks.setAdapter(new ListViewTrackAdapter(playlistTracks, getApplicationContext(), username));
                        else {
                            Toast.makeText(getApplicationContext(), getString(R.string.playlist_has_no_track), Toast.LENGTH_SHORT).show(); ///Techniquement impossible
                        }
                    });
                } else {
                    finish();
                    Toast.makeText(this, getString(R.string.cannot_modify_playlist), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savePlaylist(View view) {
        String newTitle = editTitle.getText().toString();
        String newDescription = editDescription.getText().toString();

        if (newTitle.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_title), Toast.LENGTH_SHORT).show();
            return;
        }

        if (localFileImageUri != null) {
            StorageReference imageRef = storage.child(FirebaseUtils.PLAYLIST_IMAGE_FILE_PATH).child(String.valueOf(playlist.getId()));
            imageRef.putFile(localFileImageUri).addOnSuccessListener(taskSnapshot -> {
                if (URLUtil.isValidUrl(playlist.getImageUrl())) {
                    FirebasePlaylist newPlaylist = new FirebasePlaylist(playlist.getId(), username, newTitle.trim(), newDescription.trim(), playlist.getTracksId(), playlist.getImageUrl());
                    database.child("playlists")
                            .child(username)
                            .child(String.valueOf(playlist.getId()))
                            .setValue(newPlaylist)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, getString(R.string.update_successsful), Toast.LENGTH_SHORT).show();
                                finish();
                            });
                } else {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        FirebasePlaylist newPlaylist = new FirebasePlaylist(playlist.getId(), username, newTitle.trim(), newDescription.trim(), playlist.getTracksId(), uri.toString());
                        database.child("playlists")
                                .child(username)
                                .child(String.valueOf(playlist.getId()))
                                .setValue(newPlaylist)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, getString(R.string.update_successsful), Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                    });
                }
            });
        } else {
            FirebasePlaylist newPlaylist = new FirebasePlaylist(playlist.getId(), username, newTitle.trim(), newDescription.trim(), playlist.getTracksId(), playlist.getImageUrl());
            database.child("playlists")
                    .child(username)
                    .child(String.valueOf(playlist.getId()))
                    .setValue(newPlaylist)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, getString(R.string.update_successsful), Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }

    }

    private void back(View view) {
        finish();
    }

    private void changeImage(View view) {
        imageResultLauncher.launch("image/*");
    }

    private void getImageResult(Uri uri) {
        if (uri != null) {
            localFileImageUri = uri;
            Picasso.with(getApplicationContext()).load(localFileImageUri).error(R.drawable.ic_notes).into(ivPlaylist);
        }
    }
}
