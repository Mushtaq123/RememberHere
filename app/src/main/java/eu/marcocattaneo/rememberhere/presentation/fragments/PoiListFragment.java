package eu.marcocattaneo.rememberhere.presentation.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import eu.marcocattaneo.rememberhere.ContainerActivity;
import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.callback.OnListListener;
import eu.marcocattaneo.rememberhere.business.callback.OnQueryResult;
import eu.marcocattaneo.rememberhere.business.controllers.ProximityController;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import eu.marcocattaneo.rememberhere.presentation.SettingsActivity;
import eu.marcocattaneo.rememberhere.presentation.adapter.PoiAdapter;
import eu.marcocattaneo.rememberhere.presentation.base.BaseActivity;
import eu.marcocattaneo.rememberhere.presentation.base.BaseFragment;

public class PoiListFragment extends BaseFragment implements OnQueryResult<ProximityPOI>, OnListListener, View.OnClickListener {

    public static PoiListFragment newInstance() {

        Bundle args = new Bundle();

        PoiListFragment fragment = new PoiListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BaseActivity mActivity;

    private RecyclerView poiList;
    private LinearLayoutManager mLayoutManager;

    private FloatingActionButton mAddButtoon;

    private ProximityController controller;

    private LinearLayout noItemsLinear;

    private PoiAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getBaseActivity();

        controller = new ProximityController(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poilist, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noItemsLinear = (LinearLayout) view.findViewById(R.id.no_items);

        mAddButtoon = (FloatingActionButton) view.findViewById(R.id.addPoi);
        mAddButtoon.setOnClickListener(this);

        poiList = (RecyclerView) view.findViewById(R.id.poiList);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        poiList.setLayoutManager(mLayoutManager);

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        controller.onStartRealm();
        controller.onStartGoogleAPI(null);
        controller.findProximityPOI(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                Intent intent = new Intent(mActivity, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Prevent error with adapter on Realm
        mAdapter = null;
        poiList.setAdapter(null);
        controller.onStop();
    }

    @Override
    public void onData(List<ProximityPOI> data) {
        noItemsLinear.setVisibility(data.size() > 0 ? View.GONE : View.VISIBLE);
        if (mAdapter == null) {
            mAdapter = new PoiAdapter(mActivity, data, this);
            poiList.setAdapter(mAdapter);
        } else {
            mAdapter.swapItems(data);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mActivity, ContainerActivity.class);
        intent.putExtra(ContainerActivity.EXTRA_OPERATION, ContainerActivity.OPERATION.OPEN_ADD_SECTION);
        startActivity(intent);
    }

    @Override
    public void onClickMap(View itemView, ProximityPOI poi) {
        Intent intent = new Intent(mActivity, ContainerActivity.class);
        intent.putExtra(ContainerActivity.EXTRA_OPERATION, ContainerActivity.OPERATION.OPEN_ADD_SECTION);
        intent.putExtra(ContainerActivity.EXTRA_GUID, poi.getGuid());
        startActivity(intent);
    }

    @Override
    public void onClickDelete(View itemView, final ProximityPOI poi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.delete_confirm);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Remove geofence
                controller.removeGeofence(poi.getGuid());
                // Remove POI on DB
                controller.delete(poi);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Noting
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
