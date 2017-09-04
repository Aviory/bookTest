package com.getbooks.android.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;

import com.getbooks.android.R;

/**
 * Created by marinaracu on 02.09.17.
 */

public class AnimUtil {
    public static void showAnim(View menuLayout, View textPage, View layoutSeekBar, Context context){
//        menuLayout.animate().translationY(layoutSeekBar.getHeight()).setDuration(500).start();
//        textPage.animate().translationY(layoutSeekBar.getHeight()+textPage.getHeight()).setDuration(500).start();

//        layoutSeekBar.animate().translationY(-layoutSeekBar.getHeight()).setDuration(500).start();

        layoutSeekBar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_bottom));
    }

    public static void hideAnim(View menuLayout, View textPage, View layoutSeekBar, Context context){
//        menuLayout.animate().translationY(-layoutSeekBar.getHeight()).setDuration(500).start();
//        textPage.animate().translationY(-textPage.getHeight()-layoutSeekBar.getHeight()).setDuration(500).start();

        layoutSeekBar.animate().translationY(layoutSeekBar.getHeight()).setDuration(500).start();

//         layoutSeekBar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_down));
    }
}
