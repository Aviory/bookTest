package com.getbooks.android.ui.widget.left_menu_items;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.getbooks.android.R;
import com.getbooks.android.ui.widget.ArialNormalTextView;

/**
 * Created by avi on 18.08.17.
 */

public class FragmentServicePrivacy extends Fragment implements View.OnClickListener{
    private String txt;
    CustomSeekBar seekBar;
    RadioButton btnLeft;
    RadioButton btnRigth;

    private static FragmentServicePrivacy f;

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public static FragmentServicePrivacy getInstance() {

        if(f==null){
            f = new FragmentServicePrivacy();
        }
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.left_menu_service_privacy, container, false);
        ImageView close = v.findViewById(R.id.service_privacy_close);
        seekBar = v.findViewById(R.id.servise_seek_bar);
        btnLeft = v.findViewById(R.id.btn_service_left);
        btnLeft.setOnClickListener(this);
        btnRigth = v.findViewById(R.id.btn_service_right);
        btnRigth.setOnClickListener(this);
        ArialNormalTextView text_seekbar = v.findViewById(R.id.txt_service_privacy);
        text_seekbar.setText(txt);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = (progress * (seekBar.getHeight() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                text_seekbar.setText("" + progress);
                text_seekbar.setY(seekBar.getY() + val + seekBar.getThumbOffset() / 2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                text_seekbar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               // text_seekbar.setVisibility(View.GONE);
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().hide( FragmentServicePrivacy.getInstance()).commit();
            }
        });
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_service_left:
                seekBar.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_service_right:
                seekBar.setVisibility(View.VISIBLE);
                break;
        }
    }
}
