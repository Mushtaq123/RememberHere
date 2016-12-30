package eu.marcocattaneo.rememberhere.business.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import eu.marcocattaneo.rememberhere.business.controllers.BaseController;
import eu.marcocattaneo.rememberhere.business.controllers.ProximityController;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_NOTIFICATION_DONE = "eu.marcocattaneo.rememberhere.business.receivers.NotificationReceiver.done";
    public static final String EXTRA_POI_GUID = "eu.marcocattaneo.rememberhere.business.receivers.NotificationReceiver.guid";
    public static final String EXTRA_NOTIFICATION_ID = "eu.marcocattaneo.rememberhere.business.receivers.NotificationReceiver.notificationId";

    @Override
    public void onReceive(Context context, Intent intent) {

        BaseController controller = new BaseController(context);
        controller.onStart();

        ProximityPOI poi = controller.findProximityPOIByGuid(intent.getStringExtra(EXTRA_POI_GUID));
        int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1);

        switch (intent.getAction()) {

            case ACTION_NOTIFICATION_DONE:
                if (poi != null)
                    controller.setDone(poi);
                clearNotification(context, notificationId);
                break;

        }

        controller.onStop();

    }

    /**
     * Remove notification
     * @param context
     * @param notificationId
     */
    private void clearNotification(Context context, int notificationId) {

        if (notificationId > 0) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
        }
    }

}
