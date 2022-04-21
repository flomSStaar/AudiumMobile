package uqac.dim.audium.activity.admin;

import android.content.Intent;
import android.os.Bundle;
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
import uqac.dim.audium.adapter.ListViewUserAdapter;
import uqac.dim.audium.model.entity.User;

public class UserList extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ArrayList<User> users = new ArrayList<>();
        ListView userListView = ((ListView) findViewById(R.id.userList));

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    users.add(user);
                }
                userListView.setAdapter(new ListViewUserAdapter(users, getApplicationContext()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userListView.setOnItemClickListener((adapter, view, position, arg) -> {
            Intent intent = new Intent(getApplicationContext(), UserProfile.class);
            intent.putExtra("username", ((User) userListView.getItemAtPosition(position)).getUsername());
            startActivity(intent);
        });

    }
}
