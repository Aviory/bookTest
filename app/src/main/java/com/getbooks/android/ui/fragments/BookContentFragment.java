package com.getbooks.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.ReaderActivity;
import com.getbooks.android.ui.widget.SelectorOfBookContents;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marinaracu on 16.08.17.
 */

public class BookContentFragment extends BaseFragment implements SelectorOfBookContents.OnItemSelectedListener {

    @BindView(R.id.book_contents_selector)
    protected SelectorOfBookContents mSelectorOfBookContents;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSelectorOfBookContents.setOnItemSelectedListener(this);

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_book_content;
    }

    @Override
    public ReaderActivity getAct() {
        return (ReaderActivity) getActivity();
    }

    @Override
    public void onItemSelected(View view, int id) {
        Log.d("BookContents", "onItemSelected: " + mSelectorOfBookContents.getText());
        switch (id) {
            case 0:
                Toast.makeText(getAct(), "Book content", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(getAct(), "BookMurks list", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getAct(), "Highlights list", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @OnClick(R.id.img_close)
    protected void closeContentBokList(){
        getAct().getSupportFragmentManager().beginTransaction().remove(BookContentFragment.this).commit();
    }
}
