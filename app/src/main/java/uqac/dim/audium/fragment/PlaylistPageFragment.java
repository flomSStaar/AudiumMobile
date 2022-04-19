package uqac.dim.audium.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.activity.PlaylistPageActivity;
import uqac.dim.audium.activity.admin.TrackPageActivity;
import uqac.dim.audium.firebase.FirebasePlaylist;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.entity.User;

public class PlaylistPageFragment extends Fragment {

    protected String username;
    protected Long playlistId;
    protected Playlist playlist;
    protected List<Track> tracks;
    private EditText editTitle;
    private EditText editDescription;
    private Button btnSave;
    private DatabaseReference database;
    private ListView listView;
    private Button btnEdit;
    View root;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load(){
        username = getArguments().getString("username");
        playlistId = getArguments().getLong("playlistId");

        database = FirebaseDatabase.getInstance().getReference();
        database.child("playlists").child(username).child(String.valueOf(playlistId)).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    playlist = dataSnapshot.getValue(Playlist.class);
                    editTitle.setText(playlist.getTitle());
                    editTitle.setEnabled(false);
                    editDescription.setText(playlist.getDescription());
                    editDescription.setEnabled(false);
                }
            }
        });

        ArrayList<Track> tracks = new ArrayList<>();

        database.child("tracks").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                tracks.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Track t = snap.getValue(Track.class);
                    if (t != null)
                        if (playlist.getTracksId().contains(t.getId())) {
                            tracks.add(t);
                        }
                }
                if (tracks.size() != 0)
                    listView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tracks));
                else {
                    Toast.makeText(getContext(), "This playlist has no tracks", Toast.LENGTH_SHORT).show(); ///Techniquement impossible
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_playlist_page, container, false);
        editTitle = (EditText) root.findViewById(R.id.edit_playlist_title);
        editDescription = (EditText) root.findViewById(R.id.edit_playlist_description);
        btnSave = (Button) root.findViewById(R.id.btn_save_playlist);
        btnSave.setVisibility(View.INVISIBLE);
        btnSave.setOnClickListener(this::savePlaylist);

        btnEdit = (Button) root.findViewById(R.id.modifyPlaylist);
        btnEdit.setOnClickListener(this::modifyPlaylist);
        listView = ((ListView) root.findViewById(R.id.playlist_page_tracks));
        listView.setOnItemClickListener(this::onClickedItem);

        Button btnDelete = (Button) root.findViewById(R.id.delete_playlist);
        btnDelete.setOnClickListener(this::deletePlaylist);

        return root;
    }


    private void modifyPlaylist(View view) {
        editTitle.setEnabled(true);
        editDescription.setEnabled(true);
        btnSave.setVisibility(View.VISIBLE);
    }

    public void deletePlaylist(View view) {
        database = FirebaseDatabase.getInstance().getReference();

        database.child("playlists").child(username).child(String.valueOf(playlistId)).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Playlist p = dataSnapshot.getValue(Playlist.class);
                    List<Long> tracksId = p.getTracksId();

                    database.child("playlists").child(username).child(String.valueOf(playlistId)).removeValue();

                    // Remove de user
                    database.child("users").child(username).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                User u = dataSnapshot.getValue(User.class);
                                u.getPlaylists().remove(playlistId);
                                database.child("users").child(String.valueOf(username)).child("playlists").setValue(u.getPlaylists());
                            }
                        }
                    });

                    /// Remove des tracks
                    database.child("tracks").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                Track t = snap.getValue(Track.class);
                                if (tracksId.contains(t.getId())) {
                                    t.getPlaylistsId().remove(playlistId);
                                    database.child("tracks").child(String.valueOf(t.getId())).child("playlistsId").setValue(t.getPlaylistsId());
                                }
                            }
                        }
                    });
                }
            }
        });
        getParentFragmentManager().popBackStack();
    }

    public void savePlaylist(View view) {
        String newTitle = editTitle.getText().toString();
        String newDescription = editDescription.getText().toString();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebasePlaylist newPlaylist = new FirebasePlaylist(playlistId,username, newTitle, newDescription,playlist.getTracksId(),playlist.getImageUrl());
        db.getReference("playlists/").child(username).child(String.valueOf(playlistId)).setValue(newPlaylist);
        editTitle.setEnabled(false);
        editDescription.setEnabled(false);
        btnSave.setVisibility(View.INVISIBLE);
    }

    private void onClickedItem(AdapterView<?> adapterView, View view, int i, long l) {
        /*Intent intent = new Intent(getContext(), TrackPageActivity.class);
        intent.putExtra("trackId", ((Track) listView.getItemAtPosition(i)).getId());
        intent.putExtra("albumId", ((Track) listView.getItemAtPosition(i)).getAlbumId());
        intent.putExtra("username", username);
        startActivity(intent);*/

        TrackPageFragment trackPageFragment = new TrackPageFragment();
        Bundle b = new Bundle();
        b.putString("username", username);
        b.putLong("trackId",((Track) listView.getItemAtPosition(i)).getId());
        if(((Track) listView.getItemAtPosition(i)).getAlbumId()!=null)
            b.putLong("albumId",((Track) listView.getItemAtPosition(i)).getAlbumId());
        else
            b.putLong("albumId",0);
        trackPageFragment.setArguments(b);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, trackPageFragment)
                .addToBackStack("playlistPage")
                .commit();
    }


}
