package com.getbooks.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.events.Events;
import com.getbooks.android.model.BookModel;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.ReaderActivity;
import com.getbooks.android.ui.adapter.RecyclerBookContent;
import com.getbooks.android.ui.widget.SelectorOfBookContents;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marinaracu on 16.08.17.
 */

public class BookContentFragment extends BaseFragment implements SelectorOfBookContents.OnItemSelectedListener {

    @BindView(R.id.book_contents_selector)
    protected SelectorOfBookContents mSelectorOfBookContents;
    @BindView(R.id.content_chosen)
    protected RecyclerView mRecyclerBookContent;

    LinearLayoutManager mLinearLayoutManager;
    RecyclerBookContent mBookContentAdapter;
    DividerItemDecoration dividerItemDecoration;

    private BookModel bookModel;

    private String[] mChapterList;
    private BookDataBaseLoader mBookDataBaseLoader;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSelectorOfBookContents.setOnItemSelectedListener(this);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            String boomName = bundle.getString(Const.BOOK_NAME);
            mBookDataBaseLoader = BookDataBaseLoader.getInstanceDb(getAct());
            bookModel = mBookDataBaseLoader.getCurrentBookDetailDb(Prefs.getUserSession(getAct(), Const.USER_SESSION_ID),
                    boomName);
            mChapterList = bookModel.getChapterList().split(",");
            initShelvesRecycler(mChapterList);
        }
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
                Toast.makeText(getAct(), "BookModel content", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(getAct(), "BookMurks list", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getAct(), "Highlights list", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void initShelvesRecycler(String[] chapterList) {
        mLinearLayoutManager = new LinearLayoutManager(getAct());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerBookContent.setLayoutManager(mLinearLayoutManager);

        if (mBookContentAdapter == null)
            mBookContentAdapter = new RecyclerBookContent(chapterList);
        mRecyclerBookContent.setAdapter(mBookContentAdapter);

        if (dividerItemDecoration == null) {
            dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getAct(),R.drawable.separator_book_content));
            mRecyclerBookContent.addItemDecoration(dividerItemDecoration);
        }
    }


    @OnClick(R.id.img_close)
    protected void closeContentBokList() {
        EventBus.getDefault().post(new Events.CloseContentMenuSetting(false));
        getAct().getSupportFragmentManager().beginTransaction().remove(BookContentFragment.this).commit();
    }
}
