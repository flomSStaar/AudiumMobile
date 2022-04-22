package uqac.dim.audium.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import uqac.dim.audium.MediaService;
import uqac.dim.audium.R;
import uqac.dim.audium.activity.AlbumPage;
import uqac.dim.audium.activity.admin.AddAlbum;
import uqac.dim.audium.activity.admin.ArtistProfile;
import uqac.dim.audium.activity.admin.TrackPage;
import uqac.dim.audium.adapter.ListViewAlbumAdapter;
import uqac.dim.audium.adapter.ListViewTrackAdapter;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.entity.User;

public class ArtistPageFragment extends Fragment {

    private View root;
    private DatabaseReference database;
    private List<Long> idTracks;
    private List<Track> tracks;
    private Long artistId;
    private List<Long> idAlbums;
    private String username;
    private Artist artist;
    private Button btnDelete;
    private Button btnAddAlbum;
    private Button btnAlbums;
    private Button btnTracks;
    private ImageView image;
    private MediaService mediaService;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mediaService = ((MediaService.MediaServiceBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mediaService = null;
        }
    };



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_artist_profile, container, false);
        btnDelete = ((Button) root.findViewById(R.id.btn_delete_artist));
        btnDelete.setOnClickListener(this::deleteArtist);

        btnAddAlbum = ((Button) root.findViewById(R.id.btn_add_album));
        btnAddAlbum.setOnClickListener(this::addAlbum);

        btnAlbums = ((Button) root.findViewById(R.id.btn_albums));
        btnAlbums.setOnClickListener(this::showAlbums);

        btnTracks = ((Button) root.findViewById(R.id.btn_tracks));
        btnTracks.setOnClickListener(this::showMusics);

        image = root.findViewById(R.id.artist_image);
        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(username).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User u = dataSnapshot.getValue(User.class);
                    if (!u.isAdmin()){
                        btnAddAlbum.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                    }
                }
            }
        });
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString("username");
        artistId = getArguments().getLong("artistId");

        Intent intent = new Intent(getContext(), MediaService.class);
        getContext().bindService(intent, serviceConnection, 0);

        database = FirebaseDatabase.getInstance().getReference();
        database.child("artists").child(Long.toString(artistId)).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                artist = dataSnapshot.getValue(Artist.class);
                if (artist != null) {
                    ((EditText) root.findViewById(R.id.tv_stage_name)).setText(artist.getStageName());
                    ((EditText) root.findViewById(R.id.tv_artist_first_name)).setText(artist.getFirstName());
                    ((EditText) root.findViewById(R.id.tv_artist_last_name)).setText(artist.getLastName());
                    ((EditText) root.findViewById(R.id.tv_artist_age)).setText(String.valueOf(artist.getAge()));
                    Picasso.with(getContext()).load(artist.getImageUrl()).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(image);
                }
                idTracks = artist.getTracksId();
                idAlbums = artist.getAlbumsId();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void deleteArtist(View view) {
        database = FirebaseDatabase.getInstance().getReference();
        database.child("artists").child(String.valueOf(artist.getId())).removeValue();
        if (idTracks != null) {
            for (Long idTrack : idTracks) {
                //suppression de la musique
                database.child("tracks").child(String.valueOf(idTrack)).removeValue();

                //supression de l'id de la musique dans la playlist des utilisateurs
                database.child("playlists").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                for (DataSnapshot playlist : snap.getChildren()) {
                                    Playlist p = playlist.getValue(Playlist.class);
                                    if (p.getTracksId().contains(idTrack)) {
                                        p.getTracksId().remove(idTrack);
                                        if (!p.getTracksId().isEmpty())
                                            database.child("playlists").child(snap.getKey()).child(String.valueOf(p.getId())).child("tracksId").setValue(p.getTracksId());
                                        else
                                            database.child("playlists").child(snap.getKey()).child(String.valueOf(p.getId())).removeValue();
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
        if (idAlbums != null) {
            for (Long idAlbum : idAlbums) {
                database.child("albums").child(String.valueOf(idAlbum)).removeValue();
            }
        }
        artist = null;

        getParentFragmentManager().popBackStack();
    }

    public void showAlbums(View view) {
        ListView listView = ((ListView) root.findViewById(R.id.artist_albums_tracks_list));
        ArrayList<Album> albums = new ArrayList<>();


        database = FirebaseDatabase.getInstance().getReference();
        database.child("albums").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                albums.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Album a = snap.getValue(Album.class);
                    if (a != null && a.getArtistId().equals(artist.getId()))
                        albums.add(a);
                }
                if (albums.size() != 0) {
                    listView.setAdapter(new ListViewAlbumAdapter(albums, ArtistPageFragment.this));
                } else {
                    Toast.makeText(getContext(), "This artist has no albums", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener((adapter, view1, position, arg) -> {
            Intent intent = new Intent(getContext(), AlbumPage.class);
            intent.putExtra("albumId", ((Album) listView.getItemAtPosition(position)).getId());
            intent.putExtra("username", username);
            startActivity(intent);
        });


    }

    public void showMusics(View view) {
        ListView listView = ((ListView) root.findViewById(R.id.artist_albums_tracks_list));
        tracks = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference();
        database.child("tracks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tracks.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Track t = snap.getValue(Track.class);
                    if (t != null && t.getArtistId().equals(artist.getId()))
                        tracks.add(t);
                }
                if (tracks.size() != 0) {
                    listView.setAdapter(new ListViewTrackAdapter(tracks, ArtistPageFragment.this, username));
                } else {
                    Toast.makeText(getContext(), "This artist has no tracks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listView.setOnItemClickListener((adapter, view1, position, arg) -> {
            /*Intent intent = new Intent(getContext(), TrackPage.class);
            intent.putExtra("trackId", ((Track) listView.getItemAtPosition(position)).getId());
            intent.putExtra("albumId", ((Track) listView.getItemAtPosition(position)).getAlbumId());
            intent.putExtra("username", username);
            startActivity(intent);*/

            if (!tracks.isEmpty()) {
                if (mediaService != null) {
                    mediaService.setTracks(tracks, position);
                    mediaService.stop();
                    mediaService.play();
                } else {
                    Log.w("DIM", "Media service is not initialized");
                }
            } else {
                Log.e("DIM", "No track available for the playlist");
            }
        });
    }

    public void addAlbum(View view) {
        Intent intent = new Intent(getContext(), AddAlbum.class);
        intent.putExtra("artistId", artist.getId());
        startActivity(intent);
    }
}
