package com.getbooks.android.model.enums;

/**
 * Created by marina on 27.07.17.
 */

public enum BookState {
    CLOUD_BOOK("CloudBook"),
    RENTED_BOOK("RentedBook"),
    PURCHASED_BOOK("PurchasedBook");

    private String mState;

    BookState(String mState) {
        this.mState = mState;
    }

    public String getState() {
        return mState;
    }
}
