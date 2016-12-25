package eu.marcocattaneo.rememberhere.business.receivers;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import io.realm.Realm;

public class GeofenceTransitionsIntentService extends IntentService {

    public static int notificationId = 1;

    public static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            /*String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);*/
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            for (Geofence geofence : triggeringGeofences) {

                Realm realm = Realm.getDefaultInstance();

                ProximityPOI proximityPOI = realm.where(ProximityPOI.class).equalTo("guid", geofence.getRequestId()).findFirst();

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(proximityPOI.getNote());

                int mNotificationId = notificationId++;
                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notifysnd);
                mBuilder.setSound(sound);

                mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });


                mNotifyMgr.notify(mNotificationId, mBuilder.build());

            }

        }

    }
}
