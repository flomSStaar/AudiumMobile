package uqac.dim.audiummobile;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import uqac.dim.audiummobile.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("DIM","Pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("DIM","Destroy");
    }
}
