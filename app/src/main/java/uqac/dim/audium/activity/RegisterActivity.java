package uqac.dim.audium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uqac.dim.audium.R;
import uqac.dim.audium.firebase.FirebaseUser;
import uqac.dim.audium.model.utils.HashPassword;
import uqac.dim.audium.model.utils.Utils;

public class RegisterActivity extends AppCompatActivity {
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editAge;
    private EditText editUsername;
    private EditText editPassword;

    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerBtn = (Button) findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(this::register);

        try {
            db = FirebaseDatabase.getInstance();
            initEditText();
        } catch (Exception e) {
            setResult(RESULT_CANCELED, new Intent().putExtra("error", "connection"));
            finish();
            e.printStackTrace();
        }
    }

    private void initEditText() {
        editFirstName = (EditText) findViewById(R.id.edit_first_name);
        editLastName = (EditText) findViewById(R.id.edit_last_name);
        editAge = (EditText) findViewById(R.id.edit_age);
        editUsername = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);

        editFirstName.requestFocus();

        editFirstName.setOnFocusChangeListener(this::onEditTextFocusChange);
        editLastName.setOnFocusChangeListener(this::onEditTextFocusChange);
        editAge.setOnFocusChangeListener(this::onEditTextFocusChange);
        editUsername.setOnFocusChangeListener(this::onEditTextFocusChange);
        editPassword.setOnFocusChangeListener(this::onEditTextFocusChange);
    }

    private void onEditTextFocusChange(View view, boolean b) {
        if (!b) {
            EditText editText = (EditText) view;
            if (!editText.getText().toString().trim().isEmpty()) {
                editText.setError(null);
            }
        }
    }

    private void register(View view) {
        Log.v("DIM", "RegisterActivity: register");
        try {
            Utils.hideKeyboard(getApplicationContext(), getCurrentFocus());

            String firstName = editFirstName.getText().toString();
            String lastName = editLastName.getText().toString();
            int age = !editAge.getText().toString().isEmpty() ? Integer.parseInt(editAge.getText().toString()) : -1;
            String username = editUsername.getText().toString();
            String password = editPassword.getText().toString();

            if (firstName.matches(Utils.FIRSTNAME_REGEX)
                    && lastName.matches(Utils.LASTNAME_REGEX)
                    && age > 0
                    && username.matches(Utils.USERNAME_REGEX)
                    && password.matches(Utils.PASSWORD_REGEX)) {
                DatabaseReference usersRef = db.getReference("users");
                usersRef.get()
                        .addOnSuccessListener(dataSnapshot -> {
                            if (!dataSnapshot.hasChild(username)) {
                                String hashPassword = HashPassword.hashPassword(password);
                                FirebaseUser user = new FirebaseUser(firstName, lastName, age, username, hashPassword);

                                //Save the user in the database
                                usersRef.child(username).setValue(user)
                                        .addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(), R.string.register_success, Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), R.string.register_failed, Toast.LENGTH_SHORT).show());

                                //Return information to the login page for connection
                                setResult(RESULT_OK, new Intent().putExtra("username", username).putExtra("password", password));
                                finish();
                            } else {
                                editUsername.setError(getString(R.string.username_exists));
                                editUsername.setText(username);
                                Utils.showKeyboard(getApplicationContext(), editUsername);
                            }
                        })
                        .addOnFailureListener(e -> {
                            //TODO
                            //Erreur si arrive pas à se connecter
                        });
            } else {
                //Sets the EditTexts without starting and ending spaces

                if (!firstName.matches(Utils.FIRSTNAME_REGEX)) {
                    editFirstName.setText(firstName.trim());
                    editFirstName.setError(getString(R.string.first_name_validation));
                }
                if (!lastName.matches(Utils.LASTNAME_REGEX)) {
                    editLastName.setText(lastName.trim());
                    editLastName.setError(getString(R.string.last_name_validation));
                }
                if (age <= 0) {
                    editAge.setText("");
                    editAge.setError(getString(R.string.invalid_age));
                }
                if (!username.matches(Utils.USERNAME_REGEX)) {
                    editUsername.setText(username.trim());
                    editUsername.setError(getString(R.string.username_validation));
                }
                if (!password.matches(Utils.PASSWORD_REGEX)) {
                    editPassword.setText("");
                    editPassword.setError(getString(R.string.password_validation));
                }
                Toast.makeText(getApplicationContext(), R.string.fields_must_be_valid, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.register_error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}