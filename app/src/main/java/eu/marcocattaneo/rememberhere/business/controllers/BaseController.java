package eu.marcocattaneo.rememberhere.business.controllers;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import eu.marcocattaneo.rememberhere.business.callback.OnQueryResult;
import eu.marcocattaneo.rememberhere.business.dao.ProximityDaoImpl;
import eu.marcocattaneo.rememberhere.business.dao.ProximityDao;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class BaseController {

    private Context mContext;

    private ProximityDao proximityDao;

    public BaseController(Context context) {
        this.mContext = context;
    }

    // Query listener

    private RealmResults<ProximityPOI> list;

    private RealmResults<ProximityPOI> notifyList;

    private RealmChangeListener<RealmResults<ProximityPOI>> mOnQueryResult;

    private RealmChangeListener<RealmResults<ProximityPOI>> mOnQueryNotifyResult;

    /**
     * Find poi sorted by done variable
     *
     * @param onQueryResult
     */
    public void findProximityPOI(final RealmChangeListener<RealmResults<ProximityPOI>> onQueryResult) {
        mOnQueryResult = onQueryResult;

        if (list == null) {
            list = getDao().findPoiSorted("done", Sort.ASCENDING);

            list.addChangeListener(mOnQueryResult);
        }

        // First time
        mOnQueryResult.onChange(list);
    }

    /**
     * Find poi sorted by done variable
     *
     * @param onQueryResult
     */
    public void findProximityPOIUpdates(final RealmChangeListener<RealmResults<ProximityPOI>> onQueryResult) {
        mOnQueryNotifyResult = onQueryResult;

        if (notifyList == null) {
            notifyList = getDao().findPoiNotificatioNupdate();

            notifyList.addChangeListener(mOnQueryNotifyResult);
        }

        // First time
        mOnQueryNotifyResult.onChange(notifyList);
    }

    /**
     * Find Proximity POI by GUID
     * @param guid
     * @return
     */
    public @Nullable ProximityPOI findProximityPOIByGuid(String guid) {
        return getDao().findByGuid(guid);
    }

    public Context getContext() {
        return mContext;
    }

    public void onStop() {
        if (list != null)
            list.removeChangeListener(mOnQueryResult);
        list = null;

        if (notifyList != null)
            notifyList.removeChangeListener(mOnQueryNotifyResult);
        notifyList = null;

        mOnQueryResult = null;
        mOnQueryNotifyResult = null;
        proximityDao.close();
    }

    public void onStartRealm() {
        proximityDao = new ProximityDaoImpl(mContext);
    }

    protected ProximityDao getDao() {
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

}
