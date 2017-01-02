package eu.marcocattaneo.rememberhere.presentation.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.callback.OnActivityForResultCallback;
import eu.marcocattaneo.rememberhere.business.callback.OnClientAPIListener;
import eu.marcocattaneo.rememberhere.business.callback.OnQueryResult;
import eu.marcocattaneo.rememberhere.business.controllers.ProximityController;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import eu.marcocattaneo.rememberhere.presentation.base.BaseActivity;
import eu.marcocattaneo.rememberhere.presentation.base.BaseFragment;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MapFragment extends BaseFragment implements OnMapReadyCallback, OnClientAPIListener, BottomSheetPlaceFragment.OnBottomSheetCallback, OnActivityForResultCallback {

    public static MapFragment newInstance(String guid) {

        Bundle args = new Bundle();
        if (guid != null)
            args.putString("guid", guid);

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 321;

    private GoogleMap mMap;

    private BaseActivity mActivity;
    private ProximityController mController;

    private LatLng currentLatLon;
    private LatLng here;

    private String guid = null;

    private final RealmChangeListener<RealmResults<ProximityPOI>> onDataChangeLisnter = new RealmChangeListener<RealmResults<ProximityPOI>>() {

        @Override
        public void onChange(RealmResults<ProximityPOI> element) {
            mMap.clear();

            for (ProximityPOI poi : element) {

                if (poi.isExpired()) {
                    mMap.addCircle(new CircleOptions()
                            .center(new LatLng(poi.getLatitude(), poi.getLongitude()))
                            .radius(ProximityController.RADIUS_METERS)
                            .strokeWidth(1)
                            .strokeColor(getContext().getResources().getColor(R.color.disableCircleStroke))
                            .fillColor(getContext().getResources().getColor(R.color.disableCircleFill))
                    );
                } else {
                    mMap.addCircle(new CircleOptions()
                            .center(new LatLng(poi.getLatitude(), poi.getLongitude()))
                            .radius(ProximityController.RADIUS_METERS)
                            .strokeWidth(1)
                            .strokeColor(getContext().getResources().getColor(R.color.activeCircleStroke))
                            .fillColor(getContext().getResources().getColor(R.color.activeCircleFill))
                    );
                }

            }

            putMyPosition(false);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getBaseActivity();

        mController = new ProximityController(mActivity, this);

        guid = getArguments().getString("guid", null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setBackEnable(true);
        setHasOptionsMenu(true);

        mActivity.addOnActivityForResult(PLACE_AUTOCOMPLETE_REQUEST_CODE, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                currentLatLon = latLng;
                BottomSheetPlaceFragment bottomSheetDialogFragment = new BottomSheetPlaceFragment();
                bottomSheetDialogFragment.setSubmitCallback(MapFragment.this);
                bottomSheetDialogFragment.show(mActivity.getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        mController.findProximityPOI(onDataChangeLisnter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.search:
                try {
                    Intent intent;
                    if (here != null) {
                        intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setBoundsBias(new LatLngBounds(here, here)).build(mActivity);
                    } else {
                        intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(mActivity);
                    }
                    mActivity.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                } catch (GooglePlayServicesNotAvailableException e) {
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onStart() {
        super.onStart();

        mController.onStartRealm();
        mController.onStartGoogleAPI(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onSubmit(String note) {
        mController.addPOI(currentLatLon.latitude, currentLatLon.longitude, note);
    }

    private void putMyPosition(boolean moveCamera) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mController.getGoogleApiClient());
        if (mLastLocation == null)
            return;
        here = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(here).title("YOU ARE HERE").icon(getMarkerIcon("#327723")));

        if (moveCamera)
            moveCamera(here);
    }

    private void moveCamera(LatLng position) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mActivity.removeOnActivityForResult(PLACE_AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onStop() {
        super.onStop();

        mController.onStop();
    }

    @Override
    public void onConnect(GoogleApiClient client) {
        if (guid == null)
            putMyPosition(true);
        else {
            ProximityPOI poi = mController.findProximityPOIByGuid(guid);
            if (poi != null) {
                LatLng latLng = new LatLng(poi.getLatitude(), poi.getLongitude());
                moveCamera(latLng);
            }
        }
    }

    @Override
    public void onConnectionFail(ConnectionResult result) {
        Toast.makeText(mActivity, "Errore with connection: " + result.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityForResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(mActivity, data);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(mActivity, data);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}

