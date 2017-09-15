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
import com.getbooks.android.ui.adapter.ServicePrivacyAdapter;

import java.util.List;

/**
 * Created by marinaracu on 15.09.17.
 */

public class QuestionAndHistoryFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private String mRightBtnText;
    private String mLeftBtnText;
    private RadioButton mBtnLeft;
    private RadioButton mBtnRigth;

    private static QuestionAndHistoryFragment mFragmentQuestionAndHelp;
    private LinearLayoutManager mLinearLayoutManager;
    private ServicePrivacyAdapter mBookContentAdapter;

    public void setText(List<Text> txt_list) {
        if (txt_list != null) {
            for (Text t : txt_list) {
                if (t.getPopupID().equals(Const.SERVISE_PRIVASY_RIGHT_BTN_TEXT_ID)) {
                    mRightBtnText = t.getPopupText();
                }
                if (t.getPopupID().equals(Const.SERVISE_PRIVASY_LEFT_BTN_TEXT_ID)) {
                    mLeftBtnText = t.getPopupText();
                }
            }
        }
    }

    public static QuestionAndHistoryFragment getInstance() {

        if (mFragmentQuestionAndHelp == null) {
            mFragmentQuestionAndHelp = new QuestionAndHistoryFragment();
        }
        return mFragmentQuestionAndHelp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help_and_question, container, false);
        ImageView close = v.findViewById(R.id.img_help_close);
        mBtnLeft = v.findViewById(R.id.btn_questions);
        mBtnLeft.setOnClickListener(this);
        mBtnRigth = v.findViewById(R.id.btn_help);
        mBtnRigth.setOnClickListener(this);
        mRecyclerView = v.findViewById(R.id.recycler_questions);
        mInitRecycler(mRightBtnText);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().hide(QuestionAndHistoryFragment.getInstance()).commit();
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
            case R.id.btn_questions:
                mInitRecycler(mLeftBtnText);
                break;
            case R.id.btn_help:
                mInitRecycler(mRightBtnText);
                break;
        }
    }
}
