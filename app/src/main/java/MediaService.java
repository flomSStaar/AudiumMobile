import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;

public class MediaService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener {
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    MediaPlayer mediaPlayer;


    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = "https://firebasestorage.googleapis.com/v0/b/audium-4f845.appspot.com/o/musique.mp3?alt=media&token=f481b12b-c736-4dec-bb26-389212791cac";
        if (intent.getAction().equals(ACTION_PLAY)) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.prepareAsync(); // prepare async to not block main thread

            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            WifiManager.WifiLock wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                    .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

            wifiLock.acquire();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** Called when MediaPlayer is ready */
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
}
