package uqac.dim.audium.activity.admin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.firebase.FirebaseAlbum;
import uqac.dim.audium.firebase.FirebaseUtils;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.adapter.ListViewAdapter;

public class AddAlbum extends AppCompatActivity {
    private final StorageReference storeRef = FirebaseStorage.getInstance().getReference();
    private Long artistId;
    private Artist artist;
    private ListViewAdapter adapter;
    private DatabaseReference database;
    public static List<Long> idTracksSelected;
    private Button chooseImg;
    private ImageView image;
    private Uri localFileImageUri;
    private ActivityResultLauncher<String> imageResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context c = this;
        setContentView(R.layout.activity_add_album);
        artistId = getIntent().getLongExtra("artistId", 0);
        idTracksSelected = new ArrayList<>();
        ActionMode actionMode = null;

        chooseImg = findViewById(R.id.btn_choose_album_image_file);
        chooseImg.setOnClickListener(this::addImage);
        image = findViewById(R.id.edit_image_album_path);

        imageResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::getImageResult);


        ArrayList<Track> tracks = new ArrayList<>();
        ListView artistListView = ((ListView) findViewById(R.id.artist_tracks));
        AbsListView.MultiChoiceModeListener modeListener = new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode = actionMode;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                actionMode = null;
                idTracksSelected.clear();
            }
        };
        artistListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        artistListView.setMultiChoiceModeListener(modeListener);

        database = FirebaseDatabase.getInstance().getReference();
        database.child("tracks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tracks.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Track t = snap.getValue(Track.class);
                    if (t != null && t.getArtistId().equals(artistId)) {
                        if (t.getAlbumId() == null) {
                            tracks.add(t);
                        }
                    }
                }
                if (tracks.size() != 0) {
                    artistListView.setAdapter(new ListViewAdapter(tracks, c,"Album"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        database.child("artists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Artist a = snap.getValue(Artist.class);
                    if (a != null && a.getId().equals(artistId)) {
                        artist = a;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getImageResult(Uri uri) {
        if (uri != null) {
            localFileImageUri = uri;
            Picasso.with(this).load(localFileImageUri).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(image);
        }
    }


    private void addImage(View view) {
        imageResultLauncher.launch("image/*");
    }

    public void addAlbum(View view) {
        if (idTracksSelected.size() != 0) {
            String title = ((EditText) findViewById(R.id.album_title)).getText().toString();
            String description = ((EditText) findViewById(R.id.album_description)).getText().toString();


            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference("ids/lastAlbumId").get()
                    .addOnSuccessListener(dataSnapshot -> {
                        final Long[] lastAlbumId = {dataSnapshot.getValue(Long.class)};
                        if (lastAlbumId[0] != null) {
                            StorageReference imageRef = storeRef.child(FirebaseUtils.ALBUM_IMAGE_FILE_PATH).child(lastAlbumId[0].toString());
                            imageRef.putFile(localFileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            FirebaseAlbum album = new FirebaseAlbum(lastAlbumId[0], title, description, uri.toString(), artistId, idTracksSelected);
                                            db.getReference("albums/").child(String.valueOf(lastAlbumId[0])).setValue(album);
                                            for (Long id : idTracksSelected) {
                                                db.getReference("tracks/" + id).child("albumId").setValue(lastAlbumId[0]);
                                            }

                                            if (artist.getAlbumsId() == null) {
                                                List<Long> albumsIds = new ArrayList<>();
                                                albumsIds.add(lastAlbumId[0]);
                                                db.getReference("artists/" + artistId).child("albumsId").setValue(albumsIds);
                                            } else {
                                                artist.getAlbumsId().add(lastAlbumId[0]);
                                                db.getReference("artists/" + artistId).child("albumsId").setValue(artist.getAlbumsId());
                                            }
                                            db.getReference("ids/lastAlbumId").setValue(++lastAlbumId[0]);
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("albumId", album.getId());
                                            resultIntent.putExtra("albumName", album.getTitle());
                                            setResult(RESULT_OK, resultIntent);
                                            finish();
                                        }
                                    });

                                }
                            });


                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "You need to select at least one track", Toast.LENGTH_SHORT).show();
        }
    }
}