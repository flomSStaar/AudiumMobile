package uqac.dim.audium;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.User;

public class ArtistListActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);
        ArrayList<Artist> l = new ArrayList<>();
        l.add(new Artist("Reginald","Kenneth Dwight","Elton John",75));
        ListView artistListView = ((ListView) findViewById(R.id.artistList));
        db = FirebaseFirestore.getInstance();
        db.collection("artists")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++)
                        l.add(queryDocumentSnapshots.getDocuments().get(i).toObject(Artist.class));
                    artistListView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, l));
                });

        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(ArtistListActivity.this, ArtistProfileActivity.class);
                intent.putExtra("stageName", ((Artist)artistListView.getItemAtPosition(position)).getStageName());
                startActivity(intent);
            }
        });


    }

    public void addArtists(View view) {
    }
}
