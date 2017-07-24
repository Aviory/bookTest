package com.getbooks.android.ui.fragments;

import com.getbooks.android.R;
import com.getbooks.android.events.Events;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.AuthorizationActivity;
import com.getbooks.android.ui.dialog.TutorialsDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.OnClick;

/**
 * Created by marina on 21.07.17.
 */

public class TutorialTextFloatFragment extends BaseFragment {

    private TutorialsDialog mTutorialsDialog;

    public static TutorialTextFloatFragment getInstance() {
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
