package uqac.dim.audium.activity.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import uqac.dim.audium.R;
import uqac.dim.audium.firebase.FirebaseTrack;
import uqac.dim.audium.firebase.FirebaseUtils;

public class AddTrackActivity extends AppCompatActivity {
    private Uri localFileUri;
    private final StorageReference storeRef = FirebaseStorage.getInstance().getReference(FirebaseUtils.TRACK_FILE_PATH);
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private TextView tvTrackPath;
    private EditText editTrackName;
    private EditText editImagePath;
    private EditText editArtistId;
    private EditText editAlbumId;
    private ActivityResultLauncher<String> activityResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        ((Button) findViewById(R.id.btn_choose_track_file)).setOnClickListener(this::selectFile);
        ((Button) findViewById(R.id.btn_add)).setOnClickListener(this::addTrack);

        tvTrackPath = findViewById(R.id.tv_track_path);
        editTrackName = findViewById(R.id.edit_track_name);
        editImagePath = findViewById(R.id.edit_image_path);
        editArtistId = findViewById(R.id.edit_artist_id);
        editAlbumId = findViewById(R.id.edit_album_id);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::getResult);
    }

    private void selectFile(View view) {
        activityResultLauncher.launch("audio/*");
    }

    private void addTrack(View view) {
        //TODO Faire les verifications
        String trackName = editTrackName.getText().toString();
        String imagePath = editImagePath.getText().toString();
        Long artistId = Long.valueOf(editArtistId.getText().toString());
        Long albumId = Long.valueOf(editAlbumId.getText().toString());
        if (localFileUri != null) {
            dbRef.child("ids/lastTrackId").get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            Long trackId = dataSnapshot.getValue(Long.class);
                            if (trackId != null) {
                                StorageReference ref = storeRef.child(trackId.toString());

                                ref.putFile(localFileUri)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            Log.i("DIM", "Track file " + trackId + " has been uploaded");

                                            ref.getDownloadUrl()
                                                    .addOnSuccessListener(uri -> {
                                                        FirebaseTrack track = new FirebaseTrack(trackId,
                                                                trackName, uri.toString(), imagePath, artistId, albumId);

                                                        dbRef.child("tracks").child(trackId.toString()).setValue(track);
                                                        dbRef.child("ids/lastTrackId").setValue(trackId + 1);
                                                        Log.i("DIM", "track " + trackId + " has been added in database");
                                                        Toast.makeText(getApplicationContext(), "Track has been added successfully", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(getApplicationContext(), TrackListActivity.class));
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e("DIM", e.getMessage());
                                                    });
                                        })
                                        .addOnProgressListener(snapshot -> {
                                            //Progress bar ??
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.i("DIM", "File upload error");
                                            Toast.makeText(getApplicationContext(), "File upload error", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    });
        }
    }

    private void getResult(Uri uri) {
        if (uri != null) {
            localFileUri = uri;
            tvTrackPath.setText(uri.getPath());
        }
    }
}
