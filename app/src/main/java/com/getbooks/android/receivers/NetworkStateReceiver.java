package com.getbooks.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.getbooks.android.events.Events;
import com.getbooks.android.model.enums.NetworkState;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by marina on 09.08.17.
 */

public class NetworkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            NetworkInfo.State wifiState = null;
            NetworkInfo.State mobileState = null;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (wifiState != null && mobileState != null
                    && NetworkInfo.State.CONNECTED != wifiState
                    && NetworkInfo.State.CONNECTED == mobileState) {
                // phone network connect success
                EventBus.getDefault().post(new Events.NetworkStateChange(NetworkState.INTERNET_CONNECT));
            } else if (wifiState != null && mobileState != null
                    && NetworkInfo.State.CONNECTED != wifiState
                    && NetworkInfo.State.CONNECTED != mobileState) {
                // no network;
                EventBus.getDefault().post(new Events.NetworkStateChange(NetworkState.NO_INTERNET_CONNECTON));
            } else if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {
                // wift connect success
                EventBus.getDefault().post(new Events.NetworkStateChange(NetworkState.INTERNET_CONNECT));
            }
        }
    }
}
