package com.getbooks.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.getbooks.android.model.enums.BookState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PurchasedBook extends Book implements Parcelable {

    @SerializedName("purchased_book_sku")
    @Expose
    private String purchasedBookSku;
    @SerializedName("purchased_book_name")
    @Expose
    private String purchasedBookName;
    @SerializedName("purchased_book_image")
    @Expose
    private String purchasedBookImage;
    @SerializedName("purchased_book_download_link")
    @Expose
    private String purchasedBookDownloadLink;

    private BookState bookState;

    protected PurchasedBook(Parcel in) {
        purchasedBookSku = in.readString();
        purchasedBookName = in.readString();
        purchasedBookImage = in.readString();
        purchasedBookDownloadLink = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(purchasedBookSku);
        dest.writeString(purchasedBookName);
        dest.writeString(purchasedBookImage);
        dest.writeString(purchasedBookDownloadLink);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PurchasedBook> CREATOR = new Creator<PurchasedBook>() {
        @Override
        public PurchasedBook createFromParcel(Parcel in) {
            return new PurchasedBook(in);
        }

        @Override
        public PurchasedBook[] newArray(int size) {
            return new PurchasedBook[size];
        }
    };

    public String getPurchasedBookSku() {
        return purchasedBookSku;
    }

    public void setPurchasedBookSku(String purchasedBookSku) {
        this.purchasedBookSku = purchasedBookSku;
    }

    public String getPurchasedBookName() {
        return purchasedBookName;
    }

    public void setPurchasedBookName(String purchasedBookName) {
        this.purchasedBookName = purchasedBookName;
    }

    public String getBookImage() {
        return purchasedBookImage;
    }

    @Override
    public void setBookState(BookState bookState) {
        this.bookState = bookState;
    }

    @Override
    public BookState getBookState() {
        return bookState;
    }

    @Override
    public String getBookDownloadLink() {
        return purchasedBookDownloadLink;
    }

    public void setPurchasedBookImage(String purchasedBookImage) {
        this.purchasedBookImage = purchasedBookImage;
    }

    public String getPurchasedBookDownloadLink() {
        return purchasedBookDownloadLink;
    }

    public void setPurchasedBookDownloadLink(String purchasedBookDownloadLink) {
        this.purchasedBookDownloadLink = purchasedBookDownloadLink;
    }

    @Override
    public String toString() {
        return "PurchasedBook{" +
                "purchasedBookSku='" + purchasedBookSku + '\'' +
                ", purchasedBookName='" + purchasedBookName + '\'' +
                ", purchasedBookImage='" + purchasedBookImage + '\'' +
                ", purchasedBookDownloadLink='" + purchasedBookDownloadLink + '\'' +
                '}';
    }
}


