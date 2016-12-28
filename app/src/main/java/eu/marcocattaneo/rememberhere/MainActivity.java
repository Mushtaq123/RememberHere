package eu.marcocattaneo.rememberhere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import eu.marcocattaneo.rememberhere.presentation.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSION_REQUEST = 493;

    private static final int RC_SIGN_IN = 343;

    private static final String PREF_LOGGED = "pref:login";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean(PREF_LOGGED, false))
            onPermissionGranted();

        findViewById(R.id.confirm).setOnClickListener(this);

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/
    }

    /**
     * Permission granted
     */
    private void onPermissionGranted() {
        preferences.edit().putBoolean(PREF_LOGGED, true).apply();
        Intent intent = new Intent(this, ContainerActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestPermissionAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST);

        } else {
            onPermissionGranted();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.confirm:
                requestPermissionAndOpen();
                /*Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);*/
                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                requestPermissionAndOpen();
            } else {
                Toast.makeText(this, "Error with authentication", Toast.LENGTH_SHORT).show();
            }
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Error with Google API connection", Toast.LENGTH_SHORT).show();
    }
}

