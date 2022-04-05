package uqac.dim.audium;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import uqac.dim.audium.model.entity.User;

public class MainActivity extends AppCompatActivity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.btn_logout)).setOnClickListener(this::deconnection);
        initUser();
    }

    private void initUser() {
        try {
            Bundle extras = getIntent().getExtras();

            String firstName = (String) extras.get("firstName");
            String lastName = (String) extras.get("lastName");
            int age = Integer.parseInt(extras.get("age").toString());
            String username = (String) extras.get("username");

            user = new User(firstName, lastName, age, username);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_state, Toast.LENGTH_SHORT).show();
            Log.e("DIM", "Invalid state");
            e.printStackTrace();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    private void deconnection(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("username", user.getUsername());
        startActivity(intent);
        finish();
    }
}
