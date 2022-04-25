package uqac.dim.audium.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.adapter.GridViewAdapter;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.User;

public class PlaylistFragment extends Fragment {
    private final Context context;
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private User user;
    private List<Playlist> playlists;
    String username;
    private GridView gridView;
    private Button addButton;
    View root;

    public PlaylistFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = getArguments().getString("username");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_playlists_list, container, false);

        gridView = root.findViewById(R.id.grid_playlist);
        gridView.setOnItemClickListener(this::onItemClicked);
        addButton = root.findViewById(R.id.add_playlist_btn);
        addButton.setOnClickListener(this::addPlaylist);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizeList();
    }

    private void actualizeList() {
        Context c = getContext();
        database.child("users").child(username).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User.class);
                    database.child("playlists").child(user.getUsername()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                playlists = new ArrayList<>();
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Playlist p = snap.getValue(Playlist.class);
                                    playlists.add(p);
                                }
                                if (playlists != null)
                                    //gridView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, playlists));
                                    gridView.setAdapter(new GridViewAdapter(playlists, c));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void onItemClicked(AdapterView<?> adapterView, View view, int i, long l) {
        PlaylistPageFragment playlistPageFragment = new PlaylistPageFragment(context);
        Bundle b = new Bundle();
        b.putString("username", username);
        b.putLong("playlistId", ((Playlist) gridView.getItemAtPosition(i)).getId());
        playlistPageFragment.setArguments(b);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, playlistPageFragment)
                .addToBackStack("playlistPage")
                .commit();
    }


    public void addPlaylist(View view) {
        AddPlaylistFragment addPlaylistFragment = new AddPlaylistFragment();
        Bundle b = new Bundle();
        b.putString("username", username);
        addPlaylistFragment.setArguments(b);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, addPlaylistFragment)
                .addToBackStack("playlistPage")
                .commit();

    }
}
