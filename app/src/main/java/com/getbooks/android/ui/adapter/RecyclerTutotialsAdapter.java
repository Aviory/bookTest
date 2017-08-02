package com.getbooks.android.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.getbooks.android.R;
import com.getbooks.android.util.UiUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marina on 26.07.17.
 */

public class RecyclerTutotialsAdapter extends RecyclerView.Adapter<RecyclerTutotialsAdapter.ViewHolder> {

    private Context mContext;

    public RecyclerTutotialsAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shelves, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!UiUtil.isTablet(mContext)) {
            fillBookCover(R.array.covers_books, R.array.states_books, holder, position);
        } else {
            fillBookCover(R.array.covers_books_tablet, R.array.states_books_tablet, holder, position);
        }
    }

    private void fillBookCover(int arraysCovers, int arraysStates, ViewHolder holder, int position) {
        TypedArray covers = mContext.getResources().obtainTypedArray(arraysCovers);
        TypedArray states = mContext.getResources().obtainTypedArray(arraysStates);
        holder.mImageCover.setImageResource(covers.getResourceId(position, -1));
        holder.mImageBookState.setImageResource(states.getResourceId(position, -1));
        covers.recycle();
        states.recycle();
    }

    @Override
    public int getItemCount() {
        if (!UiUtil.isTablet(mContext))
            return 5;
        else
            return 15;

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_new_downloaded_book)
        protected ImageView mImageNewBook;
        @BindView(R.id.img_book_cover)
        protected ImageView mImageCover;
        @BindView(R.id.img_book_state)
        protected ImageView mImageBookState;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
