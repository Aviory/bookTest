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

import com.getbooks.android.R;
import com.getbooks.android.api.Queries;
import com.getbooks.android.events.Events;
import com.getbooks.android.model.Library;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.AuthorizationActivity;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * Created by marina on 14.07.17.
 */

public class LibraryFragment extends BaseFragment implements Queries.CallBack {

    @BindView(R.id.view_pager)
    protected ViewPager mViewPagerTutorials;
    @BindView(R.id.rootMainView)
    protected RelativeLayout mRootLayoutView;

    private ScreenSliderPagerAdapter mSliderPagerAdapter;
    private boolean isTutorialsScreensViewed = false;

    public static final String IS_TUTORIALS_VIEWED = "is_tutorials_viewed";

    private Queries mQueries;
    private Library library;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        Prefs.completeTutorialsShow(getContext());
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

        mQueries = new Queries();
        mQueries.setCallBack(this);
        mQueries.getAllRentedBook(FirebaseInstanceId.getInstance().getToken().replace(":", ""));

//        ApiManager.getAllPurchasedBook(FirebaseInstanceId.getInstance().getToken().replace(":", ""), true);
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

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted(Library library) {
        this.library = library;
    }

    @Override
    public void onFinish() {

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
