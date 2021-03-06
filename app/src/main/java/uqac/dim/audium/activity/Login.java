package uqac.dim.audium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.User;
import uqac.dim.audium.model.utils.HashPassword;
import uqac.dim.audium.model.utils.Utils;

public class Login extends AppCompatActivity {
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private EditText editUsername;
    private EditText editPassword;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((Button) findViewById(R.id.btn_login)).setOnClickListener(this::login);
        ((Button) findViewById(R.id.btn_register)).setOnClickListener(this::register);

        editUsername = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::getRegisterResult);

        db = FirebaseDatabase.getInstance();

        File path = new File(getFilesDir(), "user.data");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            User user = (User) ois.readObject();
            Log.i("DIM", "user loaded from file");
            launchMain(user, false);
        } catch (IOException | ClassNotFoundException e) {
            Log.e("DIM", "cannot load user from file");
            e.printStackTrace();

            if (getIntent() != null && getIntent().getExtras() != null) {
                Bundle extras = getIntent().getExtras();
                String username = (String) extras.get("username");
                editUsername.setText(username);
            }
        }


    }

    private void login(View view) {
        Log.v("DIM", "LoginActivity: login");
        connect();
    }

    private void register(View view) {
        Intent i = new Intent(getApplicationContext(), Register.class);
        activityResultLauncher.launch(i);
    }

    private void connect() {
        Log.v("DIM", "LoginActivity: connect");
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();

        if (username.matches(Utils.USERNAME_REGEX) && password.matches(Utils.PASSWORD_REGEX)) {
            String hashPassword = HashPassword.hashPassword(password);

            DatabaseReference usersRef = db.getReference("users/" + username);
            usersRef.get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            String dbPassword = dataSnapshot.child("password").getValue(String.class);
                            if (Objects.equals(dbPassword, hashPassword)) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                    Log.i("DIM", "Login successful");

                                    launchMain(user, true);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.wrong_credentials), Toast.LENGTH_SHORT).show();
                                Log.e("DIM", "Wrong credentials");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("DIM", e.toString());
                        //TODO
                        //Erreur de connexion ?? la base
                    });
        } else {
            if (username.trim().isEmpty() && password.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.enter_username_and_password, Toast.LENGTH_SHORT).show();
            } else {
                if (!username.matches(Utils.USERNAME_REGEX)) {
                    editUsername.setText(username.trim());
                    editUsername.setError(getString(R.string.username_validation));
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_username), Toast.LENGTH_SHORT).show();
                }
                if (!password.matches(Utils.PASSWORD_REGEX)) {
                    editPassword.setText("");
                    editPassword.setError(getString(R.string.password_validation));
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
                }
            }
            Log.e("DIM", "Invalid username or password!");
        }
    }

    private void launchMain(User user, boolean saveInFile) {
        if (saveInFile) {
            File path = new File(getFilesDir(), "user.data");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
                oos.writeObject(user);
                Log.i("DIM", "User saved in file");
            } catch (IOException e) {
                Log.e("DIM", "Cannot save the user in file");
                e.printStackTrace();
            }
        }

        //Start the main activity
        Intent i = new Intent(getApplicationContext(), Main.class);
        i.putExtra("firstName", user.getFirstName());
        i.putExtra("lastName", user.getLastName());
        i.putExtra("age", user.getAge());
        i.putExtra("username", user.getUsername());
        i.putExtra("isAdmin", user.isAdmin());
        startActivity(i);
        finish();
    }

    private void getRegisterResult(@NonNull ActivityResult result) {
        Log.v("DIM", "LoginActivity: getRegisterResult");
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Bundle extras = result.getData().getExtras();

            if (extras != null) {
                Log.i("DIM", "Getting informations from register page");
                String username = (String) extras.get("username");
                String password = (String) extras.get("password");

                editUsername.setText(username);
                editPassword.setText(password);

                connect();
            }
        } else if (result.getResultCode() == RESULT_CANCELED
                && result.getData() != null
                && "connection".equals(result.getData().getExtras().get("error"))) {
            Toast.makeText(getApplicationContext(), R.string.cannot_register, Toast.LENGTH_SHORT).show();
            Log.e("DIM", "Cannot registered");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("DIM", "LoginActivity: onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("DIM", "LoginActivity: onDestroy");
    }
}
