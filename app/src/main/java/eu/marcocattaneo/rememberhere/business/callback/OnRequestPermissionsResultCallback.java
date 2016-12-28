package eu.marcocattaneo.rememberhere.business.callback;

import android.support.annotation.NonNull;

public interface OnRequestPermissionsResultCallback {

    /**
     * Permission callback
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

}
