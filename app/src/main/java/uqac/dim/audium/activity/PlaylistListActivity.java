package uqac.dim.audium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.User;

public class PlaylistListActivity extends AppCompatActivity {

    private DatabaseReference database;
    private User user;
    private List<Playlist> playlists;
    private GridView gridView;
    private Button addButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists_list);
        gridView = findViewById(R.id.grid_playlist);
        addButton = findViewById(R.id.add_playlist_btn);

        String username = getIntent().getStringExtra("username");

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
                                    gridView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, playlists));
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

    public void addPlaylist(View view) {
        Intent i = new Intent(getApplicationContext(), AddPlaylistActivity.class);
        i.putExtra("username",user.getUsername());
        startActivity(i);
    }
}
