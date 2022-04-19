package uqac.dim.audium.activity.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.User;
import uqac.dim.audium.model.utils.ListViewTrackAdapter;
import uqac.dim.audium.model.utils.ListViewUserAdapter;

public class UserListActivity extends AppCompatActivity {
    private DatabaseReference database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context c = this;
        setContentView(R.layout.activity_user_list);
        ArrayList<User> l = new ArrayList<>();
        ListView userListView = ((ListView) findViewById(R.id.userList));
        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                l.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    l.add(user);
                }
                //userListView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, l));
                userListView.setAdapter(new ListViewUserAdapter(l,c));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userListView.setOnItemClickListener((adapter, view, position, arg) -> {
            Intent intent = new Intent(UserListActivity.this, UserProfileActivity.class);
            intent.putExtra("username", ((User) userListView.getItemAtPosition(position)).getUsername());
            startActivity(intent);
        });

    }
}
