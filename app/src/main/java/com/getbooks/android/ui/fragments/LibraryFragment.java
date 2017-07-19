package com.getbooks.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.AuthorizationActivity;

import butterknife.BindView;

/**
 * Created by marina on 14.07.17.
 */

public class LibraryFragment extends BaseFragment {

    @BindView(R.id.view_pager)
    protected ViewPager mViewPagerTutorials;

    private ScreenSliderPagerAdapter mSliderPagerAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startViewPagerTutorials();
    }

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

    private class ScreenSliderPagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSliderPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TutorialsFragment.getInstance(R.drawable.add_bookmark);
                case 1:
                    return TutorialsFragment.getInstance(R.drawable.add_bookmark);
                case 2:
                    return TutorialsFragment.getInstance(R.drawable.add_bookmark);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
