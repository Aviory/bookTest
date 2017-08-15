package com.getbooks.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.getbooks.android.R;
import com.getbooks.android.events.Events;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.fragments.TutorialMainFragment;
import com.getbooks.android.ui.fragments.TutorialTextFloatFragment;
import com.getbooks.android.ui.fragments.TutorialTextFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * Created by marina on 26.07.17.
 */

public class TutorialsActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    protected ViewPager mViewPagerTutorials;

    private ScreenSliderPagerAdapter mSliderPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isStoragePermissionGranted();

        startViewPagerTutorials();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_base_tutorial;
    }

    @Subscribe
    public void onMessageEvent(Events.RemoveTutorialsScreens removeTutorialsScreens) {
        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);
        finish();
    }

    private void startViewPagerTutorials() {
        mSliderPagerAdapter = new ScreenSliderPagerAdapter(getSupportFragmentManager());
        mViewPagerTutorials.setAdapter(mSliderPagerAdapter);
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
