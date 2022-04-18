package uqac.dim.audium;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.URL;

import uqac.dim.audium.model.entity.Track;

public class CreateNotification {

    public static final String CHANNEL_ID="channel1";

    public static final String ACTION_PREVIOUS="actionprevious";
    public static final String ACTION_PLAY="actionplay";

    public static final String ACTION_NEXT="actionnext";

    public static Notification notification;

    private static Bitmap bm;

    public static void createNotification(Context context, Track track, int playbutton){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");





            try {
                URL url = new URL(track.getImageUrl());
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch(IOException e) {
                System.out.println(e);
            }




            PendingIntent pendingIntentPrevious;
            Intent intentPrevious = new Intent(context, NotificationActionService.class).setAction(ACTION_PREVIOUS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);


            Intent intentPlay= new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intentNext= new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
            PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 0,intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notes)
                    .setContentTitle(track.getName())
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .addAction(R.drawable.ic_round_skip_previous_24, "Previous", pendingIntentPrevious)
                    .addAction(playbutton, "Play", pendingIntentPlay)
                    .addAction(R.drawable.ic_round_skip_next_24,"Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                        )
                    .setLargeIcon(bm)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            notificationManagerCompat.notify(1, notification);

        }

    }
}
