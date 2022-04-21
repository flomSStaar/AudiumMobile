package uqac.dim.audium.activity.admin;

import android.content.Intent;
import android.net.Uri;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.activity.chooser.AlbumChooser;
import uqac.dim.audium.activity.chooser.ArtistChooser;
import uqac.dim.audium.firebase.FirebaseTrack;
import uqac.dim.audium.firebase.FirebaseUtils;

public class AddTrack extends AppCompatActivity {
    private final StorageReference storeRef = FirebaseStorage.getInstance().getReference();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private TextView tvTrackPath;
    private TextView tvArtist;
    private TextView tvAlbum;
    private EditText editTrackName;
    private ImageView editImagePath;

    private ActivityResultLauncher<String> trackResultLauncher;
    private ActivityResultLauncher<String> imageResultLauncher;
    private ActivityResultLauncher<Intent> artistResultLauncher;
    private ActivityResultLauncher<Intent> albumResultLauncher;

    private Uri localFileUri;
    private Uri localFileImageUri;
    private Long artistId;
    private String artistName;
    private Long albumId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        ((Button) findViewById(R.id.btn_add)).setOnClickListener(this::addTrack);
        ((Button) findViewById(R.id.btn_choose_track_file)).setOnClickListener(this::selectFile);
        ((Button) findViewById(R.id.btn_choose_image_file)).setOnClickListener(this::selectImage);
        ((Button) findViewById(R.id.btn_choose_artist)).setOnClickListener(this::chooseArtist);
        ((Button) findViewById(R.id.btn_choose_album)).setOnClickListener(this::chooseAlbum);

        tvTrackPath = findViewById(R.id.tv_track_path);
        editTrackName = findViewById(R.id.edit_track_name);
        editImagePath = findViewById(R.id.edit_image_path);
        tvArtist = findViewById(R.id.tv_artist_id);
        tvAlbum = findViewById(R.id.tv_album_id);

        trackResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::getTrackResult);
        imageResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::getImageResult);
        artistResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::getArtistResult);
        albumResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::getAlbumResult);
    }

    private void selectFile(View view) {
        trackResultLauncher.launch("audio/*");
    }

    private void selectImage(View view) {
        imageResultLauncher.launch("image/*");
    }

    private void chooseArtist(View view) {
        Intent intent = new Intent(getApplicationContext(), ArtistChooser.class);
        artistResultLauncher.launch(intent);
    }

    private void chooseAlbum(View view) {
        if (artistId != null && artistName != null) {
            Intent intent = new Intent(getApplicationContext(), AlbumChooser.class);
            intent.putExtra("artistId", artistId);
            intent.putExtra("artistName", artistName);
            albumResultLauncher.launch(intent);
        } else {
            Toast.makeText(getApplicationContext(), "You have to choose the artist before", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTrack(View view) {
        String trackName = editTrackName.getText().toString();


        if (localFileImageUri != null && localFileUri != null && artistId != null && !trackName.trim().isEmpty()) {
            dbRef.child("ids/lastTrackId").get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            Long trackId = dataSnapshot.getValue(Long.class);
                            if (trackId != null) {

                                StorageReference imageRef = storeRef.child(FirebaseUtils.TRACK_IMAGE_FILE_PATH).child(trackId.toString());
                                imageRef.putFile(localFileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri imageUri) {
                                                StorageReference trackRef = storeRef.child(FirebaseUtils.TRACK_FILE_PATH).child(trackId.toString());
                                                trackRef.putFile(localFileUri)
                                                        .addOnSuccessListener(taskSnapshot -> {
                                                            Log.i("DIM", "Track file " + trackId + " has been uploaded");

                                                            trackRef.getDownloadUrl()
                                                                    .addOnSuccessListener(trackUri -> {
                                                                        List<Long> playlistsId = new ArrayList<>();
                                                                        FirebaseTrack track = new FirebaseTrack(trackId, trackName.trim(), trackUri.toString(), imageUri.toString(), artistId, albumId, playlistsId);

                                                                        dbRef.child("tracks").child(trackId.toString()).setValue(track);
                                                                        dbRef.child("ids/lastTrackId").setValue(trackId + 1);
                                                                        dbRef.child("artists/" + artistId + "/tracksId").get()
                                                                                .addOnSuccessListener(dataSnapshot1 -> {
                                                                                    long childrenCount = dataSnapshot1.getChildrenCount();
                                                                                    dbRef.child("artists/" + artistId + "/tracksId/" + childrenCount).setValue(trackId);
                                                                                });
                                                                        if (albumId != null) {
                                                                            dbRef.child("albums/" + albumId + "/tracksId").get()
                                                                                    .addOnSuccessListener(dataSnapshot1 -> {
                                                                                        long childrenCount = dataSnapshot1.getChildrenCount();
                                                                                        dbRef.child("albums/" + albumId + "/tracksId/" + childrenCount).setValue(trackId);
                                                                                    });
                                                                        }

                                                                        Log.i("DIM", "track " + trackId + " has been added in database");
                                                                        Toast.makeText(getApplicationContext(), "Track has been added successfully", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(getApplicationContext(), TrackList.class));
                                                                        finish();
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                                                                        Log.e("DIM", "Cannot get download url");
                                                                    });
                                                        })
                                                        .addOnProgressListener(snapshot -> {
                                                            //Progress bar ??
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e("DIM", "Cannot upload file");
                                                            Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                                                        });
                                            }
                                        });

                                    }
                                });
                            }
                        }
                    });
        }
    }


    private void getTrackResult(Uri uri) {
        if (uri != null) {
            localFileUri = uri;
            tvTrackPath.setText(uri.getPath());
        }
    }

    private void getImageResult(Uri uri) {
        if (uri != null) {
            localFileImageUri = uri;
            Picasso.with(this).load(localFileImageUri).error(R.drawable.ic_notes).into(editImagePath);


        }
    }

    private void getArtistResult(ActivityResult activityResult) {
        try {
            if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() != null) {
                Bundle extras = activityResult.getData().getExtras();
                long newArtistId = extras.getLong("artistId");
                String newArtistName = extras.getString("artistName");
                if (newArtistId != 0 && newArtistName != null) {
                    artistId = newArtistId;
                    artistName = newArtistName;
                    tvArtist.setText(artistName + " (" + artistId + ")");
                }
            } else if (activityResult.getResultCode() == RESULT_CANCELED) {
                if (activityResult.getData() != null && activityResult.getData().hasExtra("error")) {
                    Log.e("DIM", "An error occured with the artist chooser");
                    Toast.makeText(getApplicationContext(), "An error occured with the artist chooser", Toast.LENGTH_SHORT).show();
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DIM", "An error occured with the artist chooser");
            Toast.makeText(getApplicationContext(), "An error occured with the artist chooser", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAlbumResult(ActivityResult activityResult) {
        try {
            if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() != null) {
                Bundle extras = activityResult.getData().getExtras();
                long newAlbumId = extras.getLong("albumId");
                String albumName = extras.getString("albumName");
                if (newAlbumId != 0 && albumName != null) {
                    albumId = newAlbumId;
                    tvAlbum.setText(albumName + "(" + albumId + ")");
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
}
