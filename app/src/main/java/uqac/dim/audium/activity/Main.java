package uqac.dim.audium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import uqac.dim.audium.R;
import uqac.dim.audium.fragment.HomeFragment;
import uqac.dim.audium.fragment.MediaPlayerFragment;
import uqac.dim.audium.fragment.SearchFragment;
import uqac.dim.audium.model.entity.User;

public class Main extends AppCompatActivity {
    private User user; ///A CHANGER !!!
    private ImageButton btnHome, btnSearch, btnSettings;
    private String fragmentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHome = findViewById(R.id.btn_home);
        btnSearch = findViewById(R.id.btn_search);
        btnSettings = findViewById(R.id.btn_settings);

        btnHome.setOnClickListener(this::home);
        btnSearch.setOnClickListener(this::search);
        btnSettings.setOnClickListener(this::settings);

        initUser();

        if (user != null && user.isAdmin()) {
            initAdminMenu();
        }

        HomeFragment homeFragment = new HomeFragment(getApplicationContext());
        Bundle b = new Bundle();
        b.putString("username", user.getUsername());
        homeFragment.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .add(R.id.music_player, new MediaPlayerFragment(getApplicationContext()))
                .commit();

        //this.fragmentState = "home";
    }

    private void home(View view) {
        //if (!fragmentState.equals("home")) {
            HomeFragment homeFragment = new HomeFragment(getApplicationContext());
            Bundle b = new Bundle();
            b.putString("username", user.getUsername());
            homeFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFragment)
                    .commit();

           // this.fragmentState = "home";
        //}
    }

    private void search(View view) {
        //if(!fragmentState.equals("search")) {
            SearchFragment searchFragment = new SearchFragment(getApplicationContext(), user.getUsername());
            Bundle b = new Bundle();
            b.putString("username", user.getUsername());
            searchFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, searchFragment)
                    .addToBackStack("search")
                    .commit();

            //this.fragmentState = "search";
        //}
    }

    private void settings(View view) {
        Intent i = new Intent(getApplicationContext(), Settings.class);
        i.putExtra("firstName", user.getFirstName());
        i.putExtra("lastName", user.getLastName());
        i.putExtra("age", user.getAge());
        i.putExtra("username", user.getUsername());
        i.putExtra("isAdmin", user.isAdmin());
        startActivity(i);
    }


    private void initUser() {
        try {
            Bundle extras = getIntent().getExtras();

            String firstName = (String) extras.get("firstName");
            String lastName = (String) extras.get("lastName");
            int age = Integer.parseInt(extras.get("age").toString());
            String username = (String) extras.get("username");
            boolean isAdmin = "true".equals(extras.get("isAdmin").toString());

            user = new User(firstName, lastName, age, username, isAdmin);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_state, Toast.LENGTH_SHORT).show();
            Log.e("DIM", "Invalid state");
            e.printStackTrace();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
    }

    //TODO
    //Faire le menu
    private void initAdminMenu() {
        //Admin doit pouvoir voir et modifier les musiques et les supprimer


        //Faire l'ajout de musique plus tard

        //Peut voir les infos des utilisateurs - FAIT

        //Voir pour supprimer les utilisateurs - A CODER DANS LE BOUTTON

        //Ajouter les artistes et les albums correspondant

        //Faire une vue pour les albums + gestion des albums
    }

    //// --------------------------------------------------

}
