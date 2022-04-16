package uqac.dim.audium.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.MediaService;
import uqac.dim.audium.R;
import uqac.dim.audium.SliderAdapter;
import uqac.dim.audium.SliderItem;
import uqac.dim.audium.activity.MainActivity;
import uqac.dim.audium.activity.PlaylistListActivity;
import uqac.dim.audium.activity.admin.AddArtistActivity;
import uqac.dim.audium.activity.admin.ArtistListActivity;
import uqac.dim.audium.model.entity.Track;

public class HomeFragment extends Fragment {
    private ViewPager2 viewPager2;
    private final Handler sliderHandler = new Handler();
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };
    private final List<SliderItem> sliderItems = new ArrayList<>();
    private ImageButton imageButton;

    private final Context context;
    private MediaService mediaService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mediaService = ((MediaService.MediaServiceBinder) iBinder).getService();
            Log.i("DIM", "HomeFragment: onServiceConnected");
            getTracks();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mediaService = null;
            Log.i("DIM", "HomeFragment: onServiceDisconnected");
        }
    };

    public HomeFragment(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sliderItems.add(new SliderItem("https://media2.ledevoir.com/images_galerie/nwd_881002_701874/image.jpg"));
        sliderItems.add(new SliderItem("https://www.hhqc.com/wp-content/uploads/2019/10/sethgueko.jpg"));
        sliderItems.add(new SliderItem("https://media2.ledevoir.com/images_galerie/nwd_881002_701874/image.jpg"));
        sliderItems.add(new SliderItem("https://www.hhqc.com/wp-content/uploads/2019/10/sethgueko.jpg"));
        sliderItems.add(new SliderItem("https://media2.ledevoir.com/images_galerie/nwd_881002_701874/image.jpg"));
        sliderItems.add(new SliderItem("https://www.hhqc.com/wp-content/uploads/2019/10/sethgueko.jpg"));
        sliderItems.add(new SliderItem("https://media2.ledevoir.com/images_galerie/nwd_881002_701874/image.jpg"));
        sliderItems.add(new SliderItem("https://www.hhqc.com/wp-content/uploads/2019/10/sethgueko.jpg"));

        Intent intent = new Intent(context, MediaService.class);
        context.bindService(intent, serviceConnection, 0);
    }

    // Méthode temporaire pour tester le media player
    private void getTracks() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tracks");
        ref.get().addOnSuccessListener(dataSnapshot -> {
            List<Track> tracks = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Track track = snapshot.getValue(Track.class);
                    if (track != null) {
                        tracks.add(track);
                    }
                }
                mediaService.setTracks(tracks);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);

        //ViewPager
        viewPager2 = root.findViewById(R.id.viewPagerImageSlider);
        viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2, getContext()));
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


        imageButton = root.findViewById(R.id.playlist_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // New intent avec l'id du joueur
                Intent i = new Intent(getContext(), PlaylistListActivity.class);
                i.putExtra("username",MainActivity.user.getUsername());
                startActivity(i);
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

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}
