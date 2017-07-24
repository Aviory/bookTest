package com.getbooks.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbooks.android.R;
import com.getbooks.android.events.Events;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.AuthorizationActivity;
import com.getbooks.android.ui.widget.SelectorTab;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * Created by marina on 14.07.17.
 */

public class LibraryFragment extends BaseFragment implements SelectorTab.OnItemSelectedListener {

    @BindView(R.id.view_pager)
    protected ViewPager mViewPagerTutorials;
    @BindView(R.id.rootMainView)
    protected RelativeLayout mRootLayoutView;
    @BindView(R.id.menu_main)
    protected SelectorTab mMenuMainSelectorTab;

    private ScreenSliderPagerAdapter mSliderPagerAdapter;
    private boolean isTutorialsScreensViewed = false;

    public static final String IS_TUTORIALS_VIEWED = "is_tutorials_viewed";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        Prefs.completeTutorialsShow(getContext());
        mMenuMainSelectorTab.setOnItemSelectedListener(this);
        if (savedInstanceState != null) {
            isTutorialsScreensViewed = savedInstanceState.getBoolean(IS_TUTORIALS_VIEWED);
            Log.d("isTutorialsScreensView", String.valueOf(savedInstanceState.getBoolean(IS_TUTORIALS_VIEWED)));
            Log.d("getCountTutorialsShow", String.valueOf(Prefs.getCountTutorialsShow(getContext())));
            if (Prefs.getCountTutorialsShow(getContext()) < Prefs.MAX_COUNT_VIEWS_TUTORIALS && !isTutorialsScreensViewed) {
                Log.d("isTutorials-------", String.valueOf(isTutorialsScreensViewed));
                startViewPagerTutorials();
            }
        } else {
            Log.d("getCountTutorialsShow", String.valueOf(Prefs.getCountTutorialsShow(getContext())));
            if (Prefs.getCountTutorialsShow(getContext()) < Prefs.MAX_COUNT_VIEWS_TUTORIALS && !isTutorialsScreensViewed) {
                Log.d("isTutorialsScreensView", String.valueOf(isTutorialsScreensViewed));
                startViewPagerTutorials();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            int s = savedInstanceState.getInt("showTutorials");
//            mViewPagerTutorials.setCurrentItem(2);
//            mSliderPagerAdapter.notifyDataSetChanged();
            Log.d("restoreValue", String.valueOf(s));
        }
    }

    @Override
    protected void saveValue(Bundle outState) {
        super.saveValue(outState);
        Log.d("saveValue", String.valueOf(mViewPagerTutorials.getCurrentItem()));
        outState.putInt("showTutorials", mViewPagerTutorials.getCurrentItem());
        outState.putBoolean(IS_TUTORIALS_VIEWED, true);
    }

//    @Override
//    protected void restoreValue(Bundle outState) {
//        super.restoreValue(outState);
////        Log.d("restoreValue", "restoreValue");
//        int s = outState.getInt("showTutorials");
//        Log.d("restoreValue", String.valueOf(s));
//    }

    @Override
    public int getLayout() {
        return R.layout.fragment_library;
    }

    @Override
    public AuthorizationActivity getAct() {
        return (AuthorizationActivity) getActivity();
    }

    private void startViewPagerTutorials() {
        mSliderPagerAdapter = new ScreenSliderPagerAdapter(getChildFragmentManager());
        mViewPagerTutorials.setAdapter(mSliderPagerAdapter);
    }

    @Override
    public void onItemSelected(View view, int id) {
        switch (id){
            case 0:
                Toast.makeText(getContext(), "LibraryTab", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(getContext(), "CatalogTab", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Subscribe
    public void onMessageEvent(Events.RemoveTutorialsScreens removeTutorialsScreens) {
        mRootLayoutView.removeView(mViewPagerTutorials);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private class ScreenSliderPagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSliderPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TutorialMainFragment.getInstance();
                case 1:
                    return TutorialTextFloatFragment.getInstance();
                case 2:
                    return TutorialTextFragment.getInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
