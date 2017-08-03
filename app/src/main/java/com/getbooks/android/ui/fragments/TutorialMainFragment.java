package com.getbooks.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.getbooks.android.R;
import com.getbooks.android.events.Events;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.TutorialsActivity;
import com.getbooks.android.ui.adapter.RecyclerTutotialsAdapter;
import com.getbooks.android.ui.dialog.TutorialsDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marina on 19.07.17.
 */

public class TutorialMainFragment extends BaseFragment implements TutorialsDialog.OnItemTutorialsClick {

    private TutorialsDialog mTutorialsDialog;
    @BindView(R.id.recyler_tutorials_shelves)
    protected RecyclerView mRecyclerBookShelves;
    private RecyclerTutotialsAdapter mShelvesAdapter;
    private GridLayoutManager mGridLayoutManager;

    public static TutorialMainFragment getInstance() {
        TutorialMainFragment tutorialsFragment = new TutorialMainFragment();
        return tutorialsFragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGridLayoutManager = new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.count_column_book));
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerBookShelves.setLayoutManager(mGridLayoutManager);

        mShelvesAdapter = new RecyclerTutotialsAdapter(getAct());
        mRecyclerBookShelves.setAdapter(mShelvesAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayout.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.polka));
        mRecyclerBookShelves.addItemDecoration(dividerItemDecoration);

        mRecyclerBookShelves.setOnTouchListener((view1, motionEvent) -> true);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_tutorial_main;
    }

    @Override
    public TutorialsActivity getAct() {
        return (TutorialsActivity) getActivity();
    }

    @OnClick(R.id.img_close)
    protected void closeTutorials() {
        mTutorialsDialog = new TutorialsDialog(getContext());
        mTutorialsDialog.setOnItemTutorialsClick(this);
        mTutorialsDialog.show();
    }

    @Override
    public void onYesButtonClick() {
        Prefs.addCountTutorialsView(getContext());
        EventBus.getDefault().post(new Events.RemoveTutorialsScreens());
    }

    @Override
    public void onNoButtonClick() {
        Prefs.completeTutorialsShow(getContext());
        EventBus.getDefault().post(new Events.RemoveTutorialsScreens());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTutorialsDialog != null)
            mTutorialsDialog.dismiss();
    }
}
