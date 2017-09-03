package com.getbooks.android.ui.fragments.left_menu_items;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.getbooks.android.R;
import com.getbooks.android.ui.adapter.RecyclerBookContentAdapter;
import com.getbooks.android.ui.adapter.ServicePrivacyAdapter;

import java.util.Arrays;

/**
 * Created by avi on 18.08.17.
 */

public class FragmentServicePrivacy extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private String textBody;
    private RadioButton mBtnLeft;
    private RadioButton mBtnRigth;

    private static FragmentServicePrivacy mFragmentServicePrivacy;
    private LinearLayoutManager mLinearLayoutManager;
    private ServicePrivacyAdapter mBookContentAdapter;

    public void setText(String textBody) {
        this.textBody = textBody;
    }

    public static FragmentServicePrivacy getInstance() {

        if (mFragmentServicePrivacy == null) {
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
        mRecyclerView = v.findViewById(R.id.txt_body_service_privacy);
        mInitRecycler(textBody);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().hide(FragmentServicePrivacy.getInstance()).commit();
            }
        });
        return v;
    }

    private void mInitRecycler(String textBody) {
        String[] textArray = {textBody};
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mBookContentAdapter = new ServicePrivacyAdapter(textArray);
        mRecyclerView.setAdapter(mBookContentAdapter);
//        mRecyclerView.setAdapter();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_service_left:
                mInitRecycler("");
                break;
            case R.id.btn_service_right:
                mInitRecycler(textBody);
                break;
        }
    }
}
