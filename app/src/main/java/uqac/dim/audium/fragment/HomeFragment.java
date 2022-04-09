package uqac.dim.audium.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.SliderAdapter;
import uqac.dim.audium.SliderItem;

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

    //A corriger!
//    @Override
//    protected void onPause() {
//        super.onPause();
//        sliderHandler.removeCallbacks(sliderRunnable);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        sliderHandler.postDelayed(sliderRunnable, 3000);
//    }
}