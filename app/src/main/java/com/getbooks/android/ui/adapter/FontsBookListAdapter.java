package com.getbooks.android.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.getbooks.android.R;
import com.getbooks.android.skyepubreader.CustomFont;

import java.util.ArrayList;

/**
 * Created by marinaracu on 03.09.17.
 */

public class FontsBookListAdapter extends ArrayAdapter<CustomFont> {

    ArrayList<CustomFont> fonts;
    Context mContext;

    private BookFontUpdateChangeListener mBookFontUpdateChangeListener;

    public void setBookFontUpdateChangeListener(BookFontUpdateChangeListener bookFontUpdateChangeListener) {
        this.mBookFontUpdateChangeListener = bookFontUpdateChangeListener;
    }

    public interface BookFontUpdateChangeListener {
        void applyFont(int fontIndex);
    }

//    @Override
//    public void onClick(View view) {
//        if (view.getId() >= 5100 && view.getId() < 5500) {
//            // one of fontButtons is clicked.
//            mBookFontUpdateChangeListener.applyFont(view.getId() - 5100);
//        }
//    }

    // View lookup cache
    private static class ViewHolder {
        TextView mFontName;
    }

    public FontsBookListAdapter(ArrayList<CustomFont> data, Context context) {
        super(context, R.layout.font_item, data);
        this.fonts = data;
        this.mContext = context;

    }

    @Override
    public int getCount() {
        return fonts.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CustomFont dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.font_item, parent, false);
            viewHolder.mFontName = (TextView) convertView.findViewById(R.id.txt_font_item);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        CustomFont customFont = fonts.get(position);
        Typeface tf = null;
        if (customFont.fontFileName == null || customFont.fontFileName.isEmpty()) {
            tf = getTypeface(customFont.fontFaceName, Typeface.BOLD);
        } else {
            tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/book_fonts/" + customFont.fontFileName);
        }
        if (tf != null) viewHolder.mFontName.setTypeface(tf);
        viewHolder.mFontName.setId(5100 + position);
        viewHolder.mFontName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // one of fontButtons is clicked.
                mBookFontUpdateChangeListener.applyFont(view.getId() - 5100);
            }
        });

        viewHolder.mFontName.setText(customFont.fontFaceName);

        return convertView;
    }

    public Typeface getTypeface(String fontName, int fontStyle) {
        Typeface tf = null;
        if (fontName.toLowerCase().contains("book")) {
            tf = Typeface.create(Typeface.DEFAULT, fontStyle);
        } else if (fontName.toLowerCase().contains("default")) {
            tf = Typeface.create(Typeface.DEFAULT, fontStyle);
        } else if (fontName.toLowerCase().contains("mono")) {
            tf = Typeface.create(Typeface.MONOSPACE, fontStyle);
        } else if ((fontName.toLowerCase().contains("sans"))) {
            tf = Typeface.create(Typeface.SANS_SERIF, fontStyle);
        } else if ((fontName.toLowerCase().contains("serif"))) {
            tf = Typeface.create(Typeface.SERIF, fontStyle);
        }
        return tf;
    }
}
