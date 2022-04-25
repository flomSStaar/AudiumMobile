package uqac.dim.audium;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

public class NotificationActionService extends Service {
    private BroadcastReceiver broadcastReceiver;
    private ServiceConnection serviceConnection;
    private MediaService mediaService;

    private void setBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getExtras().getString("actionname");

                switch (action) {
                    case CreateNotification.ACTION_PREVIOUS:
                        mediaService.previousTrack();
                        break;
                    case CreateNotification.ACTION_PLAY:
                        Log.e("DIM", "YOO!");
                        if (mediaService.isPlaying()) {
                            mediaService.pause();
                        } else {
                            mediaService.play();
                        }
                        break;
                    case CreateNotification.ACTION_NEXT:
                        mediaService.nextTrack();
                        break;
                    case CreateNotification.ACTION_LOOP:
                        mediaService.setLooping(!mediaService.isLooping());
                        break;
                }
            }
        };
    }

    private void setServiceConnection() {
        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i("DIM", "setServiceConnection.onServiceConnected()");
                mediaService = ((MediaService.MediaServiceBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i("DIM", "setServiceConnection.onServiceDisconnected()");
                mediaService = null;
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setServiceConnection();
        setBroadcastReceiver();

        Intent mediaServiceIntent = new Intent(getApplicationContext(), MediaService.class);
        boolean successBind = getApplicationContext().bindService(mediaServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (successBind) {
            getApplicationContext().startService(mediaServiceIntent);
            Log.i("DIM", "media service started");
        } else {
            getApplicationContext().unbindService(serviceConnection);
            Log.e("DIM", "media service cannot be bind");
        }
        getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.MEDIA_BUTTON"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getBaseContext());

        getApplicationContext().unregisterReceiver(broadcastReceiver);
        notificationManagerCompat.cancel(CreateNotification.NOTIFICATION_ID);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
