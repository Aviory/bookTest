package com.getbooks.android.ui;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.getbooks.android.R;
import com.getbooks.android.util.LogUtil;
import com.getbooks.android.util.UiUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import butterknife.ButterKnife;

/**
 * Created by marina on 12.07.17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private final static int PERMISSION_STORAGE_REQUEST = 1221;
    private final static int PERMISSION_PHOTO_REQUEST = 1222;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!UiUtil.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (getLayout() > 0) {
            setContentView(getLayout());
            ButterKnife.bind(this);
        }
    }

    public abstract int getLayout();

    public <T extends BaseActivity> T getAct() {
        return (T) this;
    }

    public static <T> Fragment addFragment(AppCompatActivity activity,
                                           Class<T> clazz,              // class of Fragment
                                           int container,               // layout
                                           @Nullable Bundle arg,        // arguments
                                           boolean addBackStack,        // add to back stack
                                           boolean replaceFragment,     // true - replace , false - add
                                           boolean animation,           // on/off animation
                                           int[] animRes) {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(clazz.getCanonicalName());
        if (fragment == null) {
            try {
                Constructor<T> constructor = clazz.getConstructor();
                fragment = (Fragment) constructor.newInstance();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (fragment.isAdded()) {
            return fragment;
        }

        if (fragment == null) return null;
        if (arg != null) {
            fragment.setArguments(arg);
        }

        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if (addBackStack) {
            transaction.addToBackStack(clazz.getCanonicalName());
        }

        if (replaceFragment) {
            transaction.replace(container, fragment, clazz.getCanonicalName());
        } else {
            transaction.add(container, fragment, clazz.getSimpleName());
        }

        if (animation) {
            if (animRes == null) {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right);
            } else if (animRes.length == 2) {
                transaction.setCustomAnimations(animRes[0], animRes[1]);
            } else if (animRes.length == 4) {
                transaction.setCustomAnimations(animRes[0], animRes[1], animRes[2], animRes[3]);
            }
        }

        transaction.commitAllowingStateLoss();
        return fragment;
    }

    // Checking if the user has granted permission of external storage
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_STORAGE_REQUEST);

                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_STORAGE_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO
                    // Permission Granted
                    //resume tasks needing this permission
                    LogUtil.log(this, "Permission granded");
                } else {
                    //TODO
                    // Permission Denied
                    LogUtil.log(this, "Permission denied");
                    finish();
                }
                break;
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }
}
