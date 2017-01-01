package eu.marcocattaneo.rememberhere;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;

import eu.marcocattaneo.rememberhere.presentation.base.BaseActivity;
import eu.marcocattaneo.rememberhere.presentation.fragments.MapFragment;
import eu.marcocattaneo.rememberhere.presentation.fragments.PoiListFragment;

public class MainActivity extends BaseActivity {

    public static final String EXTRA_OPERATION = "container.extra.op";
    public static final String EXTRA_GUID = "container.extra.guid";

    private Snackbar snackbarGpsErr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        Intent intent = getIntent();

        int operation = intent.hasExtra(EXTRA_OPERATION) ? getIntent().getIntExtra(EXTRA_OPERATION, 1) : 1;

        switch (operation) {

            case OPERATION.OPEN_LIST_SECTION:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, PoiListFragment.newInstance()).commit();
                break;

            case OPERATION.OPEN_ADD_SECTION:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, MapFragment.newInstance(intent.hasExtra(EXTRA_GUID) ? intent.getStringExtra(EXTRA_GUID) : null)).commit();
                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)  || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            hidGPSAlert();
        } else {
            showGPSAlert();
        }
    }

    public static class OPERATION {

        public static final int OPEN_LIST_SECTION = 1;

        public static final int OPEN_ADD_SECTION = 2;

    }

    private void showGPSAlert() {
        snackbarGpsErr = Snackbar.make(findViewById(R.id.mainActivity_coordinator), getString(R.string.gps_not_avaiable), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.enabled), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        hidGPSAlert();
                    }
                });
        snackbarGpsErr.show();
    }

    private void hidGPSAlert() {
        if (snackbarGpsErr != null)
            snackbarGpsErr.dismiss();
    }

}
