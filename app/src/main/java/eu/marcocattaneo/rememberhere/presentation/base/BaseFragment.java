package eu.marcocattaneo.rememberhere.presentation.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import eu.marcocattaneo.rememberhere.R;

public abstract class BaseFragment extends Fragment {

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    private Toolbar toolbar;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
    }

    protected Toolbar initToolbar() {
        toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        getBaseActivity().setSupportActionBar(toolbar);

        return toolbar;
    }

    protected void setBackEnable(boolean enable) {
        if (getBaseActivity().getSupportActionBar() == null)
            return;
        getBaseActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        getBaseActivity().getSupportActionBar().setHomeButtonEnabled(enable);
        getBaseActivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(enable);
    }

}
