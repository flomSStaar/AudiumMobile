package uqac.dim.audium.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.firebase.FirebasePlaylist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.entity.User;
import uqac.dim.audium.model.utils.ListViewAdapter;

public class AddPlaylistActivity extends AppCompatActivity {

    private String username;
    private User user;
    public static List<Long> idTracksSelected;
    private ListViewAdapter adapter;
    private DatabaseReference database;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context c = this;
        setContentView(R.layout.activity_add_playlist);
        username = getIntent().getStringExtra("username");
        idTracksSelected = new ArrayList<>();
        ActionMode actionMode = null;

        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(username).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);
                }
            }
        });


        ArrayList<Track> tracks = new ArrayList<>();
        ListView listView = ((ListView) findViewById(R.id.tracks));
        AbsListView.MultiChoiceModeListener modeListener = new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode = actionMode;
                return false;
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
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(modeListener);

        database = FirebaseDatabase.getInstance().getReference();
        database.child("tracks").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                tracks.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Track t = snap.getValue(Track.class);
                    if (t != null) {
                        tracks.add(t);
                    }
                }
                if (tracks.size() != 0) {
                    listView.setAdapter(new ListViewAdapter(tracks, c,"Playlist"));
                }
            }
        });
    }

    public void addPlaylist(View view) {
        if (idTracksSelected.size() != 0) {
            String title = ((EditText) findViewById(R.id.playlist_title)).getText().toString();
            String description = ((EditText) findViewById(R.id.playlist_description)).getText().toString();
            String imagePath = ((EditText) findViewById(R.id.playlist_image_path)).getText().toString();

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference("ids/lastPlaylistId").get()
                    .addOnSuccessListener(dataSnapshot -> {
                        Long lastPlaylistId = dataSnapshot.getValue(Long.class);
                        if (lastPlaylistId != null) {
                            FirebasePlaylist playlist = new FirebasePlaylist(lastPlaylistId,username, title, description,idTracksSelected, imagePath);
                            db.getReference("playlists/").child(username).child(String.valueOf(lastPlaylistId)).setValue(playlist);

                            /// Ajouter l'id de la playlist a la track - A FAIRE

                            Long finalLastPlaylistId = lastPlaylistId;
                            db.getReference("tracks").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data: dataSnapshot.getChildren()) {
                                        Track t = data.getValue(Track.class);
                                        if(idTracksSelected.contains(t.getId())){
                                            if(t.getPlaylistsId()==null) {
                                                List<Long> playlistsID = new ArrayList<>();
                                                playlistsID.add(finalLastPlaylistId);
                                                db.getReference("tracks/").child(String.valueOf(t.getId())).child("playlistsId").setValue(playlistsID);
                                            }else{
                                                t.getPlaylistsId().add(finalLastPlaylistId);
                                                db.getReference("tracks/").child(String.valueOf(t.getId())).child("playlistsId").setValue(t.getPlaylistsId());
                                            }
                                        }
                                    }
                                }
                            });

                            ////
                            /// Ajouter l'id de la playlist au user
                            if(user.getPlaylists()==null) {
                                List<Long> playlistsIds = new ArrayList<>();
                                playlistsIds.add(lastPlaylistId);
                                db.getReference("users/" + user.getUsername()).child("playlists").setValue(playlistsIds);
                            }
                            else {
                                user.getPlaylists().add(lastPlaylistId);
                                db.getReference("users/" + user.getUsername()).child("playlists").setValue(user.getPlaylists());
                            }
                            ////

                            //Intent resultIntent = new Intent();
                            //resultIntent.putExtra("albumId", album.getId());
                            //resultIntent.putExtra("albumName", album.getTitle());
                            //setResult(RESULT_OK, resultIntent);
                            db.getReference("ids/lastPlaylistId").setValue(++lastPlaylistId);
                            finish();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "You need to select at least one track", Toast.LENGTH_SHORT).show();
        }
    }
}

