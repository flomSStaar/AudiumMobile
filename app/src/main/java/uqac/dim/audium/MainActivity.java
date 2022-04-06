package uqac.dim.audium;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import uqac.dim.audium.adminActivity.ArtistListActivity;
import uqac.dim.audium.adminActivity.UserListActivity;
import uqac.dim.audium.model.entity.User;

public class MainActivity extends AppCompatActivity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.btn_logout)).setOnClickListener(this::deconnection);
        initUser();

        if (user != null && user.isAdmin()) {
            initAdminMenu();
        }
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
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    //// ------------------- Partie ADMIN ----------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (user.isAdmin()) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_utilisateurs:
                Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_artistes:
                Intent intent2 = new Intent(getApplicationContext(), ArtistListActivity.class);
                startActivity(intent2);
                break;
            case R.id.menu_musiques:

            default:

        }

        return super.onOptionsItemSelected(item);
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

    private void deconnection(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("username", user.getUsername());
        startActivity(intent);
        finish();
    }
}
