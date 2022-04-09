package uqac.dim.audium.activity.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
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
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.firebase.FirebaseAlbum;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.utils.ListViewAdapter;

public class AddAlbumActivity extends AppCompatActivity {
    private Long artistId;
    private ListViewAdapter adapter;
    private DatabaseReference database;
    public static List<Long> idTracksSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context c = this;
        setContentView(R.layout.activity_add_album);
        artistId = getIntent().getLongExtra("artistId", 0);
        idTracksSelected = new ArrayList<>();
        ActionMode actionMode = null;


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
                    artistListView.setAdapter(new ListViewAdapter(tracks, c));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    public void addAlbum(View view) {
        if (idTracksSelected.size() != 0) {
            String title = ((EditText) findViewById(R.id.album_title)).getText().toString();
            String description = ((EditText) findViewById(R.id.album_description)).getText().toString();
            String imagePath = ((EditText) findViewById(R.id.album_image_path)).getText().toString();

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference("ids/lastAlbumId").get()
                    .addOnSuccessListener(dataSnapshot -> {
                        Long lastAlbumId = dataSnapshot.getValue(Long.class);
                        if (lastAlbumId != null) {
                            FirebaseAlbum album = new FirebaseAlbum(lastAlbumId, title, description, imagePath, artistId, idTracksSelected);
                            db.getReference("albums/").child(String.valueOf(lastAlbumId)).setValue(album);
                            for (Long id : idTracksSelected) {
                                db.getReference("tracks/" + id).child("albumId").setValue(lastAlbumId);
                            }
                            db.getReference("ids/lastAlbumId").setValue(++lastAlbumId);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("albumId", album.getId());
                            resultIntent.putExtra("albumName", album.getTitle());
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "You need to select at least one track", Toast.LENGTH_SHORT).show();
        }
    }
}