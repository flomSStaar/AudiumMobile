package uqac.dim.audium.activity.admin;

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
import uqac.dim.audium.adapter.ListViewTrackAdapter;
import uqac.dim.audium.firebase.FirebaseAlbum;
import uqac.dim.audium.firebase.FirebaseUtils;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Track;

public class ModifyAlbum extends AppCompatActivity {
    private EditText editTitle, editDescription;
    private ImageView ivAlbum;
    private ListView lvAlbumTracks;

    private String username;
    private Album album;
    private Uri localFileImageUri;

    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private final StorageReference storage = FirebaseStorage.getInstance().getReference();
    private ActivityResultLauncher<String> imageResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_modify);

        ImageButton btnSave = findViewById(R.id.btn_save_album);
        ImageButton btnBack = findViewById(R.id.btn_back);
        editTitle = findViewById(R.id.edit_album_title);
        editDescription = findViewById(R.id.edit_album_description);
        LinearLayout layoutChangeImage = findViewById(R.id.layout_change_image);
        ivAlbum = findViewById(R.id.iv_album);
        lvAlbumTracks = findViewById(R.id.lv_album_tracks);

        btnSave.setOnClickListener(this::saveAlbum);
        btnBack.setOnClickListener(this::back);
        layoutChangeImage.setOnClickListener(this::changeImage);

        imageResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::getImageResult);
        load();
    }

    private void load() {
        username = getIntent().getStringExtra("username");
        Long albumId = getIntent().getLongExtra("albumId", -1);
        //TODO verification

        if (username == null || albumId <= 0) {
            finish();
            Toast.makeText(this, getString(R.string.cannot_modify_album), Toast.LENGTH_SHORT).show();
            return;
        }

        database.child("albums").child(String.valueOf(albumId)).get().addOnSuccessListener(albumSnapshot -> {
            if (albumSnapshot.exists()) {
                album = albumSnapshot.getValue(Album.class);
                if (album != null) {
                    editTitle.setText(album.getTitle());
                    editDescription.setText(album.getDescription());
                    Picasso.with(getApplicationContext()).load(album.getImageUrl()).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(ivAlbum);

                    database.child("tracks").get().addOnSuccessListener(trackSnapshot -> {
                        List<Track> albumTracks = new ArrayList<>();
                        for (DataSnapshot snap : trackSnapshot.getChildren()) {
                            Track t = snap.getValue(Track.class);
                            if (t != null)
                                if (album.getTracksId().contains(t.getId())) {
                                    albumTracks.add(t);
                                }
                        }
                        if (albumTracks.size() != 0)
                            lvAlbumTracks.setAdapter(new ListViewTrackAdapter(getApplicationContext(), albumTracks, username).setHasInfos(false));
                        else {
                            Toast.makeText(getApplicationContext(), getString(R.string.album_has_no_track), Toast.LENGTH_SHORT).show(); ///Techniquement impossible
                        }
                    });
                } else {
                    finish();
                    Toast.makeText(this, getString(R.string.cannot_modify_album), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveAlbum(View view) {
        String newTitle = editTitle.getText().toString();
        String newDescription = editDescription.getText().toString();

        if (newTitle.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_title), Toast.LENGTH_SHORT).show();
            return;
        }

        if (localFileImageUri != null) {
            StorageReference imageRef = storage.child(FirebaseUtils.ALBUM_IMAGE_FILE_PATH).child(String.valueOf(album.getId()));
            imageRef.putFile(localFileImageUri).addOnSuccessListener(taskSnapshot -> {
                if (URLUtil.isValidUrl(album.getImageUrl())) {
                    FirebaseAlbum newAlbum = new FirebaseAlbum(album.getId(), newTitle.trim(), newDescription.trim(), album.getImageUrl(), album.getArtistId(), album.getTracksId());
                    database.child("albums")
                            .child(String.valueOf(album.getId()))
                            .setValue(newAlbum)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, getString(R.string.update_successsful), Toast.LENGTH_SHORT).show();
                                finish();
                            });
                } else {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        FirebaseAlbum newAlbum = new FirebaseAlbum(album.getId(), newTitle.trim(), newDescription.trim(), album.getImageUrl(), album.getArtistId(), album.getTracksId());
                        database.child("albums")
                                .child(String.valueOf(album.getId()))
                                .setValue(newAlbum)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, getString(R.string.update_successsful), Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                    });
                }
            });
        } else {
            FirebaseAlbum newAlbum = new FirebaseAlbum(album.getId(), newTitle.trim(), newDescription.trim(), album.getImageUrl(), album.getArtistId(), album.getTracksId());
            database.child("albums")
                    .child(String.valueOf(album.getId()))
                    .setValue(newAlbum)
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
            Picasso.with(getApplicationContext()).load(localFileImageUri).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(ivAlbum);
        }
    }
}
