package com.getbooks.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.getbooks.android.R;
import com.getbooks.android.model.Library;
import com.getbooks.android.model.PurchasedBook;
import com.getbooks.android.model.RentedBook;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marina on 26.07.17.
 */

public class RecyclerShelvesAdapter extends RecyclerView.Adapter<RecyclerShelvesAdapter.ViewHolder> {

    private Library mLibrary;
    private Context mContext;

    public RecyclerShelvesAdapter(Library mLibrary, Context context) {
        this.mLibrary = mLibrary;
        this.mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shelves, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List<RentedBook> rentedBookList = mLibrary.getRentedRentedBookList();
        for (int i = 0; i < rentedBookList.size(); i++) {
            Picasso.with(mContext).load(rentedBookList.get(i).getRentBookImage()).into(holder.mImageCover);
            holder.mImageBookState.setImageResource(R.drawable.arrow_down_black);
        }
        List<PurchasedBook> purchasedBookList = mLibrary.getPurchasedBooks();
        for (int i = 0; i < purchasedBookList.size(); i++) {
            Picasso.with(mContext).load(purchasedBookList.get(i).getPurchasedBookImage()).into(holder.mImageCover);
            holder.mImageBookState.setImageResource(R.drawable.arrow_down_black);
        }
    }

    @Override
    public int getItemCount() {
        return mLibrary.getPurchasedBooks().size() + mLibrary.getRentedRentedBookList().size();
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
