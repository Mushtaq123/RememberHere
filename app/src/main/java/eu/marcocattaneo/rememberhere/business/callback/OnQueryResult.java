package eu.marcocattaneo.rememberhere.business.callback;

import java.util.List;

public interface OnQueryResult<T> {

    void onData(List<T> data);

}
