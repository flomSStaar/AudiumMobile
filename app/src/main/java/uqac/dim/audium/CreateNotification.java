package uqac.dim.audium;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import uqac.dim.audium.activity.Login;
import uqac.dim.audium.activity.Main;
import uqac.dim.audium.activity.Settings;
import uqac.dim.audium.fragment.MediaPlayerFragment;
import uqac.dim.audium.model.entity.Track;

public class CreateNotification extends AsyncTask<String, Void, Bitmap> {

    public static final String CHANNEL_ID = "channel1";
    public static final String CHANNEL_NAME = "MediaPlayer";

    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_LOOP = "actionloop";

    public static final int NOTIFICATION_ID = 1;
    public Notification notification;

    private final Context context;
    private final Track track;
    private int playButton;
    private int loopButton;

    public CreateNotification(Context context, Track track, @DrawableRes int playButton, @DrawableRes int loopButton) {
        this.context = context;
        this.track = track;
        this.playButton = playButton;
        this.loopButton = loopButton;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            URL url = new URL(track.getImageUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            Log.e("DIM", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        PendingIntent pendingIntentPrevious;
        Intent intentPrevious = new Intent(context, NotificationActionService.class).setAction(ACTION_PREVIOUS);
        pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPlay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentNext = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentLoop = new Intent(context, NotificationActionService.class).setAction(ACTION_LOOP);
        PendingIntent pendingIntentLoop = PendingIntent.getBroadcast(context, 0, intentLoop, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent openIntent = PendingIntent.getActivity(context, 0, new Intent(context, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_UPDATE_CURRENT);




        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notes)
                .setContentTitle(track.getName())
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(R.drawable.ic_round_skip_previous_24, "Previous", pendingIntentPrevious)
                .addAction(playButton, "Play", pendingIntentPlay)
                .addAction(R.drawable.ic_round_skip_next_24, "Next", pendingIntentNext)
                //TODO ajouter le looping
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .setLargeIcon(result);

        builder.setContentIntent(openIntent);



        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }
}
