package eu.marcocattaneo.rememberhere.business.controllers;

import android.content.Context;

import io.realm.Realm;

public class BaseController {

    private Context mContext;

    private Realm mRealm;

    public BaseController(Context context) {
        this.mContext = context;
    }

    public Realm getRealm() {
        if (mRealm == null || mRealm.isClosed())
            initReealm();
        return mRealm;
    }

    public Context getContext() {
        return mContext;
    }

    public void onStart() {
        initReealm();
    }

    private void initReealm() {
        mRealm = Realm.getDefaultInstance();
    }

    public void onStop() {
        if (mRealm != null)
            mRealm.close();
    }
}
