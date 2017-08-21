package com.getbooks.android.ui.widget.left_menu_items;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.getbooks.android.R;
import com.getbooks.android.ui.widget.left_menu_items.Tutorial.PagerView;
import com.getbooks.android.util.LogUtil;

/**
 * Created by avi on 21.08.17.
 */

public class FragmentTutorial extends DialogFragment {
    protected ViewPager mViewPagerTutorials;
    private static FragmentTutorial f;

    public static FragmentTutorial newInstance(){
        if(f==null)
        f = new FragmentTutorial();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_base_tutorial, container, false);
        mViewPagerTutorials = v.findViewById(R.id.view_pager);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startViewPagerTutorials();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void startViewPagerTutorials() {
        mViewPagerTutorials.setAdapter(new PagerView(getActivity()));
    }
}
