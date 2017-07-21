package com.getbooks.android.ui.fragments;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.AuthorizationActivity;

/**
 * Created by marina on 21.07.17.
 */

public class TutorialTextFragment extends BaseFragment {

    public static TutorialTextFragment getInstance(){
        TutorialTextFragment tutorialTextFragment = new TutorialTextFragment();
        return tutorialTextFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_tutorial_text;
    }

    @Override
    public AuthorizationActivity getAct() {
        return (AuthorizationActivity) getActivity();
    }
}
