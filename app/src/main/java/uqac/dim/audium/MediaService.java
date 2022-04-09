package uqac.dim.audium;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

public class MediaService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private MediaPlayer mediaPlayer;


    public int onStartCommand(Intent intent, int flags, int startId) {

        String url = "https://firebasestorage.googleapis.com/v0/b/audium-4f845.appspot.com/o/tracks%2F1?alt=media&token=b32941f9-60b3-4979-bd76-88e85195d87d";

        if (intent.getAction().equals(ACTION_PLAY)) {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);

            mediaPlayer.setOnErrorListener(this);
            Log.i("DIM", "zzedezdeez");
            mediaPlayer.prepareAsync(); // prepare async to not block main thread

            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            WifiManager.WifiLock wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                    .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

            wifiLock.acquire();

        }
        //A changer
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called when MediaPlayer is ready
     */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();
    }
}
