package com.getbooks.android.ui.fragments;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.AuthorizationActivity;

/**
 * Created by marina on 21.07.17.
 */

public class TutorialTextFloatFragment extends BaseFragment {

    public static TutorialTextFloatFragment getInstance(){
        TutorialTextFloatFragment tutorialTextFloatFragment = new TutorialTextFloatFragment();
        return tutorialTextFloatFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_tutorial_text_float;
    }

    @Override
    public AuthorizationActivity getAct() {
        return (AuthorizationActivity) getActivity();
    }
}
