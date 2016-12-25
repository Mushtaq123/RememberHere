package eu.marcocattaneo.rememberhere.business.callback;

import android.view.View;

import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;

public interface OnPoiListListener {

    void onClick(View itemView, ProximityPOI poi);

    void onLongPress(View itemView, ProximityPOI poi);
}
