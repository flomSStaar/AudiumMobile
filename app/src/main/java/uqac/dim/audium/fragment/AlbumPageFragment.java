package uqac.dim.audium.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.firebase.FirebaseAlbum;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.entity.User;
import uqac.dim.audium.view.adapter.ListViewTrackAdapter;

public class AlbumPageFragment extends Fragment {

    View root;
    private Long albumId;
    private String username;
    private List<Long> tracksId;
    private Artist artist;
    private Album album;
    private DatabaseReference database;
    private EditText editTitle;
    private EditText editDescription;
    private EditText editArtist;
    private ImageView imageView;
    ListView listView;
    private Button btnSave;
    private Button btnEdit;
    private Button btnDelete;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString("username");
        albumId = getArguments().getLong("albumId");

    }

    private void load() {
        database = FirebaseDatabase.getInstance().getReference();
        database.child("albums").child(String.valueOf(albumId)).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                album = dataSnapshot.getValue(Album.class);
                if (album != null) {
                    editTitle.setText(album.getTitle());
                    editTitle.setEnabled(false);
                    editDescription.setText(album.getDescription());
                    editDescription.setEnabled(false);
                    editArtist.setText(album.getArtistId().toString());
                    editArtist.setEnabled(false);
                    imageView = root.findViewById(R.id.image_album);
                    Picasso.with(getContext()).load(album.getImagePath()).error(R.drawable.ic_notes).into(imageView);

                    database.child("artists/" + album.getArtistId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                                artist = dataSnapshot.getValue(Artist.class);
                            else {
                                // A faire
                            }
                        }
                    });
                }
            }
        });
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
                if (tracks.size() != 0) {
                    listView.setAdapter(new ListViewTrackAdapter(tracks, getContext(), username));
                } else {
                    Toast.makeText(getContext(), "This album has no tracks", Toast.LENGTH_SHORT).show(); ///Techniquement impossible
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.child("users").child(username).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User.class);
                    if (!user.isAdmin()) {
                        btnEdit.setVisibility(View.INVISIBLE);
                        btnDelete.setVisibility(View.INVISIBLE);
                        btnSave.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_album_page, container, false);
        listView = ((ListView) root.findViewById(R.id.album_page_tracks));
        listView.setOnItemClickListener(this::OnItemClicked);
        editTitle = (EditText) root.findViewById(R.id.edit_album_title);
        editDescription = (EditText) root.findViewById(R.id.edit_album_description);
        editArtist = (EditText) root.findViewById(R.id.edit_album_stagename);
        btnSave = (Button) root.findViewById(R.id.btn_save_album);
        btnSave.setOnClickListener(this::saveAlbum);
        btnEdit = (Button) root.findViewById(R.id.edit_album);
        btnEdit.setOnClickListener(this::modifyAlbum);
        btnDelete = (Button) root.findViewById(R.id.delete_album);
        btnDelete.setOnClickListener(this::deleteAlbum);


        return root;
    }

    private void OnItemClicked(AdapterView<?> adapterView, View view, int i, long l) {
        /*Intent intent = new Intent(AlbumPageActivity.this, TrackPageActivity.class);
        intent.putExtra("trackId", ((Track) listView.getItemAtPosition(i)).getId());
        intent.putExtra("albumId", ((Track) listView.getItemAtPosition(i)).getAlbumId());
        intent.putExtra("username", username);
        startActivity(intent);*/


        TrackPageFragment trackPageFragment = new TrackPageFragment();
        Bundle b = new Bundle();
        b.putString("username", username);
        b.putLong("trackId", ((Track) listView.getItemAtPosition(i)).getId());
        if (((Track) listView.getItemAtPosition(i)).getAlbumId() != null)
            b.putLong("albumId", album.getId());
        else
            b.putLong("albumId", 0);
        trackPageFragment.setArguments(b);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, trackPageFragment)
                .addToBackStack("playlistPage")
                .commit();
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
        FirebaseAlbum newAlbum = new FirebaseAlbum(albumId, newTitle, newDescription, album.getImagePath(), album.getArtistId(), album.getTracksId());
        db.getReference("albums/").child(String.valueOf(albumId)).setValue(newAlbum);
        editTitle.setEnabled(false);
        editDescription.setEnabled(false);
        btnSave.setVisibility(View.INVISIBLE);
    }

    public void deleteAlbum(View view) {
        database = FirebaseDatabase.getInstance().getReference();

        /// Supprimer les idAlbum des musiques
        database.child("albums").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Album a = snap.getValue(Album.class);
                    if (a != null && a.getId().equals(albumId)) {
                        tracksId = a.getTracksId();
                        database.child("albums").child(String.valueOf(albumId)).removeValue();
                        database.child("tracks").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                    Track t = snap.getValue(Track.class);
                                    if (tracksId.contains(t.getId())) {
                                        t.setAlbumId(null);
                                        database.child("tracks").child(String.valueOf(t.getId())).setValue(t);
                                        List<Long> artistAlbumsId = artist.getAlbumsId();
                                        artistAlbumsId.remove(albumId);
                                        database.child("artists").child(artist.getId().toString()).child("albumsId").setValue(artistAlbumsId);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        getParentFragmentManager().popBackStack();
    }
}
