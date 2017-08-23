package com.getbooks.android.ui.fragments.left_menu_items;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.getbooks.android.R;

/**
 * Created by avi on 18.08.17.
 */

public class FragmentServicePrivacy extends Fragment implements View.OnClickListener{
    private String mText;
    private RadioButton mBtnLeft;
    private RadioButton mBtnRigth;
    private RecyclerView mRecyclerView;

    private static FragmentServicePrivacy mFragmentServicePrivacy;

    public void setText(String txt) {
        this.mText = txt;
    }

    public static FragmentServicePrivacy getInstance() {

        if(mFragmentServicePrivacy==null){
            mFragmentServicePrivacy = new FragmentServicePrivacy();
        }
        return mFragmentServicePrivacy;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.left_menu_service_privacy, container, false);
        ImageView close = v.findViewById(R.id.service_privacy_close);
        mBtnLeft = v.findViewById(R.id.btn_service_left);
        mBtnLeft.setOnClickListener(this);
        mBtnRigth = v.findViewById(R.id.btn_service_right);
        mBtnRigth.setOnClickListener(this);
        mRecyclerView = v.findViewById(R.id.txt_service_privacy);
       // mRecyclerView.setText(mText);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().hide( FragmentServicePrivacy.getInstance()).commit();
            }
        });
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_service_left:

                break;
            case R.id.btn_service_right:

                break;
        }
    }
}
