package uqac.dim.audium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import uqac.dim.audium.R;
import uqac.dim.audium.fragment.AdminFragment;
import uqac.dim.audium.model.entity.User;
import uqac.dim.audium.model.utils.Utils;

public class Settings extends AppCompatActivity {
    private TextView tvFirstName, tvLastName, tvUsername, tvAge;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button btnLogout = findViewById(R.id.btn_logout);
        ImageButton btnBack = findViewById(R.id.btn_back);
        tvFirstName = findViewById(R.id.tv_first_name);
        tvLastName = findViewById(R.id.tv_last_name);
        tvUsername = findViewById(R.id.tv_username);
        tvAge = findViewById(R.id.tv_age);

        btnLogout.setOnClickListener(this::deconnection);
        btnBack.setOnClickListener(this::back);

        initUser();

        if (user == null) {
            return;
        }
        tvFirstName.setText(user.getFirstName());
        tvLastName.setText(user.getLastName());
        tvUsername.setText(user.getUsername());
        tvAge.setText(String.valueOf(user.getAge()));

        if (user.isAdmin()) {
            final AdminFragment adminFragment = new AdminFragment();
            Bundle b = new Bundle();
            b.putString("username", user.getUsername());
            adminFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.admin_fragment, adminFragment)
                    .commit();
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
            finish();
        }
    }

    private void deconnection(View view) {
        File file = new File(getFilesDir(), Utils.USER_DATA_FILE);
        if (file.exists()) {
            if (file.delete()) {
                Log.i("DIM", "user data file has been deleted");
            } else {
                Log.w("DIM", "user data file has not been deleted");
            }
        }

        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.putExtra("username", user.getUsername());
        startActivity(intent);
        finish();
    }

    private void back(View view) {
        finish();
    }
}
