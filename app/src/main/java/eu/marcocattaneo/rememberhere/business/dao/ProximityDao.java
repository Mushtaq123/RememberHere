package eu.marcocattaneo.rememberhere.business.dao;

import android.content.Context;

import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ProximityDao implements ProximityDaoImpl {

    private Realm mRealm;

    public ProximityDao(Context context) {
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public ProximityPOI create(String guid, String note, int radius, double longitude, double latitude) {
        mRealm.beginTransaction();

        ProximityPOI proximityPOI = mRealm.createObject(ProximityPOI.class, guid);
        proximityPOI.setLongitude(longitude);
        proximityPOI.setLatitude(latitude);
        proximityPOI.setNote(note);

        mRealm.commitTransaction();
        return proximityPOI;
    }

    @Override
    public void delete(ProximityPOI proximityPOI) {
        mRealm.beginTransaction();

        proximityPOI.deleteFromRealm();

        mRealm.commitTransaction();
    }

    @Override
    public void setDone(ProximityPOI proximityPOI) {
        mRealm.beginTransaction();

        proximityPOI.setDone(true);

        mRealm.commitTransaction();
    }

    @Override
    public void setExpired(ProximityPOI proximityPOI) {
        mRealm.beginTransaction();

        proximityPOI.setExpired(true);

        mRealm.commitTransaction();
    }

    @Override
    public RealmResults<ProximityPOI> findPoiSorted(String field, Sort sort) {
        if (field != null)
            return mRealm.where(ProximityPOI.class).findAllSorted(field, sort);
        else
            return mRealm.where(ProximityPOI.class).findAll();
    }

    public void close() {
        mRealm.close();
    }

}
