package uqac.dim.audium.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
//import uqac.dim.audium.firebase.CustomAdapter;
import uqac.dim.audium.model.entity.Album;


public class SearchableActivity extends AppCompatActivity {
    private DatabaseReference database;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private ArrayList<Album> albums;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_fragment);
        database = FirebaseDatabase.getInstance().getReference();

        //recyclerView=(RecyclerView) findViewById(R.id.recview);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //adapter = new CustomAdapter();
        //recyclerView.setAdapter(adapter);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //getMenuInflater().inflate(R.menu.admin_menu,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_bar).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MenuItem item=menu.findItem(R.id.search_bar);



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

        return super.onCreateOptionsMenu(menu);
    }

    public void search(String s) {
        database.child("albums").startAt(s).endAt(s + "\uf8ff").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    albums.add(dataSnapshot.getValue(Album.class));
                }
            }
        });
        ListView lv=(ListView) findViewById(R.id.list_view);
        ArrayAdapter<Album> arrayAdapter = new ArrayAdapter<Album>(
                this, android.R.layout.simple_list_item_1,albums);

        lv.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }
}
