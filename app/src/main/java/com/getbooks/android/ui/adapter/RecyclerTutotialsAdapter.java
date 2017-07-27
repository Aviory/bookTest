package com.getbooks.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.getbooks.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marina on 26.07.17.
 */

public class RecyclerTutotialsAdapter extends RecyclerView.Adapter<RecyclerTutotialsAdapter.ViewHolder> {

    private int[] mCoversBooks = new int[]{R.drawable.book_1, R.drawable.book_2, R.drawable.book_3,
            R.drawable.book_4, R.drawable.book_5};

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shelves, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mImageCover.setImageResource(mCoversBooks[position]);
    }

    @Override
    public int getItemCount() {
        return mCoversBooks.length;
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
