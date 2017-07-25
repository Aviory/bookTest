package com.getbooks.android.ui.fragments;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.BaseFragment;

/**
 * Created by marina on 25.07.17.
 */

public class CatalogFragment extends BaseFragment {
    @Override
    public int getLayout() {
        return R.layout.fragment_authorization_layout;
    }

    @Override
    public <T extends BaseActivity> T getAct() {
        return null;
    }
}
