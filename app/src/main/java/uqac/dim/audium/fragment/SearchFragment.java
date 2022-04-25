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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.MediaService;
import uqac.dim.audium.R;
import uqac.dim.audium.adapter.ListViewAlbumAdapter;
import uqac.dim.audium.adapter.ListViewArtistAdapter;
import uqac.dim.audium.adapter.ListViewTrackAdapter;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;

public class SearchFragment extends Fragment {
    private final Context context;
    private DatabaseReference database;
    private String username;
    private MediaService mediaService;
    private ServiceConnection serviceConnection;

    private SearchView searchView;
    private ListView lvResult;
    private RadioButton radioAlbum, radioArtist, radioTrack;

    private String searchMode;
    private String searchText = "";

    public SearchFragment(Context context, String username) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        } else if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        this.username = username;
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();

        setServiceConnection();
        Intent mediaServiceIntent = new Intent(context, MediaService.class);
        boolean successBind = context.bindService(mediaServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (successBind) {
            context.startService(mediaServiceIntent);
            Log.i("DIM", "media service started");
        } else {
            context.unbindService(serviceConnection);
            Log.e("DIM", "media service cannot be bind");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = root.findViewById(R.id.search_bar);
        lvResult = root.findViewById((R.id.list_view));
        radioAlbum = root.findViewById(R.id.radio_album);
        radioArtist = root.findViewById(R.id.radio_artist);
        radioTrack = root.findViewById(R.id.radio_track);

        lvResult.setOnItemClickListener(this::onItemClickListener);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchText = s;
                search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchText = s;
                search(s);
                return false;
            }
        });
        radioAlbum.setOnClickListener(this::onRadioClick);
        radioArtist.setOnClickListener(this::onRadioClick);
        radioTrack.setOnClickListener(this::onRadioClick);

        setSearchMode(this.searchMode == null ? "track" : searchMode);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        search("");
    }

    public void search(String searchText) {
        switch (searchMode) {
            case "album":
                searchAlbum(searchText);
                break;
            case "artist":
                searchArtist(searchText);
                break;
            case "track":
                searchTrack(searchText);
                break;
        }
    }

    private void searchAlbum(String searchText) {
        database.child("albums").limitToFirst(40).get().addOnSuccessListener(dataSnapshot -> {
            List<Album> albums = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Album album = snap.getValue(Album.class);
                    if (album != null && album.getTitle().contains(searchText)) {
                        albums.add(album);
                    }
                }
            }
            lvResult.setAdapter(new ListViewAlbumAdapter(context, albums).setHasIndex(false));
        });
    }

    private void searchArtist(String searchText) {
        database.child("artists").limitToFirst(40).get().addOnSuccessListener(dataSnapshot -> {
            List<Artist> artists = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Artist artist = snap.getValue(Artist.class);
                    if (artist != null && artist.getPrintableName().contains(searchText)) {
                        artists.add(artist);
                    }
                }
            }
            lvResult.setAdapter(new ListViewArtistAdapter(artists, context));
        });
    }

    private void searchTrack(String searchText) {
        Context c = getContext();
        database.child("tracks").limitToFirst(40).get().addOnSuccessListener(dataSnapshot -> {
            List<Track> tracks = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Track track = snap.getValue(Track.class);
                    if (track != null && track.getName().contains(searchText)) {
                        tracks.add(track);
                    }
                }
            }
            lvResult.setAdapter(new ListViewTrackAdapter(c, tracks, username).setHasIndex(false));
        });
    }

    private void showAlbum(Album album) {
        Log.i("DIM", "showAlbum");

        AlbumPageFragment albumPageFragment = new AlbumPageFragment();
        Bundle b = new Bundle();
        b.putString("username", username);
        b.putLong("albumId", album.getId());
        albumPageFragment.setArguments(b);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, albumPageFragment)
                .addToBackStack("album")
                .commit();
    }

    private void showArtist(Artist artist) {
        Log.i("DIM", "showArtist");

        ArtistPageFragment artistPageFragment = new ArtistPageFragment();
        Bundle b = new Bundle();
        b.putString("username", username);
        b.putLong("artistId", artist.getId());
        artistPageFragment.setArguments(b);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, artistPageFragment)
                .addToBackStack("artistPage")
                .commit();

    }

    private void playTrack(Track track) {
        Log.i("DIM", "showTrack");

        List<Track> tracks = new ArrayList<>();
        tracks.add(track);

        if (mediaService != null) {
            mediaService.setTracks(tracks);
            mediaService.stop();
            mediaService.play();
        }
    }

    private void setSearchMode(String searchMode) {
        if (this.searchMode == null) {

        }
        this.searchMode = searchMode;
        switch (searchMode) {
            case "album":
                radioAlbum.setChecked(true);
                break;
            case "artist":
                radioArtist.setChecked(true);
                break;
            case "track":
                radioTrack.setChecked(true);
                break;
            default:
                this.searchMode = "track";
                radioTrack.setChecked(true);
        }
        search(searchText);
    }

    private void setServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i("DIM", "setServiceConnection.onServiceConnected()");
                mediaService = ((MediaService.MediaServiceBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i("DIM", "setServiceConnection.onServiceDisconnected()");
                mediaService = null;
            }
        };
    }

    private void onRadioClick(View view) {
        RadioButton radioButton = (RadioButton) view;
//        radioButton.toggle();

        switch (radioButton.getId()) {
            case R.id.radio_album:
                searchMode = "album";
                break;
            case R.id.radio_artist:
                searchMode = "artist";
                break;
            case R.id.radio_track:
                searchMode = "track";
                break;
        }
        search(searchText);
    }

    private void onItemClickListener(AdapterView<?> adapterView, View view, int position, long l) {
        switch (searchMode) {
            case "album":
                Album album = (Album) adapterView.getItemAtPosition(position);
                showAlbum(album);
                break;
            case "artist":
                Artist artist = (Artist) adapterView.getItemAtPosition(position);
                showArtist(artist);
                break;
            case "track":
                Track track = (Track) adapterView.getItemAtPosition(position);
                playTrack(track);
                break;
        }
    }
}
