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
import com.getbooks.android.model.RequestModel;
import com.getbooks.android.model.Text;
import com.getbooks.android.util.LogUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by avi on 17.08.17.
 */

public class AlertDialogServicePrivacy extends DialogFragment {
    public AlertDialogServicePrivacy(){}
    public static AlertDialogServicePrivacy newInstance(){
        AlertDialogServicePrivacy f = new AlertDialogServicePrivacy();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.left_menu_service_privacy, container, false);
        ImageView close = (ImageView) v.findViewById(R.id.service_privacy_close);
        ArialNormalTextView text = (ArialNormalTextView) v.findViewById(R.id.txt_service_privacy);
        new QueriesTexts().getApi().getAllTexts().enqueue(new Callback<RequestModel>() {
            @Override
            public void onResponse(Call<RequestModel> call, Response<RequestModel> response) {
                RequestModel s = response.body();
                List<Text> list = s.getPopUps();
                for (Text t: list) {
                    if(t.getPopupID()=="4984");{
                        text.setText(t.getPopupText());
                        return;
                    }
                }

            }

            @Override
            public void onFailure(Call<RequestModel> call, Throwable t) {
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

