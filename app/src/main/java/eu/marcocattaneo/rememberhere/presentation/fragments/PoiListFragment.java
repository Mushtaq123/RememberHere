package eu.marcocattaneo.rememberhere.presentation.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import eu.marcocattaneo.rememberhere.ContainerActivity;
import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.callback.OnListListener;
import eu.marcocattaneo.rememberhere.business.callback.OnQueryResult;
import eu.marcocattaneo.rememberhere.business.controllers.BaseController;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import eu.marcocattaneo.rememberhere.presentation.adapter.PoiAdapter;
import eu.marcocattaneo.rememberhere.presentation.base.BaseActivity;
import eu.marcocattaneo.rememberhere.presentation.base.BaseFragment;

public class PoiListFragment extends BaseFragment implements OnQueryResult<ProximityPOI>, OnListListener, View.OnClickListener {

    public static PoiListFragment newInstance() {

        Bundle args = new Bundle();

        PoiListFragment fragment = new PoiListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BaseActivity mActivity;

    private RecyclerView poiList;
    private LinearLayoutManager mLayoutManager;

    private FloatingActionButton mAddButtoon;

    private BaseController controller;

    private PoiAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getBaseActivity();

        controller = new BaseController(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poilist, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAddButtoon = (FloatingActionButton) view.findViewById(R.id.addPoi);
        mAddButtoon.setOnClickListener(this);

        poiList = (RecyclerView) view.findViewById(R.id.poiList);
        poiList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        poiList.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();

        controller.onStart();
        controller.findProximityPOI(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        controller.onStop();
    }

    @Override
    public void onData(List<ProximityPOI> data) {
        if (mAdapter == null) {
            mAdapter = new PoiAdapter(mActivity, data, this);
            poiList.setAdapter(mAdapter);
        } else {
            mAdapter.swapItems(data);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mActivity, ContainerActivity.class);
        intent.putExtra(ContainerActivity.EXTRA_OPERATION, ContainerActivity.OPERATION.OPEN_ADD_SECTION);
        startActivity(intent);
    }

    @Override
    public void onClickDelete(View itemView, final ProximityPOI poi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.delete_confirm);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                controller.delete(poi);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Noting
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
