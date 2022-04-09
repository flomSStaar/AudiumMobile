package uqac.dim.audium.activity.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.firebase.FirebaseArtist;
import uqac.dim.audium.model.utils.Utils;

public class AddArtistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artist);
    }

    public void addArtist(View view) {
        String firstName = ((EditText) findViewById(R.id.artist_first_name)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.artist_last_name)).getText().toString();
        int age = !((EditText) findViewById(R.id.artist_age)).getText().toString().isEmpty() ? Integer.parseInt(((EditText) findViewById(R.id.artist_age)).getText().toString()) : -1;
        String stageName = ((EditText) findViewById(R.id.artist_stage_name)).getText().toString();
        String imagePath = ((EditText) findViewById(R.id.image_path)).getText().toString();
        List<Long> tracksId = new ArrayList<>();
        List<Long> albumsID = new ArrayList<>();

        if ((firstName.matches(Utils.FIRSTNAME_REGEX) && lastName.matches(Utils.LASTNAME_REGEX) || stageName.matches(Utils.STAGENAME_REGEX)) && age > 0 && !imagePath.trim().isEmpty()) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference("ids/lastArtistId").get()
                    .addOnSuccessListener(dataSnapshot -> {
                        Long lastArtistId = dataSnapshot.getValue(Long.class);
                        if (lastArtistId != null) {
                            FirebaseArtist artist = new FirebaseArtist(lastArtistId, firstName, lastName, age, stageName, tracksId, albumsID, imagePath);
                            db.getReference("artists/").child(String.valueOf(lastArtistId)).setValue(artist);
                            db.getReference("ids/lastArtistId").setValue(++lastArtistId);
                        }
                    });
            finish();
        }

    }
}