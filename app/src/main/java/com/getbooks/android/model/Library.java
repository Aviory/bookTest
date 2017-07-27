package com.getbooks.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marina on 25.07.17.
 */

public class Library implements Parcelable {

    private List<RentedBook> mRentedRentedBookList;
    private List<PurchasedBook> mPurchasedBooks;

    private List<Book> mAllBook;

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

    public List<Book> getAllBook() {
        return mAllBook;
    }

    public void setAllBook(List<Book> mAllBook) {
        this.mAllBook = mAllBook;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Library() {
    }

    protected Library(Parcel in) {
        mAllBook = new ArrayList<Book>();
        in.readList(mAllBook, Book.class.getClassLoader());
        mPurchasedBooks = new ArrayList<PurchasedBook>();
        in.readList(mPurchasedBooks, PurchasedBook.class.getClassLoader());
        mRentedRentedBookList = new ArrayList<RentedBook>();
        in.readList(mRentedRentedBookList, RentedBook.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(mAllBook);
        parcel.writeList(mPurchasedBooks);
        parcel.writeList(mRentedRentedBookList);
    }

    public static final Parcelable.Creator<Library> CREATOR = new Parcelable.Creator<Library>() {


        @Override
        public Library createFromParcel(Parcel parcel) {
            return new Library(parcel);
        }

        @Override
        public Library[] newArray(int i) {
            return new Library[i];
        }
    };
}
