package eu.marcocattaneo.rememberhere.presentation.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.callback.OnClientAPIListener;
import eu.marcocattaneo.rememberhere.business.callback.OnQueryResult;
import eu.marcocattaneo.rememberhere.business.controllers.ProximityController;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import eu.marcocattaneo.rememberhere.presentation.base.BaseActivity;
import eu.marcocattaneo.rememberhere.presentation.base.BaseFragment;

public class MapFragment extends BaseFragment implements OnMapReadyCallback, OnClientAPIListener, OnQueryResult<ProximityPOI>, BottomSheetPlaceFragment.OnBottomSheetCallback {

    public static MapFragment newInstance() {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private GoogleMap mMap;

    private BaseActivity mActivity;
    private ProximityController mController;

    private LatLng currentLatLon;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getBaseActivity();

        mController = new ProximityController(mActivity, this);
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

        mController.findProximityPOI(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        mController.onStart();

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
        LatLng here = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(here).title("YOU ARE HERE").icon(getMarkerIcon("#327723")));

        if (moveCamera)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 14));
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    @Override
    public void onStop() {
        super.onStop();

        mController.onStop();
    }

    @Override
    public void onConnect(GoogleApiClient client) {
        putMyPosition(true);
    }

    @Override
    public void onConnectionFail(ConnectionResult result) {
        Toast.makeText(mActivity, "Errore with connection: " + result.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onData(List<ProximityPOI> proximityList) {
        mMap.clear();

        for (ProximityPOI poi : proximityList) {

            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(poi.getLatitude(), poi.getLongitude()))
                    .radius(ProximityController.RADIUS_METERS)
                    .strokeWidth(1)
                    .strokeColor(getContext().getResources().getColor(R.color.circleStroke))
                    .fillColor(getContext().getResources().getColor(R.color.circleFill))
            );

        }

        putMyPosition(false);

    }

}

