package uqac.dim.audium;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.model.entity.Track;

public class MediaService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private IBinder binder;
    private boolean isServiceStarted = false;

    private boolean isPlayingRequested = false;
    private boolean isPrepared = false;
    private boolean looping = false;
    private int currentPlayingIndex = 0;
    private int maxPlayingIndex = 0;
    private Track currentTrack;
    private List<Track> tracks;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("DIM", "MediaService.onCreate()");

        mediaPlayer = new MediaPlayer();
        binder = new MediaServiceBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("DIM", "MediaService.onDestroy()");

        mediaPlayer.release();
    }

    // Service

    public class MediaServiceBinder extends Binder {
        public MediaService getService() {
            Log.i("DIM", "MediaServiceBinder.getService()");
            return MediaService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DIM", "MediaService.onStartCommand()");

        if (!isServiceStarted) {
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            isServiceStarted = true;
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("DIM", "MediaService.onBind()");
        return binder;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.i("DIM", "MediaService.onPrepared()");
        isPrepared = true;
        if (isPlayingRequested) {
            mediaPlayer.start();
            isPlayingRequested = false;
            notifyPlay();

            Log.i("DIM", currentTrack.getName());
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextTrack();
    }

    public void setTracks(List<Track> tracks) {
        Log.i("DIM", "MediaService.setTracks()");

        if (tracks != null && tracks.size() > 0) {
            //Creates a copy of the track list
            this.tracks = new ArrayList<>(tracks);
            this.maxPlayingIndex = this.tracks.size();
        } else {
            throw new IllegalArgumentException("tracks cannot be null or empty");
        }
    }

    // MediaPlayer Controls

    public void play() {
        Log.i("DIM", "MediaService.play()");

        if (tracks != null) {
            if (currentTrack == null) {
                prepareTrack(true, 0);
            } else if (isPrepared) {
                mediaPlayer.start();
                notifyPlay();
            } else {
                isPlayingRequested = true;
            }
        } else {
            Log.w("DIM", "MediaService.play(): tracks is not initialized");
        }
    }

    public void pause() {
        Log.i("DIM", "MediaService.pause()");
        mediaPlayer.pause();
        notifyPause();
    }

    public void nextTrack() {
        Log.i("DIM", "MediaService.nextTrack()");

        if (tracks != null) {
            int nextPlayingIndex = (currentPlayingIndex + 1) % maxPlayingIndex;
            if (currentPlayingIndex == maxPlayingIndex - 1) {
                if (!looping) {
                    notifyPause();
                }
                prepareTrack(looping, nextPlayingIndex);
            } else {
                prepareTrack(true, nextPlayingIndex);
            }
        } else {
            Log.w("DIM", "MediaService.nextTrack(): tracks is not initialized");
        }
    }

    public void previousTrack() {
        Log.i("DIM", "MediaService.previousTrack()");

        if (tracks != null) {
            int previousPlayingIndex = (currentPlayingIndex - 1) % maxPlayingIndex;
            if (currentPlayingIndex == 0) {
                if (looping) {
                    prepareTrack(true, previousPlayingIndex);
                }
            } else {
                prepareTrack(true, previousPlayingIndex);
            }
        } else {
            Log.w("DIM", "MediaService.previousTrack(): tracks is not initialized");
        }
    }

    private void prepareTrack(boolean isPlayingRequested, int newPlayingIndex) {
        Log.i("DIM", "MediaService.prepareTrack()");

        try {
            if (newPlayingIndex == currentPlayingIndex && isPrepared) {
                if (isPlayingRequested) {
                    mediaPlayer.start();
                    notifyPlay();
                }
                return;
            }
            this.isPrepared = false;
            this.currentPlayingIndex = newPlayingIndex;
            this.currentTrack = tracks.get(currentPlayingIndex);
            this.isPlayingRequested = isPlayingRequested;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(currentTrack.getUrl());
            mediaPlayer.prepareAsync();
            notifyTrackChanged();
        } catch (IOException e) {
            Log.e("DIM", "MediaService.prepareTrack(): Error mediaPlayer.setDataSource()");
            e.printStackTrace();
        }
    }

    // Others

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    // Events

    private final List<MediaEventListener> listeners = new ArrayList<>();

    public void addMediaEventListener(MediaEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeMediaEventListener(MediaEventListener listener) {
        listeners.remove(listener);
    }

    private void notifyPlay() {
        listeners.forEach(MediaEventListener::onTrackPlay);
    }

    private void notifyPause() {
        listeners.forEach(MediaEventListener::onTrackPause);
    }

    private void notifyTrackChanged() {
        listeners.forEach(listener -> listener.onTrackChanged(currentTrack));
    }

    public interface MediaEventListener {
        void onTrackPlay();

        void onTrackPause();

        void onTrackChanged(Track track);
    }
}
