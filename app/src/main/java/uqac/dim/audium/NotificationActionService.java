package uqac.dim.audium;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("android.intent.action.MEDIA_BUTTON")
                .putExtra("actionname", intent.getAction()));
    }
}
