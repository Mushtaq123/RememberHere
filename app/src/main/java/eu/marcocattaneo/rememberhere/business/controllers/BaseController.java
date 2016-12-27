package eu.marcocattaneo.rememberhere.business.controllers;

import android.content.Context;

import eu.marcocattaneo.rememberhere.business.callback.OnQueryResult;
import eu.marcocattaneo.rememberhere.business.dao.ProximityDao;
import eu.marcocattaneo.rememberhere.business.dao.ProximityDaoImpl;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class BaseController {

    private Context mContext;

    private ProximityDaoImpl proximityDao;

    public BaseController(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void onStop() {
        proximityDao.close();
    }

    public void onStart() {
        proximityDao = new ProximityDao(mContext);
    }

    protected ProximityDaoImpl getDao() {
        return proximityDao;
    }

    /**
     * Find poi sorted by done variable
     *
     * @param onQueryResult
     */
    public void findProximityPOI(final OnQueryResult<ProximityPOI> onQueryResult) {
        RealmResults<ProximityPOI> list = getDao().findPoiSorted("done", Sort.ASCENDING);
        list.addChangeListener(new RealmChangeListener<RealmResults<ProximityPOI>>() {
            @Override
            public void onChange(RealmResults<ProximityPOI> element) {
                onQueryResult.onData(element);
            }
        });

        // First time
        onQueryResult.onData(list);
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

}
