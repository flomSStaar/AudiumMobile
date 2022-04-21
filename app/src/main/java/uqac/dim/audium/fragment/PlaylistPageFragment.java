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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.MediaService;
import uqac.dim.audium.R;
import uqac.dim.audium.activity.ModifyPlaylistActivity;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.entity.User;
import uqac.dim.audium.model.utils.ListViewTrackAdapter;

public class PlaylistPageFragment extends Fragment {
    private String username;
    private Long playlistId;
    private Playlist playlist;
    private final List<Track> playlistTracks = new ArrayList<>();

    private TextView tvTitle, tvDescription;
    private ImageButton btnPlay, btnDelete, btnModify;
    private ImageView imageView;
    private ListView listView;

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private final Context context;
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

    public PlaylistPageFragment(Context context) {
        if (context != null) {
            this.context = context;
        } else {
            throw new IllegalArgumentException("context cannot be null");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(context, MediaService.class);
        context.bindService(intent, serviceConnection, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlist_page, container, false);

        tvTitle = (TextView) root.findViewById(R.id.tv_playlist_title);
        tvDescription = (TextView) root.findViewById(R.id.tv_playlist_description);
        btnPlay = (ImageButton) root.findViewById(R.id.btn_play_playlist);
        btnDelete = (ImageButton) root.findViewById(R.id.btn_delete_playlist);
        btnModify = (ImageButton) root.findViewById(R.id.btn_modify_playlist);
        listView = (ListView) root.findViewById(R.id.playlist_page_tracks);
        imageView = root.findViewById(R.id.image_playlist);

        btnPlay.setOnClickListener(this::playPlaylist);
        btnDelete.setOnClickListener(this::deletePlaylist);
        btnModify.setOnClickListener(this::modifyPlaylist);
        listView.setOnItemClickListener(this::onClickedItem);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        username = getArguments().getString("username");
        playlistId = getArguments().getLong("playlistId");

        database = FirebaseDatabase.getInstance().getReference();
        database.child("playlists").child(username).child(String.valueOf(playlistId)).get().addOnSuccessListener(playlistSnapshot -> {
            if (playlistSnapshot.exists()) {
                playlist = playlistSnapshot.getValue(Playlist.class);
                if (playlist != null) {
                    tvTitle.setText(playlist.getTitle());
                    tvDescription.setText(playlist.getDescription());
                    Picasso.with(getContext()).load(playlist.getImageUrl()).error(R.drawable.ic_notes).into(imageView);

                    database.child("tracks").get().addOnSuccessListener(trackSnapshot -> {
                        playlistTracks.clear();
                        for (DataSnapshot snap : trackSnapshot.getChildren()) {
                            Track t = snap.getValue(Track.class);
                            if (t != null)
                                if (playlist.getTracksId().contains(t.getId())) {
                                    playlistTracks.add(t);
                                }
                        }
                        if (playlistTracks.size() != 0)
                            //listView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tracks));
                            listView.setAdapter(new ListViewTrackAdapter(playlistTracks, getContext()));
                        else {
                            Toast.makeText(getContext(), "This playlist has no tracks", Toast.LENGTH_SHORT).show(); ///Techniquement impossible
                        }
                    });
                } else {
                    // Erreur
                }
            }
        });
    }

    private void playPlaylist(View view) {
        if (!playlistTracks.isEmpty()) {
            if (mediaService != null) {
                mediaService.setTracks(playlistTracks);
                mediaService.play();
            } else {
                Log.w("DIM", "Media service is not initialized");
            }
        } else {
            Log.e("DIM", "No track available for the playlist");
        }
    }

    public void deletePlaylist(View view) {
        database.child("playlists").child(username).child(String.valueOf(playlistId)).get().addOnSuccessListener(playlistSnapshot -> {
            if (playlistSnapshot.exists()) {
                Playlist playlist = playlistSnapshot.getValue(Playlist.class);
                if (playlist != null) {
                    List<Long> tracksId = playlist.getTracksId();
                    database.child("playlists").child(username).child(String.valueOf(playlistId)).removeValue();

                    // Remove de user
                    database.child("users").child(username).get().addOnSuccessListener(userSnapshot -> {
                        if (userSnapshot.exists()) {
                            User u = userSnapshot.getValue(User.class);
                            if (u != null) {
                                u.getPlaylists().remove(playlistId);
                                database.child("users").child(String.valueOf(username)).child("playlists").setValue(u.getPlaylists());
                            }
                        }
                    });

                    /// Remove des tracks
                    database.child("tracks").get().addOnSuccessListener(trackSnapshot -> {
                        for (DataSnapshot snap : trackSnapshot.getChildren()) {
                            Track track = snap.getValue(Track.class);
                            if (track != null && tracksId.contains(track.getId())) {
                                track.getPlaylistsId().remove(playlistId);
                                database.child("tracks").child(String.valueOf(track.getId())).child("playlistsId").setValue(track.getPlaylistsId());
                            }
                        }
                    });
                }
            }
            getParentFragmentManager().popBackStack();
        });
    }

    private void modifyPlaylist(View view) {
        Intent modifyIntent = new Intent(getActivity(), ModifyPlaylistActivity.class);
        modifyIntent.putExtra("username", username);
        modifyIntent.putExtra("playlistId", playlistId);
        startActivity(modifyIntent);
    }

    private void onClickedItem(AdapterView<?> adapterView, View view, int i, long l) {
        TrackPageFragment trackPageFragment = new TrackPageFragment();
        Bundle b = new Bundle();
        b.putString("username", username);
        b.putLong("trackId", ((Track) listView.getItemAtPosition(i)).getId());
        if (((Track) listView.getItemAtPosition(i)).getAlbumId() != null)
            b.putLong("albumId", ((Track) listView.getItemAtPosition(i)).getAlbumId());
        else
            b.putLong("albumId", 0);
        trackPageFragment.setArguments(b);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, trackPageFragment)
                .addToBackStack("playlistPage")
                .commit();
    }
}
