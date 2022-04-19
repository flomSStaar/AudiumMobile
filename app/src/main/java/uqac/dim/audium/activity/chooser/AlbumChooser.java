package uqac.dim.audium.activity.chooser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import uqac.dim.audium.activity.admin.AddAlbumActivity;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.utils.ListViewAlbumAdapter;

public class AlbumChooser extends AppCompatActivity {
    private TextView tvArtistInfo;
    private ListView listViewAlbums;

    private Long artistId;

    private ActivityResultLauncher<Intent> albumResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_chooser);

        findViewById(R.id.btn_add_artist).setOnClickListener(this::createAlbum);
        tvArtistInfo = findViewById(R.id.tv_artist_info);
        listViewAlbums = findViewById(R.id.list_artists);
        listViewAlbums.setOnItemClickListener(this::onAlbumClick);

        albumResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::getAlbumResult);

        try {
            Bundle extras = getIntent().getExtras();
            artistId = (Long) extras.get("artistId");
            String artistName = extras.get("artistName").toString();
            if (artistId != null) {
                tvArtistInfo.setText(getString(R.string.choose_an_album_from) + " " + artistName);
                loadAlbums(artistId);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            setResult(RESULT_CANCELED, new Intent().putExtra("error", true));
            finish();
        }
    }

    private void loadAlbums(Long artistId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Context c = this;
        dbRef.child("artists/" + artistId + "/albumsId").get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        List<Long> albumIds = new ArrayList<>();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            albumIds.add(d.getValue(Long.class));
                        }
                        if (!albumIds.isEmpty()) {
                            dbRef.child("albums").get()
                                    .addOnSuccessListener(dataSnapshot1 -> {
                                        if (dataSnapshot1.exists()) {
                                            List<Album> albums = new ArrayList<>();
                                            for (DataSnapshot d : dataSnapshot1.getChildren()) {
                                                Long albumId = d.getKey() != null ? Long.valueOf(d.getKey()) : null;
                                                if (albumId != null && albumIds.contains(albumId)) {
                                                    Album album = d.getValue(Album.class);
                                                    if (album != null) {
                                                        albums.add(album);
                                                    }
                                                }
                                            }
                                            //listViewAlbums.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, albums));
                                            listViewAlbums.setAdapter(new ListViewAlbumAdapter(albums, c));
                                        }
                                    });
                        } else {
                            Log.i("DIM", "No album for this artist");
                        }
                    } else {
                        Log.e("DIM", "Artist " + artistId + " doesn't exist");
                    }
                });
    }

    private void onAlbumClick(AdapterView<?> adapterView, View view, int i, long l) {
        Album album = (Album) adapterView.getItemAtPosition(i);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("albumId", album.getId());
        resultIntent.putExtra("albumName", album.getTitle());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void createAlbum(View view) {
        Intent intent = new Intent(getApplicationContext(), AddAlbumActivity.class);
        intent.putExtra("artistId", artistId);
        albumResultLauncher.launch(intent);
    }

    private void getAlbumResult(ActivityResult activityResult) {
        try {
            if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() != null) {
                Bundle extras = activityResult.getData().getExtras();
                Long albumId = extras.getLong("albumId");
                String albumName = extras.getString("albumName");
                if (albumId != 0 && albumName != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("albumId", albumId);
                    resultIntent.putExtra("albumName", albumName);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    throw new Exception();
                }
            } else if (activityResult.getResultCode() == RESULT_CANCELED) {
                if (activityResult.getData() != null && activityResult.getData().hasExtra("error")) {
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
