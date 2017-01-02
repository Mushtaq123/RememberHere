package eu.marcocattaneo.rememberhere.business.dao;

import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import io.realm.RealmResults;
import io.realm.Sort;

public interface ProximityDao {

    ProximityPOI create(String guid, String note, int radius, double longitude, double latitude);

    void delete(ProximityPOI proximityPOI);

    void setDone(ProximityPOI proximityPOI);

    void setExpired(ProximityPOI proximityPOI);

    RealmResults<ProximityPOI> findPoiSorted(String field, Sort sort);

    RealmResults<ProximityPOI> findPoiNotificatioNupdate();

    ProximityPOI findByGuid(String guid);

    void close();

}
