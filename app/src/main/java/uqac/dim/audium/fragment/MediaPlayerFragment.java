package uqac.dim.audium.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uqac.dim.audium.MediaService;
import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.Track;

public class MediaPlayerFragment extends Fragment implements MediaService.MediaEventListener {
    private final Context context;
    private MediaService mediaService;
    private ServiceConnection serviceConnection;

    private ImageButton btnPrevious, btnPlayPause, btnNext, btnLooping;
    private TextView tvTrackName, tvArtistName;
    private ImageView ivTrack;

    public MediaPlayerFragment(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setServiceConnection();
        Intent mediaServiceIntent = new Intent(context, MediaService.class);
        boolean successBind = context.bindService(mediaServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (successBind) {
            context.startService(mediaServiceIntent);
            Log.i("DIM", "media service started");
        } else {
            context.unbindService(serviceConnection);
            Log.e("DIM", "media service cannot be bind");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.media_player_fragment, container, false);

        btnPrevious = root.findViewById(R.id.btn_previous);
        btnPlayPause = root.findViewById(R.id.btn_play_pause);
        btnNext = root.findViewById(R.id.btn_next);
        btnLooping = root.findViewById(R.id.btn_looping);
        tvTrackName = root.findViewById(R.id.tv_track_name);
        tvArtistName = root.findViewById(R.id.tv_artist_name);
        ivTrack = root.findViewById(R.id.iv_track);

        btnPrevious.setOnClickListener(this::previous);
        btnPlayPause.setOnClickListener(this::playPause);
        btnNext.setOnClickListener(this::next);
        btnLooping.setOnClickListener(this::looping);

        btnPrevious.setEnabled(false);
        btnPlayPause.setEnabled(false);
        btnNext.setEnabled(false);
        btnLooping.setEnabled(false);

        return root;
    }

    private void previous(View view) {
        if (mediaService != null) {
            mediaService.previousTrack();
        }
    }

    private void playPause(View view) {
        if (mediaService != null && !mediaService.isPlaying()) {
            mediaService.play();
        } else if (mediaService != null && mediaService.isPlaying()) {
            mediaService.pause();
        }
    }

    private void next(View view) {
        if (mediaService != null) {
            mediaService.nextTrack();
        }
    }

    private void looping(View view) {
        if (mediaService != null) {
            mediaService.setLooping(!mediaService.isLooping());
            if (mediaService.isLooping()) {
                btnLooping.setImageResource(R.drawable.ic_baseline_repeat_enable_24);
            } else {
                btnLooping.setImageResource(R.drawable.ic_baseline_repeat_disable_24);
            }
        }
    }

    private void setServiceConnection() {
        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i("DIM", "setServiceConnection.onServiceConnected()");
                mediaService = ((MediaService.MediaServiceBinder) iBinder).getService();
                mediaService.addMediaEventListener(MediaPlayerFragment.this);

                btnPrevious.setEnabled(true);
                btnPlayPause.setEnabled(true);
                btnNext.setEnabled(true);
                btnLooping.setEnabled(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i("DIM", "setServiceConnection.onServiceDisconnected()");
                mediaService.removeMediaEventListener(MediaPlayerFragment.this);
                mediaService = null;

                btnPrevious.setEnabled(false);
                btnPlayPause.setEnabled(false);
                btnNext.setEnabled(false);
                btnLooping.setEnabled(false);
            }
        };
    }

    @Override
    public void onTrackPlay() {
        btnPlayPause.setImageResource(R.drawable.ic_outline_pause_circle_filled_24);
    }

    @Override
    public void onTrackPause() {
        btnPlayPause.setImageResource(R.drawable.ic_outline_play_circle_filled_24);
    }

    @Override
    public void onTrackChanged(Track track) {
        if (track != null) {
            tvTrackName.setText(track.getName());
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("artists")
                    .child(String.valueOf(track.getArtistId()));
            ref.get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            Artist artist = dataSnapshot.getValue(Artist.class);
                            if (artist != null) {
                                tvArtistName.setText(artist.getPrintableName());
                            } else {
                                tvArtistName.setText(R.string.artist_name_error);
                            }
                        } else {
                            tvArtistName.setText(R.string.artist_name_error);
                        }
                    })
                    .addOnFailureListener(e -> {
                        tvArtistName.setText(R.string.artist_name_error);
                    });
            //Changer la photo de la musique
        }
    }
}
