package uqac.dim.audium.activity.admin;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.firebase.FirebaseArtist;
import uqac.dim.audium.firebase.FirebaseUtils;
import uqac.dim.audium.model.utils.Utils;

public class AddArtist extends AppCompatActivity {

    private Button chooseImg;
    private ImageView image;
    private Uri localFileImageUri;
    private ActivityResultLauncher<String> imageResultLauncher;
    private final StorageReference storeRef = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artist);
        chooseImg = findViewById(R.id.btn_choose_artist_image_file);
        chooseImg.setOnClickListener(this::addImage);
        image = findViewById(R.id.image_artist);

        imageResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::getImageResult);


    }

    public void addArtist(View view) {
        String firstName = ((EditText) findViewById(R.id.artist_first_name)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.artist_last_name)).getText().toString();
        int age = !((EditText) findViewById(R.id.artist_age)).getText().toString().isEmpty() ? Integer.parseInt(((EditText) findViewById(R.id.artist_age)).getText().toString()) : -1;
        String stageName = ((EditText) findViewById(R.id.artist_stage_name)).getText().toString();
        List<Long> tracksId = new ArrayList<>();
        List<Long> albumsID = new ArrayList<>();



        if ((firstName.matches(Utils.FIRSTNAME_REGEX) && lastName.matches(Utils.LASTNAME_REGEX) || stageName.matches(Utils.STAGENAME_REGEX)) && age > 0) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference("ids/lastArtistId").get()
                    .addOnSuccessListener(dataSnapshot -> {
                        final Long[] lastArtistId = {dataSnapshot.getValue(Long.class)};
                        if (lastArtistId[0] != null) {
                            StorageReference imageRef = storeRef.child(FirebaseUtils.ARTIST_IMAGE_FILE_PATH).child(lastArtistId[0].toString());
                            imageRef.putFile(localFileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            FirebaseArtist artist = new FirebaseArtist(lastArtistId[0], firstName, lastName, age, stageName, tracksId, albumsID, uri.toString());
                                            db.getReference("artists/").child(String.valueOf(lastArtistId[0])).setValue(artist);
                                            db.getReference("ids/lastArtistId").setValue(++lastArtistId[0]);
                                        }
                                    });
                                }
                            });
                        }
                    });
            finish();
        }

    }

    private void addImage(View view) {
        imageResultLauncher.launch("image/*");
    }

    private void getImageResult(Uri uri) {
        if (uri != null) {
            localFileImageUri = uri;
            Picasso.with(this).load(localFileImageUri).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(image);
        }
    }
}