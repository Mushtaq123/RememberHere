package eu.marcocattaneo.rememberhere.business.callback;

import android.view.View;

import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;

public interface OnListListener {

    void onClickDelete(View itemView, ProximityPOI poi);

    void onClickMap(View itemView, ProximityPOI poi);

}
