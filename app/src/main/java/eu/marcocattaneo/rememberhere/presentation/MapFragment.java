package eu.marcocattaneo.rememberhere.presentation;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.callback.OnClientAPIListener;
import eu.marcocattaneo.rememberhere.business.callback.OnPoiListListener;
import eu.marcocattaneo.rememberhere.business.controllers.ProximityController;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import eu.marcocattaneo.rememberhere.presentation.adapter.PoiAdapter;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MapFragment extends Fragment implements OnMapReadyCallback, OnClientAPIListener, RealmChangeListener<RealmResults<ProximityPOI>>, OnPoiListListener {

    public static MapFragment newInstance() {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private GoogleMap mMap;

    private RecyclerView poiList;
    private PoiAdapter mAdapter;

    private AppBarLayout mAppBarLayout;

    private LinearLayoutManager mLayoutManager;

    private Context mContext;

    private ProximityController mController;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mController = new ProximityController(mContext, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        poiList = (RecyclerView) view.findViewById(R.id.poiList);
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);

        poiList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        poiList.setLayoutManager(mLayoutManager);

        poiList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    int lastVisibilePostiion = mLayoutManager.findLastVisibleItemPosition();
                    if (lastVisibilePostiion == mAdapter.getItemCount()) {
                        mAppBarLayout.setExpanded(true, true);
                    }

                }
            }
        });

        // Disable appLayout scroll
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mController.addPOI(latLng);
            }
        });

        // When ready
        RealmResults<ProximityPOI> pois = mController.getRealm().where(ProximityPOI.class).findAll();
        pois.addChangeListener(this);

        fillList(pois);
    }

    @Override
    public void onStart() {
        super.onStart();

        mController.onStart();
        if (mMap != null)
            onMapReady(mMap);
    }

    @Override
    public void onChange(RealmResults<ProximityPOI> pois) {
        fillList(pois);
    }

    private void putMyPosition(boolean moveCamera) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mController.getApiClient());
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

    private void fillList(RealmResults<ProximityPOI> pois) {
        if (mAdapter == null) {
            mAdapter = new PoiAdapter(pois, this);
            poiList.setAdapter(mAdapter);
        } else {
            mAdapter.swapItems(pois);
        }

        mMap.clear();
        for (ProximityPOI poi : pois) {

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

    @Override
    public void onStop() {
        super.onStop();

        mAdapter = null;
        poiList.setAdapter(null);
        mController.onStop();
    }

    @Override
    public void onConnect(GoogleApiClient client) {
        putMyPosition(true);
    }

    @Override
    public void onConnectionFail(ConnectionResult result) {

    }

    @Override
    public void onClick(View itemView, ProximityPOI poi) {
        LatLng here = new LatLng(poi.getLatitude(), poi.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 17));
    }

    @Override
    public void onLongPress(View itemView, final ProximityPOI poi) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setTitle(R.string.delete)
                .setMessage(R.string.delete_confirm)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mController.deletePOI(poi);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Noting
                    }
                }).create();
        alertDialog.show();
    }
}

