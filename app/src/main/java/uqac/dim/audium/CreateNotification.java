package uqac.dim.audium;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uqac.dim.audium.model.entity.Track;

public class CreateNotification  extends AsyncTask<String, Void, Bitmap> {

    public static final String CHANNEL_ID = "channel1";

    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_LOOP = "actionloop";
    public static final int NOTIFICATION_ID = 1;

    public static Notification notification;

    private Context context;
    private Track track;
    private int playbutton;
    private int loopbutton;


    private static Bitmap bm;

    public CreateNotification(Context context, Track track, int playbutton, int loopbutton){
        this.context=context;
        this.track=track;
        this.playbutton=playbutton;
        this.loopbutton=loopbutton;
    }

    @Override
    protected void onPostExecute(Bitmap result) {

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        try {
            URL url = new URL(track.getImageUrl());

            bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        }

        PendingIntent pendingIntentPrevious;
        Intent intentPrevious = new Intent(context, NotificationActionService.class).setAction(ACTION_PREVIOUS);
        pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPlay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentNext = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentLoop = new Intent(context, NotificationActionService.class).setAction(ACTION_LOOP);
        PendingIntent pendingIntentLoop = PendingIntent.getBroadcast(context, 0, intentLoop, PendingIntent.FLAG_UPDATE_CURRENT);


        notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notes)
                .setContentTitle(track.getName())
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(R.drawable.ic_round_skip_previous_24, "Previous", pendingIntentPrevious)
                .addAction(playbutton, "Play", pendingIntentPlay)
                .addAction(R.drawable.ic_round_skip_next_24, "Next", pendingIntentNext)
                //ajouter le looping
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .setLargeIcon(bm)
                .build();

        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        InputStream in;
        try {

            URL url = new URL(track.getImageUrl());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            in = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(in);
            return myBitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
