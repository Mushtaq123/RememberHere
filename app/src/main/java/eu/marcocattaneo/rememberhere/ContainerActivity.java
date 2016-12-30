package eu.marcocattaneo.rememberhere;

import android.content.Intent;
import android.os.Bundle;

import eu.marcocattaneo.rememberhere.presentation.base.BaseActivity;
import eu.marcocattaneo.rememberhere.presentation.fragments.MapFragment;
import eu.marcocattaneo.rememberhere.presentation.fragments.PoiListFragment;

public class ContainerActivity extends BaseActivity {

    public static final String EXTRA_OPERATION = "container.extra.op";
    public static final String EXTRA_GUID = "container.extra.guid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        Intent intent = getIntent();

        int operation = intent.hasExtra(EXTRA_OPERATION) ? getIntent().getIntExtra(EXTRA_OPERATION, 1) : 1;

        switch (operation) {

            case OPERATION.OPEN_LIST_SECTION:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, PoiListFragment.newInstance()).commit();
                break;

            case OPERATION.OPEN_ADD_SECTION:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, MapFragment.newInstance(intent.hasExtra(EXTRA_GUID) ? intent.getStringExtra(EXTRA_GUID) : null)).commit();
                break;

        }

    }

    public static class OPERATION {

        public static final int OPEN_LIST_SECTION = 1;

        public static final int OPEN_ADD_SECTION = 2;

    }

}
