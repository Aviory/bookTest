package com.getbooks.android.ui.fragments;

import android.app.Fragment;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.shapes.RoundRectShape;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;

import com.getbooks.android.GetbooksApplication;
import com.getbooks.android.R;
import com.getbooks.android.events.Events;
import com.getbooks.android.skyepubreader.CustomFont;
import com.getbooks.android.ui.adapter.FontsBookListAdapter;
import com.getbooks.android.util.UiUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marinaracu on 15.08.17.
 */

public class BookSettingMenuFragment extends Fragment implements View.OnTouchListener,
        View.OnClickListener, FontsBookListAdapter.BookFontUpdateChangeListener {

    public interface ChangeBookSettingListener {
        void changeScreenBrightness(int progress);

        void increaseLineSpacing();

        void dicreaseLineSpacing();

        void increaseFontSize();

        void dicreaseFontSize();

        void checkSettingsBook();

        void applyThemeBook(int themeIndex);

        void applyBookFont(int fontIndex);
    }

    public static final String TAG_SETTING_BOOK_FRAGMENT = "book_setting_tag";

    @BindView(R.id.layout_book_settings_root)
    protected FrameLayout mRootLayoutBookSettings;
    @BindView(R.id.layout_book_settings)
    protected LinearLayout mLayoutBookSettings;
    @BindView(R.id.seek_bar_brightness)
    protected SeekBar mSeemBarBrightness;
    @BindView(R.id.img_line_spacing_increase)
    protected ImageView mImageLineSpacingIncrease;
    @BindView(R.id.img_line_spacing_dicrease)
    protected ImageView mImageLineSpacingDicrease;
    @BindView(R.id.img_increase_font_size)
    protected ImageView mImageIncreaseFontSize;
    @BindView(R.id.img_dicrese_font_size)
    protected ImageView mImageDicreaseFontSize;
    @BindView(R.id.img_white_book_background)
    protected ImageView mImageWhiteBookBackground;
    @BindView(R.id.img_beige_book_background)
    protected ImageView mImageBeigeBookBackground;
    @BindView(R.id.img_black_book_background)
    protected ImageView mImageBlackBookBackground;
    @BindView(R.id.list_fonts)
    protected ListView mListBookFonts;
    GetbooksApplication app;
    ArrayList<CustomFont> fonts = new ArrayList<CustomFont>();
    FontsBookListAdapter mFontsBookListAdapter;
    private ChangeBookSettingListener mChangeBookSettingListener;


    public void setChangeBookSettingListener(ChangeBookSettingListener changeBookSettingListener) {
        this.mChangeBookSettingListener = changeBookSettingListener;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, view);
        app = (GetbooksApplication) getActivity().getApplication();
        mImageLineSpacingIncrease.setId(4001);
        mImageLineSpacingDicrease.setId(4000);
        mImageIncreaseFontSize.setId(5001);
        mImageDicreaseFontSize.setId(5000);
        mImageWhiteBookBackground.setId(7000);
        mImageBeigeBookBackground.setId(7001);
        mImageBlackBookBackground.setId(7002);
        mSeemBarBrightness.setId(997);
        mSeemBarBrightness.setMax(999);
        makeFonts();
        mFontsBookListAdapter = new FontsBookListAdapter(fonts, getActivity());
        mFontsBookListAdapter.setBookFontUpdateChangeListener(this);
        mListBookFonts.setAdapter(mFontsBookListAdapter);
        UiUtil.increaseTouchArea(mRootLayoutBookSettings, mSeemBarBrightness);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageLineSpacingIncrease.setOnClickListener(this);
        mImageLineSpacingDicrease.setOnClickListener(this);
        mImageIncreaseFontSize.setOnClickListener(this);
        mImageDicreaseFontSize.setOnClickListener(this);
        mRootLayoutBookSettings.setOnTouchListener(this);
        mImageWhiteBookBackground.setOnClickListener(this);
        mImageBeigeBookBackground.setOnClickListener(this);
        mImageBlackBookBackground.setOnClickListener(this);
        mSeemBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mChangeBookSettingListener.changeScreenBrightness(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public int getLayout() {
        return R.layout.fragment_menu_book_settings;
    }

    @Override
    public void onClick(View view) {
        // line space
        if (view.getId() == 4000) {
            // decrease button
            mChangeBookSettingListener.dicreaseLineSpacing();
        } else if (view.getId() == 4001) {
            // increase button
            mChangeBookSettingListener.increaseLineSpacing();
        }

        // fonts related
        if (view.getId() == 5000) {
            // decrease button
            mChangeBookSettingListener.dicreaseFontSize();
        } else if (view.getId() == 5001) {
            // increase button
            mChangeBookSettingListener.increaseFontSize();
        }

        if (view.getId() >= 7000 && view.getId() < 7100) {
            mChangeBookSettingListener.checkSettingsBook();
            mChangeBookSettingListener.applyThemeBook(view.getId() - 7000);
        }
    }

    @Override
    public void applyFont(int fontIndex) {
        mChangeBookSettingListener.applyBookFont(fontIndex);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Rect rect = new Rect();
        mLayoutBookSettings.getGlobalVisibleRect(rect);
        if (!rect.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) {
            EventBus.getDefault().post(new Events.CloseContentMenuSetting(false));
            getActivity().getFragmentManager().beginTransaction().remove(BookSettingMenuFragment.this).commit();
        }
        return false;
    }

    public void makeFonts() {
        fonts.clear();
        fonts.add(0, new CustomFont("Frank Ruehl", "frank.ttf"));
    }
}
