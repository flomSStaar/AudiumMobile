package uqac.dim.audium.fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import uqac.dim.audium.CreateNotification;
import uqac.dim.audium.MediaService;
import uqac.dim.audium.NotificationActionService;
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
    private SeekBar progressBar;

    private Track currentTrack;

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

        NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, CreateNotification.CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        getActivity().startService(new Intent(getActivity().getBaseContext(), NotificationActionService.class));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_media_player, container, false);

        btnPrevious = root.findViewById(R.id.btn_previous);
        btnPlayPause = root.findViewById(R.id.btn_play_pause);
        btnNext = root.findViewById(R.id.btn_next);
        btnLooping = root.findViewById(R.id.btn_looping);
        tvTrackName = root.findViewById(R.id.tv_track_name);
        tvArtistName = root.findViewById(R.id.tv_artist_name);
        ivTrack = root.findViewById(R.id.iv_track);
        progressBar = root.findViewById(R.id.progress_bar);

        btnPrevious.setOnClickListener(this::previous);
        btnPlayPause.setOnClickListener(this::playPause);
        btnNext.setOnClickListener(this::next);
        btnLooping.setOnClickListener(this::looping);

        btnPrevious.setEnabled(false);
        btnPlayPause.setEnabled(false);
        btnNext.setEnabled(false);
        btnLooping.setEnabled(false);
        progressBar.setEnabled(false);

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaService != null && fromUser) {
                    mediaService.seekTo(progress);
                }
            }
        });
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

                onTrackChanged(mediaService.getCurrentTrack());

                btnPrevious.setEnabled(true);
                btnPlayPause.setEnabled(true);
                btnNext.setEnabled(true);
                btnLooping.setEnabled(true);
                progressBar.setEnabled(true);
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
                progressBar.setEnabled(false);
            }
        };
    }

    private void createNotification(@DrawableRes int playPauseButtonId) {
        if (mediaService == null || currentTrack == null) {
            return;
        }
        int loopButton;
        if (mediaService.isLooping()) {
            loopButton = R.drawable.ic_baseline_repeat_enable_24;
        } else {
            loopButton = R.drawable.ic_outline_play_circle_filled_24;
        }
        CreateNotification createNotification = new CreateNotification(context, currentTrack, playPauseButtonId, loopButton);
        createNotification.execute();
    }

    @Override
    public void onTrackPlay() {
        btnPlayPause.setImageResource(R.drawable.ic_outline_pause_circle_filled_24);
        progressBar.setMax(mediaService.getDuration());
        createNotification(R.drawable.ic_outline_pause_circle_filled_24);
    }

    @Override
    public void onTrackPause() {
        btnPlayPause.setImageResource(R.drawable.ic_outline_play_circle_filled_24);
        createNotification(R.drawable.ic_outline_play_circle_filled_24);
    }

    @Override
    public void onTrackStop() {
        btnPlayPause.setImageResource(R.drawable.ic_outline_play_circle_filled_24);
        createNotification(R.drawable.ic_outline_play_circle_filled_24);
    }

    @Override
    public void onTrackChanged(Track track) {
        Log.i("DIM", "onTrackChanged()");
        if (track != null) {
            currentTrack = track;

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
            Picasso.with(context).load(Uri.parse(track.getImageUrl())).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(ivTrack);
            progressBar.setEnabled(true);
            if (mediaService != null && mediaService.isTrackPrepared()) {
                progressBar.setProgress(mediaService.getCurrentPosition());
                progressBar.setMax(mediaService.getDuration());
                if (mediaService.isPlaying()) {
                    btnPlayPause.setImageResource(R.drawable.ic_outline_pause_circle_filled_24);
                    createNotification(R.drawable.ic_outline_pause_circle_filled_24);
                } else {
                    createNotification(R.drawable.ic_outline_play_circle_filled_24);
                }
            } else {
                progressBar.setProgress(0);
                progressBar.setMax(1);
            }

            Handler mHandler = new Handler();
            //Make sure you update Seekbar on UI thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaService != null) {
                        int mCurrentPosition = mediaService.getCurrentPosition();
                        progressBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
        } else {
            tvTrackName.setText("No track selected");
            tvArtistName.setText("");
            ivTrack.setImageResource(R.drawable.ic_notes);
            progressBar.setEnabled(false);
            progressBar.setProgress(0);
            progressBar.setMax(1);
        }
    }
}
