package com.getbooks.android.ui.fragments.left_menu_items;

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
import com.getbooks.android.ui.activities.LibraryActivity;
import com.getbooks.android.ui.activities.TutorialsActivity;
import com.getbooks.android.ui.adapter.RecyclerTutotialsAdapter;
import com.getbooks.android.ui.dialog.TutorialsDialog;
import com.getbooks.android.ui.fragments.TutorialMainFragment;
import com.getbooks.android.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by avi on 30.08.17.
 */

public class TutorialMainFragmentLibrary extends BaseFragment implements TutorialsDialog.OnItemTutorialsClick {

    private TutorialsDialog mTutorialsDialog;
    @BindView(R.id.recyler_tutorials_shelves)
    protected RecyclerView mRecyclerBookShelves;
    private RecyclerTutotialsAdapter mShelvesAdapter;
    private GridLayoutManager mGridLayoutManager;

    public static TutorialMainFragmentLibrary getInstance() {
        TutorialMainFragmentLibrary tutorialsFragment = new TutorialMainFragmentLibrary();
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
    public LibraryActivity getAct() {
        return (LibraryActivity) getActivity();
    }

    @OnClick(R.id.img_close)
    protected void closeTutorials() {
        getActivity().getSupportFragmentManager().beginTransaction().hide( TutorialFragment.getInstance()).commit();
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
