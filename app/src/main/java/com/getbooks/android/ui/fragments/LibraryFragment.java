package com.getbooks.android.ui.fragments;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.AuthorizationActivity;

/**
 * Created by marina on 14.07.17.
 */

public class LibraryFragment extends BaseFragment {



    @Override
    public int getLayout() {
        return R.layout.library_fragment;
    }

    @Override
    public AuthorizationActivity getAct() {
        return (AuthorizationActivity) getActivity();
    }
}
