package com.getbooks.android.ui.fragments;

import com.getbooks.android.R;
import com.getbooks.android.events.Events;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.TutorialsActivity;
import com.getbooks.android.ui.dialog.TutorialsDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.OnClick;

/**
 * Created by marina on 21.07.17.
 */

public class TutorialTextFragment extends BaseFragment implements TutorialsDialog.OnItemTutorialsClick {

    private TutorialsDialog mTutorialsDialog;

    public static TutorialTextFragment getInstance() {
        TutorialTextFragment tutorialTextFragment = new TutorialTextFragment();
        return tutorialTextFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_tutorial_text;
    }

    @Override
    public TutorialsActivity getAct() {
        return (TutorialsActivity) getActivity();
    }

    @OnClick(R.id.img_close)
    protected void closeTutorials() {
        mTutorialsDialog = new TutorialsDialog(getContext());
        mTutorialsDialog.setOnItemTutorialsClick(this);
        mTutorialsDialog.show();
    }

    @Override
    public void onYesButtonClick() {
        Prefs.addCountTutorialsView(getContext());
        EventBus.getDefault().post(new Events.RemoveTutorialsScreens());
    }

    @Override
    public void onNoButtonClick() {
        Prefs.completeTutorialsShow(getContext());
        EventBus.getDefault().post(new Events.RemoveTutorialsScreens());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTutorialsDialog != null)
            mTutorialsDialog.dismiss();
    }
}
