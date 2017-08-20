package com.getbooks.android.ui.widget.left_menu_items;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.getbooks.android.R;
import com.getbooks.android.api.QueriesTexts;
import com.getbooks.android.model.RequestModel;
import com.getbooks.android.model.Text;
import com.getbooks.android.ui.widget.ArialNormalTextView;
import com.getbooks.android.util.LogUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by avi on 18.08.17.
 */

public class FragmentServicePrivacy extends Fragment {
    private String txt;

    private static FragmentServicePrivacy f;

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public static FragmentServicePrivacy getInstance() {

        if(f==null){
            f = new FragmentServicePrivacy();
        }
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.left_menu_service_privacy, container, false);
        ImageView close = (ImageView) v.findViewById(R.id.service_privacy_close);
        ArialNormalTextView text = (ArialNormalTextView) v.findViewById(R.id.txt_service_privacy);
        text.setText(txt);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().hide( FragmentServicePrivacy.getInstance()).commit();
            }
        });
        return v;
    }
}
