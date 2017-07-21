package com.getbooks.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.AuthorizationActivity;

/**
 * Created by marina on 19.07.17.
 */

public class TutorialMainFragment extends BaseFragment {

    private static final String RES_IMG = "com.getbooks.android.fragment.img";
//
//    @BindView(R.id.img_tutorial)
//    protected ImageView mImageTutorial;


    public static TutorialMainFragment getInstance() {
//        Bundle bundle = new Bundle();
//        bundle.putInt(RES_IMG, imgRes);
        TutorialMainFragment tutorialsFragment = new TutorialMainFragment();
//        tutorialsFragment.setArguments(bundle);
        return tutorialsFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        if (getArguments() != null) {
//            int res = getArguments().getInt(RES_IMG, -1);
//            if (res > 0)
//                mImageTutorial.setImageResource(res);
//        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_tutorial_main;
    }

    @Override
    public AuthorizationActivity getAct() {
        return (AuthorizationActivity) getActivity();
    }
}
