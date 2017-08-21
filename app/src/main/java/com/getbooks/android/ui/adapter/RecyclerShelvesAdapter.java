package com.getbooks.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.getbooks.android.R;
import com.getbooks.android.chashe.PicassoCache;
import com.getbooks.android.model.BookDetail;
import com.squareup.picasso.Callback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marina on 26.07.17.
 */

public class RecyclerShelvesAdapter extends RecyclerView.Adapter<RecyclerShelvesAdapter.ViewHolder> {

    private List<BookDetail> mLibrary;
    private Context mContext;

    public RecyclerShelvesAdapter(List<BookDetail> mLibrary, Context context) {
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
        PicassoCache.getPicassoInstance(mContext).load(mLibrary.get(position)
                .getImageDownloadLink())
//                .networkPolicy(NetworkPolicy.OFFLINE)
//                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                .error(R.drawable.book_1)
                .into(holder.mImageCover, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        holder.mImageCover.setImageResource(R.drawable.book_1);
                    }
                });
        BookDetail book = mLibrary.get(position);
        switch (book.getBookState()) {
            case CLOUD_BOOK:
                holder.mImageBookState.setImageResource(R.drawable.arrow_down_black);
                break;
            case PURCHASED_BOOK:
                if (book.isIsBookFirstOpen()) {
                    holder.mImageNewBook.setVisibility(View.VISIBLE);
                }
                holder.mImageBookState.setImageResource(R.drawable.check_black);
                break;
            case RENTED_BOOK:
                if (book.isIsBookFirstOpen()) {
                    holder.mImageNewBook.setVisibility(View.VISIBLE);
                }
                holder.mImageBookState.setImageResource(R.drawable.clock_black);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mLibrary.size();
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
