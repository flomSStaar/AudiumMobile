package uqac.dim.audium.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.User;

public class UserProfileActivity extends AppCompatActivity {

    DatabaseReference database;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ((Button) findViewById(R.id.btn_delete_user)).setOnClickListener(this::deleteUser);

        Intent i = getIntent();
        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(i.getStringExtra("username")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null) {
                    ((TextView) findViewById(R.id.username)).setText(user.getUsername());
                    ((TextView) findViewById(R.id.nom)).setText(user.getFirstName());
                    ((TextView) findViewById(R.id.prenom)).setText(user.getLastName());
                    ((TextView) findViewById(R.id.age)).setText(String.valueOf(user.getAge()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteUser(View view) {
        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(user.getUsername()).removeValue();
        user = null;
        finish();
    }
}
