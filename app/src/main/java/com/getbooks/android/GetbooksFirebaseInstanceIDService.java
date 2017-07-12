package com.getbooks.android;

import com.getbooks.android.util.LogUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by marina on 11.07.17.
 */

public class GetbooksFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        LogUtil.log(this.getClass().getName(), "RefreshToken" + refreshToken);
    }
}
