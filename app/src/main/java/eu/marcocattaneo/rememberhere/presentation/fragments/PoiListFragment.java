package eu.marcocattaneo.rememberhere.presentation.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.ListView;

import eu.marcocattaneo.rememberhere.MainActivity;
import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.callback.OnListListener;
import eu.marcocattaneo.rememberhere.business.controllers.ProximityController;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import eu.marcocattaneo.rememberhere.presentation.adapter.NotificationAdapter;
import eu.marcocattaneo.rememberhere.presentation.adapter.PoiAdapter;
import eu.marcocattaneo.rememberhere.presentation.base.BaseActivity;
import eu.marcocattaneo.rememberhere.presentation.base.BaseFragment;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class PoiListFragment extends BaseFragment implements OnListListener {

    public static PoiListFragment newInstance() {

        Bundle args = new Bundle();

        PoiListFragment fragment = new PoiListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BaseActivity mActivity;

    private RecyclerView poiList;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout noItemsLinear;

    private ListView notificationList;

    private FloatingActionButton mAddButton;

    private DrawerLayout drawer;

    private ProximityController controller;
    private PoiAdapter mAdapter;

    private View footerView = null;

    private final RealmChangeListener<RealmResults<ProximityPOI>> fullListListener = new RealmChangeListener<RealmResults<ProximityPOI>>() {

        @Override
        public void onChange(RealmResults<ProximityPOI> element) {
            noItemsLinear.setVisibility(element.size() > 0 ? View.GONE : View.VISIBLE);
            if (mAdapter == null) {
                mAdapter = new PoiAdapter(mActivity, element, PoiListFragment.this);
                poiList.setAdapter(mAdapter);
            } else {
                mAdapter.swapItems(element);
            }
        }
    };

    private final RealmChangeListener<RealmResults<ProximityPOI>> notifiesListListener = new RealmChangeListener<RealmResults<ProximityPOI>>() {

        @Override
        public void onChange(RealmResults<ProximityPOI> element) {
            if (element.size() > 0) {
                if (footerView != null)
                    notificationList.removeFooterView(footerView);
                notificationList.setAdapter(new NotificationAdapter(element));
            } else {
                notificationList.setAdapter(null);
                if (footerView == null)
                    footerView = LayoutInflater.from(mActivity).inflate(R.layout.view_nonotify, null);
                notificationList.addFooterView(footerView);
            }
        }
    };

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
        drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        notificationList = (ListView) view.findViewById(R.id.notification_list);

        mAddButton = (FloatingActionButton) view.findViewById(R.id.addPoi);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_OPERATION, MainActivity.OPERATION.OPEN_ADD_SECTION);
                startActivity(intent);
            }
        });

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
        controller.findProximityPOI(fullListListener);
        controller.findProximityPOIUpdates(notifiesListListener);
    }

    private void checkFabVisiblity() {
        if (mLayoutManager.findLastVisibleItemPosition() < mAdapter.getItemCount())
            mAddButton.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notify, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.show_notify:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                else
                    drawer.openDrawer(GravityCompat.END);
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
        notificationList.setAdapter(null);
        controller.onStop();
    }

    @Override
    public void onClickMap(View itemView, ProximityPOI poi, int position) {
        Intent intent = new Intent(mActivity, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_OPERATION, MainActivity.OPERATION.OPEN_ADD_SECTION);
        intent.putExtra(MainActivity.EXTRA_GUID, poi.getGuid());
        startActivity(intent);
    }

    @Override
    public void onClickDelete(View itemView, final ProximityPOI poi, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.delete_confirm);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mAdapter.notifyItemRemoved(position);

                // Remove geofence
                controller.removeGeofence(poi.getGuid());
                // Remove POI on DB
                controller.delete(poi);

                checkFabVisiblity();
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
