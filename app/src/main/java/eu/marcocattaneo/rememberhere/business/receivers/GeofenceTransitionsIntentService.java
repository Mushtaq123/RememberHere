package eu.marcocattaneo.rememberhere.business.receivers;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.controllers.BaseController;
import eu.marcocattaneo.rememberhere.business.controllers.ProximityController;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import io.realm.Realm;

import static eu.marcocattaneo.rememberhere.business.receivers.NotificationReceiver.ACTION_NOTIFICATION_DONE;

public class GeofenceTransitionsIntentService extends IntentService {

    public static int notificationId = 1;

    private static final String GROUP_KEY_NOTIFY = "group_geofe";

    public static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    private BaseController controller;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Start controller
        controller = new BaseController(this);
        controller.onStart();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        // Detect transition time
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER/* || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT*/) {

            // Get results
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Each results
            for (Geofence geofence : triggeringGeofences) {

                int mNotificationId = notificationId++;

                // Pending Intent
                Intent actionIntent = new Intent(NotificationReceiver.ACTION_NOTIFICATION_DONE);
                actionIntent.putExtra(NotificationReceiver.EXTRA_POI_GUID, geofence.getRequestId());
                actionIntent.putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId);
                PendingIntent pIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), actionIntent, 0);

                // Get POI
                ProximityPOI proximityPOI = controller.findProximityPOIByGuid(geofence.getRequestId());
                if (proximityPOI == null)
                    return;

                controller.setExpired(proximityPOI);

                // Build notification with pendig actions
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.app_name))
                                .setGroup(GROUP_KEY_NOTIFY)
                                // Done task
                                .addAction(R.drawable.ic_done_black_24dp, getString(R.string.task_done), pIntent)
                                .setAutoCancel(true)
                                .setContentText(proximityPOI.getNote());

                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notifysnd);
                mBuilder.setSound(sound);

                mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

                mNotifyMgr.notify(mNotificationId, mBuilder.build());

            }

        }

        // end controller
        controller.onStop();

    }
}
