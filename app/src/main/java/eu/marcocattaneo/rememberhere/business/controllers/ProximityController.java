package eu.marcocattaneo.rememberhere.business.controllers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.callback.OnClientAPIListener;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import eu.marcocattaneo.rememberhere.business.receivers.GeofenceTransitionsIntentService;
import eu.marcocattaneo.rememberhere.presentation.ui.EditTextDialog;

public class ProximityController extends BaseController implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int RADIUS_METERS = 50;

    private PendingIntent mPendingIntent;

    private GoogleApiClient mGoogleApiClient;

    private OnClientAPIListener mCallback;

    public ProximityController(Context context, OnClientAPIListener callback) {
        super(context);

        this.mCallback = callback;
    }

    private String currentGuid;

    private LatLng currentCoordinates;

    public void addPOI(LatLng latLng) {
        this.currentCoordinates = latLng;
        this.currentGuid = UUID.randomUUID().toString();

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(getGeofencingRequestList()),
                getGeofencePendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {

            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {

                    EditTextDialog editTextDialog = new EditTextDialog(getContext());
                    editTextDialog.setTitle(R.string.string_note);
                    editTextDialog.setConfirmDialog(getContext().getResources().getString(R.string.ok), new EditTextDialog.DialogInterface() {
                        @Override
                        public void onClick(EditTextDialog dialog) {

                            getRealm().beginTransaction();

                            ProximityPOI poi = getRealm().createObject(ProximityPOI.class, currentGuid);
                            poi.setNote(dialog.getValue());
                            poi.setLatitude(currentCoordinates.latitude);
                            poi.setLongitude(currentCoordinates.longitude);

                            getRealm().commitTransaction();
                            Toast.makeText(getContext(), R.string.ok_insert, Toast.LENGTH_SHORT).show();

                        }
                    });
                    editTextDialog.show();
                } else {
                    Toast.makeText(getContext(), R.string.err_insert, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Create geofence POI
     * @return
     */
    private List<Geofence> getGeofencingRequestList() {

        List<Geofence> mGeofenceList = new ArrayList<>();

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(currentGuid)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(
                        currentCoordinates.latitude,
                        currentCoordinates.longitude,
                        RADIUS_METERS
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        return mGeofenceList;
    }

    /**
     * Build GeoFence request
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
        if (mCallback != null)
            mCallback.onConnect(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mCallback != null)
            mCallback.onConnectionFail(connectionResult);
    }

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

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        mGoogleApiClient = null;
    }

    public void deletePOI(final ProximityPOI poi) {
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                getGeofencePendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                getRealm().beginTransaction();
                poi.deleteFromRealm();
                getRealm().commitTransaction();
                Toast.makeText(getContext(), R.string.ok_delete, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public GoogleApiClient getApiClient() {
        return mGoogleApiClient;
    }
}
