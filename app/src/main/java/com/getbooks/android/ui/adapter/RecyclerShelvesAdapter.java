package com.getbooks.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.getbooks.android.R;
import com.getbooks.android.chashe.PicassoCache;
import com.getbooks.android.model.Library;
import com.squareup.picasso.Callback;

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
        PicassoCache.getPicassoInstance(mContext).load(mLibrary.getAllBook().get(position)
                .getBookImage())
//                .networkPolicy(NetworkPolicy.OFFLINE)
//                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                .error(R.drawable.book_1)
                .into(holder.mImageCover, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Log.d("AAAAAA", "aaaaaa");
                        holder.mImageCover.setImageResource(R.drawable.book_1);
                    }
                });
        switch (mLibrary.getAllBook().get(position).getBookState()) {
            case DOWNLOAD:
                holder.mImageBookState.setImageResource(R.drawable.arrow_down_black);
                break;
            case PURCHASED:
                holder.mImageBookState.setImageResource(R.drawable.check_black);
                break;
            case RENTED:
                holder.mImageBookState.setImageResource(R.drawable.clock_black);
                break;
            case NEWBOOK:
                holder.mImageNewBook.setVisibility(View.VISIBLE);
                break;
        }

        holder.mImageCover.setOnClickListener(view -> {
            Toast.makeText(mContext, "Book click", Toast.LENGTH_SHORT).show();
//            Log.d("Downloaded", String.valueOf(mLibrary.getAllBook().get(position)));
//            BookDownLoadManager bookDownLoadManager = new BookDownLoadManager(mContext);
//            bookDownLoadManager.onResume();
//            bookDownLoadManager.downloadBook(mLibrary.getAllBook().get(position).getBookDownloadLink());
        });
    }

    @Override
    public int getItemCount() {
        return mLibrary.getAllBook().size();
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
