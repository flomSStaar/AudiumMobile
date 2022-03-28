package uqac.dim.audiummobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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