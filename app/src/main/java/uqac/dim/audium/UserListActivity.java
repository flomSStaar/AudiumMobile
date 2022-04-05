package uqac.dim.audium;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;

import uqac.dim.audium.model.entity.User;

public class UserListActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ArrayList<String> l = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(int i=0; i<queryDocumentSnapshots.getDocuments().size();i++)
                        l.add(queryDocumentSnapshots.getDocuments().get(i).toObject(User.class).getUsername());
                    ((ListView) findViewById(R.id.userList)).setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,l));
                });


    }
}
