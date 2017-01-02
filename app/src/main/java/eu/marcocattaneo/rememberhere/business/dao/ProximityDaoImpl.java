package eu.marcocattaneo.rememberhere.business.dao;

import android.content.Context;

import java.util.Date;

import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ProximityDaoImpl implements ProximityDao {

    private Realm mRealm;

    public ProximityDaoImpl(Context context) {
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public ProximityPOI create(String guid, String note, int radius, double latitude, double longitude) {
        mRealm.beginTransaction();

        ProximityPOI proximityPOI = mRealm.createObject(ProximityPOI.class, guid);
        proximityPOI.setLongitude(longitude);
        proximityPOI.setLatitude(latitude);
        proximityPOI.setNote(note);
        proximityPOI.setCreateDate(new Date());
        proximityPOI.setUpdateDate(new Date());
        proximityPOI.setExpired(false);
        proximityPOI.setDone(false);

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
        proximityPOI.setUpdateDate(new Date());

        mRealm.commitTransaction();
    }

    @Override
    public void setExpired(ProximityPOI proximityPOI) {
        mRealm.beginTransaction();

        proximityPOI.setExpired(true);
        proximityPOI.setUpdateDate(new Date());

        mRealm.commitTransaction();
    }

    @Override
    public RealmResults<ProximityPOI> findPoiSorted(String field, Sort sort) {
        if (field != null)
            return mRealm.where(ProximityPOI.class).findAllSorted(field, sort);
        else
            return mRealm.where(ProximityPOI.class).findAll();
    }

    @Override
    public RealmResults<ProximityPOI> findPoiNotificatioNupdate() {
        return mRealm.where(ProximityPOI.class).equalTo("expired", true).findAllSorted("updateDate", Sort.DESCENDING);
    }

    @Override
    public ProximityPOI findByGuid(String guid) {
        return mRealm.where(ProximityPOI.class).equalTo("guid", guid).findFirst();
    }

    public void close() {
        mRealm.close();
    }

}
