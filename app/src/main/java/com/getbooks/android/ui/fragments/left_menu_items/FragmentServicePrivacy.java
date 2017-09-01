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

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.model.Text;
import com.getbooks.android.ui.adapter.RecyclerBookContent;

import java.util.List;

/**
 * Created by avi on 18.08.17.
 */

public class FragmentServicePrivacy extends Fragment implements View.OnClickListener{

    private RecyclerView mRecyclerView;
    private String mRightBtnText;
    private String mLeftBtnText;
    private RadioButton mBtnLeft;
    private RadioButton mBtnRigth;

    private static FragmentServicePrivacy mFragmentServicePrivacy;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerBookContent mBookContentAdapter;

    public void setText(List<Text> txt_list) {
        if(txt_list!=null){
            for (Text t: txt_list) {
                if(t.getPopupID().equals(Const.SERVISE_PRIVASY_RIGHT_BTN_TEXT_ID)){
                    mRightBtnText = t.getPopupText();
                }
                if(t.getPopupID().equals(Const.SERVISE_PRIVASY_LEFT_BTN_TEXT_ID)){
                    mLeftBtnText = t.getPopupText();
                }
            }
        }
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
        View v = inflater.inflate(R.layout.fragment_left_menu_service_privacy, container, false);
        ImageView close = v.findViewById(R.id.service_privacy_close);
        mBtnLeft = v.findViewById(R.id.btn_service_left);
        mBtnLeft.setOnClickListener(this);
        mBtnRigth = v.findViewById(R.id.btn_service_right);
        mBtnRigth.setOnClickListener(this);
        mRecyclerView = v.findViewById(R.id.txt_body_service_privacy);
        mInitRecycler(mRightBtnText);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().hide( FragmentServicePrivacy.getInstance()).commit();
            }
        });
        return v;
    }

    private void mInitRecycler(String textBody) {
        String[] textArray = {textBody};
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mBookContentAdapter = new RecyclerBookContent(textArray);
        mRecyclerView.setAdapter(mBookContentAdapter);
//        mRecyclerView.setAdapter();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_service_left:
                mInitRecycler(mLeftBtnText);
                break;
            case R.id.btn_service_right:
                mInitRecycler(mRightBtnText);
                break;
        }
    }
}
