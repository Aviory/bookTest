package com.getbooks.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PurchasedBook {

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

    public String getPurchasedBookImage() {
        return purchasedBookImage;
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


