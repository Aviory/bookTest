package com.getbooks.android.ui;

import android.app.Activity;

import static com.getbooks.android.util.CommonUtils.checkNotNull;

/**
 * Created by marina on 12.07.17.
 */

public class AuthHelper {

    private String mTokenDevice;
    private String mRedirectUrl;

    private AuthHelper(String tokenDevice, String redirectUrl) {
        this.mTokenDevice = tokenDevice;
        this.mRedirectUrl = redirectUrl;
    }

    public void loginFromActivity(Activity context){

    }


    public static final class Builder {
        private String mTokenDevice;
        private String mRedirectUr;

        public Builder withTokenDevice(String tokenDevice) {
            this.mTokenDevice = checkNotNull(tokenDevice, "tokenDevice == null");
            return this;
        }

        public Builder withRedirectUrl(String redirectUrl) {
            this.mRedirectUr = checkNotNull(redirectUrl, "redirectUrl == null");
            return this;
        }

        public AuthHelper build() {
            return new AuthHelper(mTokenDevice, mRedirectUr);
        }
    }
}
