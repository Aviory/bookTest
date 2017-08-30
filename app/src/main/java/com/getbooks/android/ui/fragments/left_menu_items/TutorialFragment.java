package com.getbooks.android.ui.fragments.left_menu_items;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.LibraryActivity;
import com.getbooks.android.ui.fragments.TutorialMainFragment;
import com.getbooks.android.ui.fragments.TutorialTextFloatFragment;
import com.getbooks.android.ui.fragments.TutorialTextFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * Created by avi on 30.08.17.
 */

public class TutorialFragment extends Fragment{

    protected ViewPager mViewPagerTutorials;
    public static TutorialFragment tutorialFragment;

    private ScreenSliderPagerAdapters mSliderPagerAdapter;

    public TutorialFragment(){}

    public static TutorialFragment getInstance(){
        if(tutorialFragment==null)
             tutorialFragment = new TutorialFragment();
        return tutorialFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_base_tutorial, container, false);
        mViewPagerTutorials = v.findViewById(R.id.view_pager);
        startViewPagerTutorials();
        return v;
    }

    private void startViewPagerTutorials() {
            mSliderPagerAdapter = new ScreenSliderPagerAdapters(getActivity().getSupportFragmentManager());
            mViewPagerTutorials.setAdapter(mSliderPagerAdapter);
        }

private class ScreenSliderPagerAdapters extends FragmentStatePagerAdapter {

    public ScreenSliderPagerAdapters(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TutorialMainFragmentLibrary.getInstance();
            case 1:
                return TutorialTextFloatFragmentLibrary.getInstance();
            case 2:
                return TutorialTextFragmentLibrary.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
}

