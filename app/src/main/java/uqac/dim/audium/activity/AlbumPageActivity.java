package uqac.dim.audium.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

import uqac.dim.audium.R;
import uqac.dim.audium.firebase.FirebaseAlbum;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;

public class AlbumPageActivity extends AppCompatActivity {

    protected Long albumId;
    protected Album album;
    DatabaseReference database;
    EditText title;
    EditText description;
    EditText artist;
    Button saveBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_page);
        albumId=getIntent().getLongExtra("idAlbum",0);

        title = ((EditText) findViewById(R.id.album_page_title));
        description = ((EditText) findViewById(R.id.album_page_description));
        artist = ((EditText) findViewById(R.id.album_page_stagename));
        saveBtn = ((Button) findViewById(R.id.save_album_button));
        saveBtn.setVisibility(View.INVISIBLE);

        database = FirebaseDatabase.getInstance().getReference();
        database.child("albums").child(String.valueOf(albumId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                album = snapshot.getValue(Album.class);
                if(album!=null) {
                    title.setText(album.getTitle());
                    title.setEnabled(false);
                    description.setText(album.getDescription());
                    description.setEnabled(false);
                    artist.setText(album.getArtistId().toString());
                    artist.setEnabled(false);
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
                    if(t.getAlbumId().equals(albumId)) {
                        tracks.add(t);
                    }
                }
                if(tracks.size()!=0)
                    listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, tracks));
                else{
                    Toast.makeText(getApplicationContext(), "This album has no tracks", Toast.LENGTH_SHORT).show(); ///Techniquement impossible
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void modifyAlbum(View view) {
        title.setEnabled(true);
        description.setEnabled(true);
        saveBtn.setVisibility(View.VISIBLE);
    }

    public void saveAlbum(View view) {
        String newTitle = title.getText().toString();
        String newDescription = description.getText().toString();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseAlbum newAlbum = new FirebaseAlbum(albumId, newTitle, newDescription, album.getDescription(), album.getArtistId(), album.getTracksId());
        db.getReference("albums/").child(String.valueOf(albumId)).setValue(newAlbum);
        title.setEnabled(false);
        description.setEnabled(false);
        saveBtn.setVisibility(View.INVISIBLE);
    }

    public void deleteAlbum(View view) {
        database = FirebaseDatabase.getInstance().getReference();
        database.child("albums").child(String.valueOf(albumId)).removeValue();
        albumId = null;
        finish();
    }
}
