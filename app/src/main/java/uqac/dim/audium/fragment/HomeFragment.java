package uqac.dim.audium.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.SliderAdapter;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Playlist;

public class HomeFragment extends Fragment {

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private String username;
    private View root;
    private ImageButton btnAlbums;
    private ViewPager2 viewPager2;
    private LinearLayout linearLayoutAlbums;
    private LinearLayout linearLayoutPlaylists;
    private final Handler sliderHandler = new Handler();
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };
    private final List<Artist> sliderItems = new ArrayList<>();
    private ImageButton btnPlaylists;

    private final Context context;

    public HomeFragment(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = getArguments().getString("username");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager2 = root.findViewById(R.id.viewPagerImageSlider);
        linearLayoutAlbums = root.findViewById(R.id.albums_list);
        linearLayoutPlaylists = root.findViewById(R.id.playlists_list);
        btnAlbums = root.findViewById(R.id.btn_see_albums);
        btnPlaylists = root.findViewById(R.id.playlist_btn);

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
        setHomePage();
        getPlaylistsButtons();
        getAlbumsButtons();
    }

    private void setHomePage() {
        database.child("artists").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Artist a = data.getValue(Artist.class);
                        if (a != null) {
                            sliderItems.add(a);
                        }
                    }
                    viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2, HomeFragment.this));
                    viewPager2.setClipToPadding(false);
                    viewPager2.setClipChildren(false);
                    viewPager2.setOffscreenPageLimit(3);
                    viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                    compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                    compositePageTransformer.addTransformer((page, position) -> {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    });

                    btnAlbums.setOnClickListener(view -> {
                        AlbumsFragment albumsFragment = new AlbumsFragment();
                        Bundle b = new Bundle();
                        b.putString("username", username);
                        albumsFragment.setArguments(b);
                        FragmentManager manager = getParentFragmentManager();
                        manager.beginTransaction()
                                .replace(R.id.fragment_container, albumsFragment)
                                .addToBackStack("albums")
                                .commit();
                    });

                    btnPlaylists.setOnClickListener(view -> {
                        PlaylistFragment playlistFragment = new PlaylistFragment(context);
                        Bundle b = new Bundle();
                        b.putString("username", username);
                        playlistFragment.setArguments(b);
                        FragmentManager manager = getParentFragmentManager();
                        manager.beginTransaction()
                                .replace(R.id.fragment_container, playlistFragment)
                                .addToBackStack("playlist")
                                .commit();
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
                }
            }
        });
    }

    private void getPlaylistsButtons() {
        database.child("playlists").child(username).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                linearLayoutPlaylists.removeAllViews();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Playlist playlist = data.getValue(Playlist.class);
                    if (playlist != null) {
                        View view = getLayoutInflater().inflate(R.layout.grid_view_item, linearLayoutPlaylists, false);
                        TextView tvPlaylistName = view.findViewById(R.id.gv_playlist_name);
                        ImageView ivPlaylist = view.findViewById(R.id.playlist_image);

                        tvPlaylistName.setText(playlist.getTitle());
                        Picasso.with(getContext())
                                .load(playlist.getImageUrl())
                                .placeholder(R.drawable.ic_notes)
                                .error(R.drawable.ic_notes)
                                .into(ivPlaylist);

                        view.setOnClickListener(view1 -> {
                            PlaylistPageFragment playlistPageFragment = new PlaylistPageFragment(context);
                            Bundle b = new Bundle();
                            b.putString("username", username);
                            b.putLong("playlistId", playlist.getId());
                            playlistPageFragment.setArguments(b);
                            FragmentManager manager = getParentFragmentManager();
                            manager.beginTransaction()
                                    .replace(R.id.fragment_container, playlistPageFragment)
                                    .addToBackStack("mainPage")
                                    .commit();
                        });
                        linearLayoutPlaylists.addView(view);
                    }
                }
            } else {
                TextView noPlaylists = new TextView(getContext());
                noPlaylists.setText(R.string.no_playlist);
                noPlaylists.setTextColor(Color.WHITE);
                noPlaylists.setLayoutParams((new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
                linearLayoutPlaylists.removeAllViews();
                linearLayoutPlaylists.addView(noPlaylists);
            }
        });

    }

    private void getAlbumsButtons() {
        database.child("albums").limitToLast(5).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                linearLayoutAlbums.removeAllViews();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Album album = data.getValue(Album.class);
                    if (album != null) {
                        View view = getLayoutInflater().inflate(R.layout.grid_view_album_item, linearLayoutAlbums, false);
                        TextView tvAlbumName = view.findViewById(R.id.gv_album_name);
                        TextView tvArtistName = view.findViewById(R.id.gv_artist_name);
                        ImageView ivAlbum = view.findViewById(R.id.gv_album_image);

                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        database.child("artists")
                                .child(String.valueOf(album.getArtistId())).get()
                                .addOnSuccessListener(artistSnapshot -> {
                                    if (artistSnapshot.exists()) {
                                        Artist artist = artistSnapshot.getValue(Artist.class);
                                        if (artist != null) {
                                            tvArtistName.setText(artist.getPrintableName());
                                        } else {
                                            tvArtistName.setText("");
                                        }
                                    }
                                });
                        tvAlbumName.setText(album.getTitle());
                        Picasso.with(context)
                                .load(album.getImageUrl())
                                .placeholder(R.drawable.ic_notes)
                                .error(R.drawable.ic_notes)
                                .into(ivAlbum);

                        view.setOnClickListener(view1 -> {
                            AlbumPageFragment albumPageFragment = new AlbumPageFragment();
                            Bundle b = new Bundle();
                            b.putString("username", username);
                            b.putLong("albumId", album.getId());
                            albumPageFragment.setArguments(b);
                            FragmentManager manager = getParentFragmentManager();
                            manager.beginTransaction()
                                    .replace(R.id.fragment_container, albumPageFragment)
                                    .addToBackStack("albumPage")
                                    .commit();
                        });
                        linearLayoutAlbums.addView(view);
                    }
                }
            } else {
                TextView noAlbums = new TextView(getContext());
                noAlbums.setText(R.string.no_album);
                noAlbums.setLayoutParams((new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
                linearLayoutAlbums.removeAllViews();
                linearLayoutAlbums.addView(noAlbums);
            }
        });
    }
}
