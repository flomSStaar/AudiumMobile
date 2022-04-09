package uqac.dim.audium.activity;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import uqac.dim.audium.R;
import uqac.dim.audium.firebase.FirebaseAlbum;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Track;

public class AlbumPageActivity extends AppCompatActivity {
    private Long albumId;
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
        database.child("albums").child(String.valueOf(albumId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                album = snapshot.getValue(Album.class);
                if (album != null) {
                    editTitle.setText(album.getTitle());
                    editTitle.setEnabled(false);
                    editDescription.setText(album.getDescription());
                    editDescription.setEnabled(false);
                    editArtist.setText(album.getArtistId().toString());
                    editArtist.setEnabled(false);
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
        database.child("albums").child(String.valueOf(albumId)).removeValue();
        albumId = null;
        finish();
    }
}