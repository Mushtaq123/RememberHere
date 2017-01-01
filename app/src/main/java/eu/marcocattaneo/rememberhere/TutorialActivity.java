package eu.marcocattaneo.rememberhere;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.cleveroad.slidingtutorial.Direction;
import com.cleveroad.slidingtutorial.PageOptions;
import com.cleveroad.slidingtutorial.TransformItem;
import com.cleveroad.slidingtutorial.TutorialFragment;
import com.cleveroad.slidingtutorial.TutorialOptions;
import com.cleveroad.slidingtutorial.TutorialPageOptionsProvider;
import com.cleveroad.slidingtutorial.TutorialSupportFragment;

import eu.marcocattaneo.rememberhere.presentation.base.BaseActivity;

public class TutorialActivity extends BaseActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST = 493;

    private static final int TOTAL_PAGES = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? 2 :3;

    private static final String PREF_LOGGED = "eu.marcocattaneo.rememberhere.first";

    private SharedPreferences preferences;

    private TutorialFragment tutorialFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean(PREF_LOGGED, false))
            onPermissionGranted();

        int[] mPagesColors = new int[]{
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.white)
        };

        final TutorialOptions tutorialOptions = TutorialFragment.newTutorialOptionsBuilder(this)
                .setUseInfiniteScroll(false)
                .setPagesColors(mPagesColors)
                .setPagesCount(TOTAL_PAGES)
                .setTutorialPageProvider(new TutorialPagesProvider())
                .setOnSkipClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tutorialFragment.getViewPager().setCurrentItem(TOTAL_PAGES - 1);
                        tutorialFragment.getSkipButton().setVisibility(View.GONE);
                    }
                })
                .build();

        tutorialFragment =  TutorialSupportFragment.newInstance(tutorialOptions);
        getFragmentManager().beginTransaction().replace(R.id.container, tutorialFragment).commit();
    }

    /**
     * Permission granted
     */
    private void onPermissionGranted() {
        preferences.edit().putBoolean(PREF_LOGGED, true).apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestPermissionAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST);

        } else {
            onPermissionGranted();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.permission:
                requestPermissionAndOpen();
                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted();
                } else {
                    Toast.makeText(this, "Permessi necessari. ", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static final class TutorialPagesProvider implements TutorialPageOptionsProvider {

        @NonNull
        @Override
        public PageOptions provide(int position) {
            @LayoutRes int pageLayoutResId;

            TransformItem[] tutorialItems = new TransformItem[]{
                    TransformItem.create(R.id.image, Direction.LEFT_TO_RIGHT, 0.06f),
            };
            switch (position) {
                case 0: {
                    pageLayoutResId = R.layout.fragment_tutorial1;
                    break;
                }
                case 1: {
                    pageLayoutResId = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? R.layout.fragment_tutorial2_button : R.layout.fragment_tutorial2;
                    break;
                }
                case 2: {
                    pageLayoutResId = R.layout.fragment_tutorial3;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown position: " + position);
                }
            }

            return PageOptions.create(pageLayoutResId, position, tutorialItems);
        }
    }

}

