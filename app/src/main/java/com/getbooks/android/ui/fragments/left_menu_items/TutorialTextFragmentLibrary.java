package com.getbooks.android.ui.fragments.left_menu_items;

import com.getbooks.android.R;
import com.getbooks.android.events.Events;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.LibraryActivity;
import com.getbooks.android.ui.activities.TutorialsActivity;
import com.getbooks.android.ui.dialog.TutorialsDialog;
import com.getbooks.android.ui.fragments.TutorialTextFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.OnClick;

/**
 * Created by avi on 30.08.17.
 */

public class TutorialTextFragmentLibrary  extends BaseFragment implements TutorialsDialog.OnItemTutorialsClick {

    private TutorialsDialog mTutorialsDialog;

    public static TutorialTextFragmentLibrary getInstance() {
        TutorialTextFragmentLibrary tutorialTextFragment = new TutorialTextFragmentLibrary();
        return tutorialTextFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_tutorial_text;
    }

    @Override
    public LibraryActivity getAct() {
        return (LibraryActivity) getActivity();
    }

    @OnClick(R.id.img_close)
    protected void closeTutorials() {
        getActivity().getSupportFragmentManager().beginTransaction().hide( TutorialFragment.getInstance()).commit();
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
