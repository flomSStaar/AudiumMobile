package uqac.dim.audium;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import uqac.dim.audium.fragment.ArtistPageFragment;
import uqac.dim.audium.fragment.HomeFragment;
import uqac.dim.audium.fragment.PlaylistPageFragment;
import uqac.dim.audium.model.entity.Artist;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private Context context;
    private List<Artist> sliderItems;
    private ViewPager2 viewPager2;
    Fragment fragment;

    public SliderAdapter(List<Artist> sliderItems, ViewPager2 viewPager2, Fragment context) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
        this.fragment = context;
        this.context = fragment.getContext();
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slideritem_container,
                        parent,
                        false
                ), context
        );
    }



    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(new SliderItem(sliderItems.get(position).getImageUrl()));
        if (position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }
        holder.setOnClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArtistPageFragment artistPageFragment = new ArtistPageFragment();
                Bundle b = new Bundle();
                b.putString("username", fragment.getArguments().getString("username"));
                b.putLong("artistId", sliderItems.get(holder.getAdapterPosition()).getId());
                artistPageFragment.setArguments(b);
                FragmentManager manager = fragment.getParentFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, artistPageFragment)
                        .addToBackStack("mainPage")
                        .commit();
            }

        });
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;
        private Context context;
        private View itemView;

        SliderViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.imageslide);
        }

        void setOnClickedListener(View.OnClickListener listener){
            itemView.setOnClickListener(listener);
        }

        void setImage(SliderItem sliderItem) {
            Picasso.with(context)
                    .load(Uri.parse(sliderItem.getImage()))
                    .into(imageView);
            //imageView.setImageResource(sliderItem.getImage());
        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };


}
