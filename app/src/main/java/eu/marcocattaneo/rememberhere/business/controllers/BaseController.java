package eu.marcocattaneo.rememberhere.business.controllers;

import android.content.Context;

import eu.marcocattaneo.rememberhere.business.callback.OnQueryResult;
import eu.marcocattaneo.rememberhere.business.dao.ProximityDao;
import eu.marcocattaneo.rememberhere.business.dao.ProximityDaoImpl;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class BaseController implements RealmChangeListener<RealmResults<ProximityPOI>> {

    private Context mContext;

    private ProximityDaoImpl proximityDao;

    public BaseController(Context context) {
        this.mContext = context;
    }

    // Query listener

    private RealmResults<ProximityPOI> list;

    private OnQueryResult<ProximityPOI> mOnQueryResult;

    /**
     * Find poi sorted by done variable
     *
     * @param onQueryResult
     */
    public void findProximityPOI(final OnQueryResult<ProximityPOI> onQueryResult) {
        if (mOnQueryResult == null)
            mOnQueryResult = onQueryResult;

        if (list == null) {
            list = getDao().findPoiSorted("done", Sort.ASCENDING);

            list.addChangeListener(this);
        }

        // First time
        onQueryResult.onData(list);
    }

    public Context getContext() {
        return mContext;
    }

    public void onStop() {
        if (list != null)
            list.removeChangeListener(this);
        list = null;
        mOnQueryResult = null;
        proximityDao.close();
    }

    public void onStart() {
        proximityDao = new ProximityDao(mContext);
    }

    protected ProximityDaoImpl getDao() {
        return proximityDao;
    }

    /**
     * Delete the param poi
     *
     * @param proximityPOI
     */
    public void delete(ProximityPOI proximityPOI) {
        getDao().delete(proximityPOI);
    }

    /**
     * Set as done the param poi
     *
     * @param proximityPOI
     */
    public void setDone(ProximityPOI proximityPOI) {
        getDao().setDone(proximityPOI);
    }

    /**
     * Set ax espire the param poi
     *
     * @param proximityPOI
     */
    public void setExpired(ProximityPOI proximityPOI) {
        getDao().setExpired(proximityPOI);
    }

    @Override
    public void onChange(RealmResults<ProximityPOI> element) {
        mOnQueryResult.onData(element);
    }
}
