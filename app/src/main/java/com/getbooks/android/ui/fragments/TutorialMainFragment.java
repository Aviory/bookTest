package com.getbooks.android.ui.fragments;

import android.widget.ImageView;

import com.getbooks.android.R;
import com.getbooks.android.events.Events;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.AuthorizationActivity;
import com.getbooks.android.ui.dialog.TutorialsDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marina on 19.07.17.
 */

public class TutorialMainFragment extends BaseFragment {

    private static final String RES_IMG = "com.getbooks.android.fragment.img";

    @BindView(R.id.img_close)
    protected ImageView mImageCloseTutorial;

    private TutorialsDialog mTutorialsDialog;

    public static TutorialMainFragment getInstance() {
        TutorialMainFragment tutorialsFragment = new TutorialMainFragment();
        return tutorialsFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_tutorial_main;
    }

    @Override
    public AuthorizationActivity getAct() {
        return (AuthorizationActivity) getActivity();
    }

    @OnClick(R.id.img_close)
    protected void closeTutorials() {
            mTutorialsDialog = new TutorialsDialog(getContext());
            mTutorialsDialog.show();
    }


    @Subscribe
    public void onMessageEvent(Events.ShowTutorialsScreens showTutorialsScreens) {
        if (showTutorialsScreens.isShowTutorials()) {
            Prefs.addCountTutorialsView(getContext());
            EventBus.getDefault().post(new Events.RemoveTutorialsScreens());
            getFragmentManager().beginTransaction().remove(this).commit();
        } else if (!showTutorialsScreens.isShowTutorials()) {
            Prefs.completeTutorialsShow(getContext());
            EventBus.getDefault().post(new Events.RemoveTutorialsScreens());
            getFragmentManager().beginTransaction().remove(this).commit();
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mTutorialsDialog != null)
            mTutorialsDialog.dismiss();
    }
}
