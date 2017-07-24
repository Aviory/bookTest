package com.getbooks.android.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import com.getbooks.android.R;
import com.getbooks.android.events.Events;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by marina on 24.07.17.
 */

public class TutorialsDialog extends AlertDialog {

    private Button mButtonYes;
    private Button mButtonNo;

    public TutorialsDialog(@NonNull Context context) {
        super(context);
    }

    protected TutorialsDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected TutorialsDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    public void show() {
        super.show();
        setContentView(R.layout.dialog_tutorials);
        mButtonYes = (Button) findViewById(R.id.txt_yes);
        mButtonNo = (Button) findViewById(R.id.txt_no);

        showTutorials();
        hideTutorial();
    }

    protected void showTutorials() {
        mButtonYes.setOnClickListener(v -> {
            EventBus.getDefault().post(new Events.ShowTutorialsScreens(true));
        });
    }

    protected void hideTutorial() {
        mButtonNo.setOnClickListener(view -> {
            EventBus.getDefault().post(new Events.ShowTutorialsScreens(false));
        });
    }
}
