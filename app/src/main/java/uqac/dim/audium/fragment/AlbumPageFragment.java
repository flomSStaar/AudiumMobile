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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import uqac.dim.audium.adapter.ListViewTrackAdapter;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.entity.User;

public class AlbumPageFragment extends Fragment {
    private String username;
    private Long albumId;
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private MediaService mediaService;
    private ServiceConnection serviceConnection;

    private List<Long> tracksId;
    private final List<Track> tracks = new ArrayList<>();
    private Artist artist;
    private Album album;

    private ImageView ivAlbum;
    private ListView listView;
    private ImageButton btnPlay;
    private TextView tvAlbumName, tvAlbumArtistName, tvAlbumDescription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString("username");
        albumId = getArguments().getLong("albumId");

        setServiceConnection();
        Intent intent = new Intent(getContext(), MediaService.class);
        getContext().bindService(intent, serviceConnection, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_album_page, container, false);

        ivAlbum = root.findViewById(R.id.iv_album);
        listView = root.findViewById(R.id.lv_album_tracks);
        btnPlay = root.findViewById(R.id.btn_play_album);
        tvAlbumName = root.findViewById(R.id.tv_album_name);
        tvAlbumArtistName = root.findViewById(R.id.tv_album_artist);
        tvAlbumDescription = root.findViewById(R.id.tv_album_description);

        listView.setOnItemClickListener(this::onTrackItemClicked);
        btnPlay.setOnClickListener(this::playAlbum);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        Context c = getContext();
        database.child("albums").child(String.valueOf(albumId)).get().addOnSuccessListener(albumSnapshot -> {
            album = albumSnapshot.getValue(Album.class);
            if (album != null) {
                tracksId = album.getTracksId();
                tvAlbumName.setText(album.getTitle());
                tvAlbumDescription.setText(album.getDescription());
                Picasso.with(getContext()).load(album.getImageUrl()).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(ivAlbum);

                database.child("artists").child(album.getArtistId().toString()).get().addOnSuccessListener(artistSnapshot -> {
                    if (artistSnapshot.exists()) {
                        artist = artistSnapshot.getValue(Artist.class);
                        if (artist != null) {
                            tvAlbumArtistName.setText(artist.getPrintableName());
                        }
                    } else {
                        tvAlbumArtistName.setText(R.string.artist_name_error);
                    }
                });
            }
        });
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
                    listView.setAdapter(new ListViewTrackAdapter(c, tracks, username));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onTrackItemClicked(AdapterView<?> adapterView, View view, int i, long l) {
        if (!tracks.isEmpty()) {
            if (mediaService != null) {
                mediaService.setTracks(tracks, i);
                mediaService.stop();
                mediaService.play();
            } else {
                Log.w("DIM", "Media service is not initialized");
            }
        } else {
            Log.e("DIM", "No track available for the playlist");
        }
    }

    private void playAlbum(View view) {
        if (!tracks.isEmpty()) {
            if (mediaService != null) {
                mediaService.setTracks(tracks);
                mediaService.stop();
                mediaService.play();
            } else {
                Log.w("DIM", "Media service is not initialized");
            }
        } else {
            Log.e("DIM", "No track available for the album");
        }
    }

    private void setServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mediaService = ((MediaService.MediaServiceBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mediaService = null;
            }
        };
    }
}
