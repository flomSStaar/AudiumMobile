package uqac.dim.audium.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uqac.dim.audium.adapter.GridViewAlbumAdapter;
import uqac.dim.audium.adapter.ListViewAlbumAdapter;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private Context context;
    private DatabaseReference database;
    private RecyclerView recyclerView;
    private ListViewAlbumAdapter adapter;
    private List<Album> albums = new ArrayList<>();
    SearchView searchView;
    ListView lv;
    View root;
    private LinearLayout linearLayoutAlbums;

    public SearchFragment(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);


        searchView = root.findViewById(R.id.search_bar);
        lv = root.findViewById((R.id.list_view));
        linearLayoutAlbums = root.findViewById(R.id.albums_list2);
        adapter = new ListViewAlbumAdapter(albums, context);
        lv.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }
        });


        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        search("");
    }

    public void search(String s) {
        ListViewAlbumAdapter lvaa;
        database.child("albums").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            //@Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                        if(snap.getValue(Album.class).getTitle().contains(s)) {
                            albums.add(snap.getValue(Album.class));
                        }
                    }
                    if (albums != null) {

                    }
                }
            }


            //@Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            //database.child("albums").child("title").equalTo("dodo" + "\uf8ff").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    albums.clear();
                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                        if(snap.getValue(Album.class).getTitle().contains(s)) {
                            albums.add(snap.getValue(Album.class));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        //View view = getLayoutInflater().inflate(R.layout.grid_view_album_item, linearLayoutAlbums,false);
        //lv.addView(view);
        /*
        ArrayAdapter<Album> arrayAdapter = new ArrayAdapter<Album>(
                getContext(), android.R.layout.simple_list_item_1,albums);

        lv.setAdapter(arrayAdapter);
        */
        //lvaa.notifyDataSetChanged();
    }
}
