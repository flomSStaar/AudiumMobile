package uqac.dim.audium;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import uqac.dim.audium.model.entity.Artist;

public class ArtistProfileActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent i = getIntent();
        db = FirebaseFirestore.getInstance();


        db.collection("artists")
                .whereEqualTo("stageName", i.getStringExtra("stageName"))
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.getDocuments().size() == 1) {
                Artist artist = queryDocumentSnapshots.getDocuments().get(0).toObject(Artist.class);
                if (artist != null) {
                    ((TextView)findViewById(R.id.stageName)).setText(artist.getStageName());
                    ((TextView)findViewById(R.id.artistFirstname)).setText(artist.getFirstName());
                    ((TextView)findViewById(R.id.artistLastname)).setText(artist.getLastName());
                    ((TextView)findViewById(R.id.artistAge)).setText(String.valueOf(artist.getAge()));

                    // Afficher les albums
                }
            } else {
                Log.e("DIM", "Invalid credentials");
            }
        });
    }
}
