package com.getbooks.android.ui.widget;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.getbooks.android.R;
import com.getbooks.android.api.QueriesTexts;
import com.getbooks.android.model.Text;
import com.getbooks.android.util.LogUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Avi on 14.08.2017.
 */

public class AlertDialogAboutUs extends DialogFragment {
    public AlertDialogAboutUs(){}

    public static AlertDialogAboutUs newInstance(){
        AlertDialogAboutUs f = new AlertDialogAboutUs();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.left_menu_about_us, container, false);
        ImageView close = (ImageView) v.findViewById(R.id.about_us_close);
        ArialNormalTextView text = (ArialNormalTextView) v.findViewById(R.id.txt_about);
        new QueriesTexts().getApi().getAllTexts().enqueue(new Callback<List<Text>>() {
            @Override
            public void onResponse(Call<List<Text>> call, Response<List<Text>> response) {
                List<Text> list = response.body();
                for (Text t: list) {
                    if(t.getPopupID()=="4972");{
                        text.setText(t.getPopupText());
                        return;
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Text>> call, Throwable t) {
                LogUtil.log(this, "onFailure: <List<Text>> ");
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return v;
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
}
