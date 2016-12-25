package eu.marcocattaneo.rememberhere.business.callback;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public interface OnClientAPIListener {

    void onConnect(GoogleApiClient client);

    void onConnectionFail(ConnectionResult result);

}
