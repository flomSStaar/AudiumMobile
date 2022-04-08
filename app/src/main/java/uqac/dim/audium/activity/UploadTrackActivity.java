package uqac.dim.audium.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import uqac.dim.audium.R;

public class UploadTrackActivity extends AppCompatActivity {
    private Button btnChoose;
    private Button btnUpload;
    private ListView listView;
    private ActivityResultLauncher<String> activityResultLauncher;
    private Uri filePath;
    private final List<String> tracks = new ArrayList<>();
    private final StorageReference storage = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_track);

        btnChoose = findViewById(R.id.btn_choose_file);
        btnUpload = findViewById(R.id.btn_upload_file);
        listView = findViewById(R.id.list_item);

        btnChoose.setOnClickListener(this::selectImage);
        btnUpload.setOnClickListener(this::uploadImage);
        listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, tracks));

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::getResult);

        loadTracks();
    }

    private void selectImage(View view) {
        activityResultLauncher.launch("image/*");
    }

    private void uploadImage(View view) {
        if (filePath != null) {

            StorageReference ref = storage.child(UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.i("DIM", "File uploaded");
                        Toast.makeText(getApplicationContext(), "File uploaded", Toast.LENGTH_SHORT).show();
                        loadTracks();
                    })
                    .addOnProgressListener(snapshot -> {
                        //Progress bar ??
                    })
                    .addOnFailureListener(e -> {
                        Log.i("DIM", "File upload error");
                        Toast.makeText(getApplicationContext(), "File upload error", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadTracks() {
        storage.listAll()
                .addOnSuccessListener(listResult -> {
                    tracks.clear();
                    for (StorageReference ref : listResult.getItems()) {
                        tracks.add(ref.getName());
                    }
                    ((ArrayAdapter<String>) listView.getAdapter()).notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("DIM", e.getMessage());
                });

    }

    private void getResult(Uri uri) {
        filePath = uri;
    }
}
