package com.getbooks.android.ui.widget.left_menu_items;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.getbooks.android.R;
import com.getbooks.android.api.QueriesTexts;
import com.getbooks.android.model.RequestModel;
import com.getbooks.android.model.Text;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.LibraryActivity;
import com.getbooks.android.ui.widget.ArialNormalTextView;
import com.getbooks.android.util.LogUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by avi on 18.08.17.
 */

public class FragmentServicePrivacy extends BaseFragment{
    @Override
    public int getLayout() {
        return R.layout.left_menu_service_privacy;
    }

    @Override
    public LibraryActivity getAct() {
        return (LibraryActivity) getActivity();
    }

    public static FragmentServicePrivacy getInstance() {
        FragmentServicePrivacy f = new FragmentServicePrivacy();
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

            }
        });
        return v;
    }
}
