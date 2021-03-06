package uqac.dim.audium.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.entity.User;

public class UserProfile extends AppCompatActivity {
    private DatabaseReference database;
    private User user;
    private EditText username;
    private EditText firstName;
    private EditText lastName;
    private EditText age;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        username = ((EditText) findViewById(R.id.tv_username));
        username.setEnabled(false);
        firstName = ((EditText) findViewById(R.id.tv_first_name));
        firstName.setEnabled(false);
        lastName = ((EditText) findViewById(R.id.tv_last_name));
        lastName.setEnabled(false);
        age = ((EditText) findViewById(R.id.tv_age));
        age.setEnabled(false);



        ((Button) findViewById(R.id.btn_delete_user)).setOnClickListener(this::deleteUser);

        Intent intent = getIntent();
        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(intent.getStringExtra("username")).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    username.setText(user.getUsername());
                    lastName.setText(user.getFirstName());
                    firstName.setText(user.getLastName());
                    age.setText(String.valueOf(user.getAge()));

                }
            }
        });

    }

    private void deleteUser(View view) {
        database.child("users").child(user.getUsername()).removeValue();
        List<Long> idPlaylists = user.getPlaylists();

        database.child("playlists").child(user.getUsername()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot playlist : dataSnapshot.getChildren()) {
                        Playlist p = playlist.getValue(Playlist.class);
                        if (idPlaylists.contains(p.getId())) {
                            for (Long track:p.getTracksId()) {
                                database.child("tracks").child(String.valueOf(track)).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Track t = dataSnapshot.getValue(Track.class);
                                            t.getPlaylistsId().remove(p.getId());
                                            database.child("tracks").child(String.valueOf(track)).child("playlistsId").setValue(t.getPlaylistsId());
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
        database.child("playlists").child(user.getUsername()).removeValue();
        user = null;
        finish();
    }
}
