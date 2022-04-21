package uqac.dim.audium;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

public class OnClearFromRecentService extends Service {
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

        notificationManagerCompat.cancel(CreateNotification.NOTIFICATION_ID);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
