package eu.marcocattaneo.rememberhere.presentation.base;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;

import eu.marcocattaneo.rememberhere.business.callback.OnActivityForResultCallback;
import eu.marcocattaneo.rememberhere.business.callback.OnRequestPermissionsResultCallback;

public abstract class BaseActivity extends AppCompatActivity {

    private SparseArray<OnActivityForResultCallback> mCallbacks = new SparseArray<OnActivityForResultCallback>();
    private SparseArray<OnRequestPermissionsResultCallback> mPermissionCallbacks = new SparseArray<OnRequestPermissionsResultCallback>();

    /**
     * Add Callback for ActivityForResult
     * @param requestCode
     * @param onActivityForResult
     */
    public void addOnActivityForResult(int requestCode, OnActivityForResultCallback onActivityForResult) {
        mCallbacks.put(requestCode, onActivityForResult);
    }

    /**
     * Remove callback for activityForResult by request code
     * @param requestCode
     */
    public void removeOnActivityForResult(int requestCode) {
        if (mCallbacks.get(requestCode) != null)
            mCallbacks.remove(requestCode);
    }

    /**
     * Add Callback for ActivityForResult
     * @param requestCode
     * @param onActivityForResult
     */
    public void addOnActivityPermissionForResult(int requestCode, OnRequestPermissionsResultCallback onActivityForResult) {
        mPermissionCallbacks.put(requestCode, onActivityForResult);
    }

    /**
     * Remove callback for activityForResult by request code
     * @param requestCode
     */
    public void removeOnPermissionActivityForResult(int requestCode) {
        if (mPermissionCallbacks.get(requestCode) != null)
            mPermissionCallbacks.remove(requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (mCallbacks.get(requestCode) != null) {
            mCallbacks.get(requestCode).onActivityForResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionCallbacks.get(requestCode) != null) {
            mPermissionCallbacks.get(requestCode).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
