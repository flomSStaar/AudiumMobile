package uqac.dim.audium;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

public class UserProfileActivity extends AppCompatActivity {

    FirebaseDatabase db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent i = getIntent();
        db = FirebaseDatabase.getInstance();

//        DatabaseReference usersRef = db.getReference("users");
//        usersRef.get()
//                .addOnSuccessListener(dataSnapshot -> {
//
//                });
//
//        db.collection("users")
//                .whereEqualTo("username", i.getStringExtra("username"))
//                .get().addOnSuccessListener(queryDocumentSnapshots -> {
//            if (queryDocumentSnapshots.getDocuments().size() == 1) {
//                User user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
//                if (user != null) {
//                    ((TextView)findViewById(R.id.username)).setText(user.getUsername());
//                    ((TextView)findViewById(R.id.nom)).setText(user.getFirstName());
//                    ((TextView)findViewById(R.id.prenom)).setText(user.getLastName());
//                    ((TextView)findViewById(R.id.age)).setText(String.valueOf(user.getAge()));
//                }
//            } else {
//                Log.e("DIM", "Invalid credentials");
//            }
//        });
    }

}
