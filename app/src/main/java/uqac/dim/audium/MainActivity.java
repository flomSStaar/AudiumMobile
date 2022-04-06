package uqac.dim.audium;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.model.entity.User;

public class MainActivity extends AppCompatActivity {
    private User user;

    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ViewPager
        viewPager2 = findViewById(R.id.viewPagerImageSlider);
        List<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem("https://media2.ledevoir.com/images_galerie/nwd_881002_701874/image.jpg"));
        sliderItems.add(new SliderItem("https://www.hhqc.com/wp-content/uploads/2019/10/sethgueko.jpg"));

        sliderItems.add(new SliderItem("https://media2.ledevoir.com/images_galerie/nwd_881002_701874/image.jpg"));
        sliderItems.add(new SliderItem("https://www.hhqc.com/wp-content/uploads/2019/10/sethgueko.jpg"));

        sliderItems.add(new SliderItem("https://media2.ledevoir.com/images_galerie/nwd_881002_701874/image.jpg"));
        sliderItems.add(new SliderItem("https://www.hhqc.com/wp-content/uploads/2019/10/sethgueko.jpg"));

        sliderItems.add(new SliderItem("https://media2.ledevoir.com/images_galerie/nwd_881002_701874/image.jpg"));
        sliderItems.add(new SliderItem("https://www.hhqc.com/wp-content/uploads/2019/10/sethgueko.jpg"));
        viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2, getApplicationContext()));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);

            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });


        ((Button) findViewById(R.id.btn_logout)).setOnClickListener(this::deconnection);
        initUser();

        if (user != null && user.isAdmin()) {
            initAdminMenu();
        }
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
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
