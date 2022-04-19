package uqac.dim.audium.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import uqac.dim.audium.activity.AddPlaylistActivity;
import uqac.dim.audium.activity.PlaylistListActivity;
import uqac.dim.audium.activity.PlaylistPageActivity;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.User;

public class PlaylistFragment extends Fragment {

    private DatabaseReference database;
    private User user;
    private List<Playlist> playlists;
    String username;
    private GridView gridView;
    private Button addButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_playlists_list, container, false);

        gridView = root.findViewById(R.id.grid_playlist);
        gridView.setOnItemClickListener(this::onItemClicked);
        addButton = root.findViewById(R.id.add_playlist_btn);
        addButton.setOnClickListener(this::addPlaylist);

        return root;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = getArguments().getString("username");

        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(username).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);
                    database.child("playlists").child(user.getUsername()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                playlists = new ArrayList<>();
                                for (DataSnapshot snap: snapshot.getChildren()) {
                                    Playlist p = snap.getValue(Playlist.class);
                                    playlists.add(p);
                                }
                                if(playlists!=null)
                                    gridView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, playlists));
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
        /*
        Intent intent = new Intent(getContext(), PlaylistPageActivity.class);
        intent.putExtra("playlistId", ((Playlist) gridView.getItemAtPosition(i)).getId());
        intent.putExtra("username", username);
        startActivity(intent);*/
        PlaylistPageFragment playlistPageFragment = new PlaylistPageFragment();
        Bundle b = new Bundle();
        b.putString("username", username);
        b.putLong("playlistId",((Playlist) gridView.getItemAtPosition(i)).getId());
        playlistPageFragment.setArguments(b);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, playlistPageFragment)
                .addToBackStack("playlistPage")
                .commit();
    }


    public void addPlaylist(View view) {
        Intent i = new Intent(getContext(), AddPlaylistActivity.class);
        i.putExtra("username",user.getUsername());
        startActivity(i);
    }
}
