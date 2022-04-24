package uqac.dim.audium.activity.chooser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.activity.admin.AddArtist;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.adapter.ListViewArtistAdapter;

public class ArtistChooser extends AppCompatActivity {
    private ListView listViewArtists;

    private ActivityResultLauncher<Intent> artistResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_chooser);

        findViewById(R.id.btn_add_artist).setOnClickListener(this::createArtist);
        listViewArtists = findViewById(R.id.list_artists);
        listViewArtists.setOnItemClickListener(this::onArtistClick);

        artistResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::getArtistResult);

        loadArtists();
    }

    private void loadArtists() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Context c = this;
        dbRef.child("artists").get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        List<Artist> artists = new ArrayList<>();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            Artist a = d.getValue(Artist.class);
                            if (a != null) {
                                artists.add(a);
                            }
                        }
                        //listViewArtists.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, artists));
                        listViewArtists.setAdapter(new ListViewArtistAdapter(artists,c));
                    } else {
                        Log.i("DIM", "There is no artist in database");

                    }
                });
    }

    private void onArtistClick(AdapterView<?> adapterView, View view, int i, long l) {
        Artist artist = (Artist) adapterView.getItemAtPosition(i);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("artistId", artist.getId());
        resultIntent.putExtra("artistName", artist.getPrintableName());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void createArtist(View view) {
        Intent intent = new Intent(getApplicationContext(), AddArtist.class);
        artistResultLauncher.launch(intent);
    }

    private void getArtistResult(ActivityResult activityResult) {
        try {
            if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() != null) {
                Bundle extras = activityResult.getData().getExtras();
                Long artistId = extras.getLong("artistId");
                String artistName = extras.getString("artistName");
                if (artistId != 0 && artistName != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("artistId", artistId);
                    resultIntent.putExtra("artistName", artistName);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    throw new Exception();
                }
            } else if (activityResult.getResultCode() == RESULT_CANCELED && activityResult.getData() != null) {
                boolean error = activityResult.getData().getBooleanExtra("error", true);
                if (activityResult.getResultCode() == RESULT_CANCELED && error) {
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            setResult(RESULT_CANCELED, new Intent().putExtra("error", true));
            finish();
        }
    }
}
