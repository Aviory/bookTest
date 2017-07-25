package com.getbooks.android.model;

import java.util.List;

/**
 * Created by marina on 25.07.17.
 */

public class Library {

    private List<RentedBook> mRentedRentedBookList;
    private List<PurchasedBook> mPurchasedBooks;

    private long mSetTimeLoad;

    public List<PurchasedBook> getPurchasedBooks() {
        return mPurchasedBooks;
    }

    public void setPurchasedBooks(List<PurchasedBook> purchasedBooks) {
        this.mPurchasedBooks = purchasedBooks;
    }

    public List<RentedBook> getRentedRentedBookList() {
        return mRentedRentedBookList;
    }

    public void setRentedRentedBookList(List<RentedBook> rentedRentedBookList) {
        this.mRentedRentedBookList = rentedRentedBookList;
    }

    public long getSetTimeLoad() {
        return mSetTimeLoad;
    }

    public void setSetTimeLoad(long setTimeLoad) {
        this.mSetTimeLoad = setTimeLoad;
    }
}
