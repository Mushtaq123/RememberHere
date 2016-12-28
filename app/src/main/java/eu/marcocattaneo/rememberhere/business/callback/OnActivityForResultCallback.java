package eu.marcocattaneo.rememberhere.business.callback;

import android.content.Intent;

public interface OnActivityForResultCallback {

    /**
     * Activity for result callback
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityForResult(int requestCode, int resultCode, Intent data);

}
