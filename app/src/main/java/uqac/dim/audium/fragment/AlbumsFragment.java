package uqac.dim.audium.fragment;

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
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.User;
import uqac.dim.audium.view.adapter.GridViewAlbumAdapter;

public class AlbumsFragment extends Fragment {
    private DatabaseReference database;
    private User user;
    private List<Album> albums;
    String username;
    private GridView gridView;
    private Button addButton;
    View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = getArguments().getString("username");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root  = inflater.inflate(R.layout.activity_albums_list, container, false);

        gridView = root.findViewById(R.id.grid_albums);
        gridView.setOnItemClickListener(this::onItemClicked);

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        actualizeList();
    }

    private void actualizeList(){
        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(username).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);
                    database.child("albums").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                albums = new ArrayList<>();
                                for (DataSnapshot snap: snapshot.getChildren()) {
                                    Album a = snap.getValue(Album.class);
                                    albums.add(a);
                                }
                                if(albums !=null)
                                    //gridView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, playlists));
                                    gridView.setAdapter(new GridViewAlbumAdapter(albums,getContext()));
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
        AlbumPageFragment albumPageFragment = new AlbumPageFragment();
        Bundle b = new Bundle();
        b.putString("username", username);
        b.putLong("albumId",((Album) gridView.getItemAtPosition(i)).getId());
        albumPageFragment.setArguments(b);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, albumPageFragment)
                .addToBackStack("playlistPage")
                .commit();
    }
}
