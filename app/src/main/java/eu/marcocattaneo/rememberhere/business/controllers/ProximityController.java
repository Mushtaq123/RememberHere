package eu.marcocattaneo.rememberhere.business.controllers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.callback.OnClientAPIListener;
import eu.marcocattaneo.rememberhere.business.receivers.GeofenceTransitionsIntentService;

public class ProximityController extends BaseController implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int RADIUS_METERS = 80;

    private PendingIntent mPendingIntent;

    private GoogleApiClient mGoogleApiClient;

    private OnClientAPIListener mCallback;

    public ProximityController(Context context, @NonNull OnClientAPIListener onClientAPIListener) {
        super(context);

        this.mCallback = onClientAPIListener;
    }

    public void addPOI(final double latitude, final double longitude, final String note) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final String guid = UUID.randomUUID().toString();

        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(getGeofencingRequestList(guid, latitude, longitude)),
                getGeofencePendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {

            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {

                    getDao().create(guid, note, RADIUS_METERS, latitude, longitude);

                } else {
                    Toast.makeText(getContext(), R.string.err_insert, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Start controller
     */
    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();
        }
    }

    /**
     * Create geofence POI
     *
     * @return
     */
    private List<Geofence> getGeofencingRequestList(String guid, double latitude, double longitude) {

        List<Geofence> mGeofenceList = new ArrayList<>();

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(guid)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(latitude, longitude, RADIUS_METERS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        return mGeofenceList;
    }

    /**
     * Build GeoFence request
     *
     * @param mGeofenceList
     * @return
     */
    private GeofencingRequest getGeofencingRequest(List<Geofence> mGeofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    /**
     * Return PendingIntent request
     *
     * @return
     */
    private PendingIntent getGeofencePendingIntent() {
        if (mPendingIntent != null)
            return mPendingIntent;
        // Reuse the PendingIntent if we already have it.
        Intent intent = new Intent(getContext(), GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return mPendingIntent = PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mCallback.onConnect(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mCallback.onConnectionFail(connectionResult);
    }

    /**
     * Stop controller
     */
    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        mGoogleApiClient = null;
    }

    /**
     * Return Google API Client
     *
     * @return
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
}
