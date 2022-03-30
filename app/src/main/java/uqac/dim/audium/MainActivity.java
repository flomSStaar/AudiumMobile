package uqac.dim.audium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Playlist;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.entity.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User u = new User("Thomas", "Wilhem", 18, "thwilhem", "bonjour");
        Artist lolo = new Artist("lolo", "barto", 23);
        Track t = new Track("TestTrack", lolo, "oui");
        u.getPlaylists().add(new Playlist("oui", "non"));
        //u.getPlaylists().get(0).getTrackList().add(t); Fait tout planter

        Button searchBtn = findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(i);
        });

        Button loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        });
    }
}
