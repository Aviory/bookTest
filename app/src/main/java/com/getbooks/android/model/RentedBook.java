package com.getbooks.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.getbooks.android.model.enums.BookState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RentedBook implements Parcelable {
    @SerializedName("rent_id")
    @Expose
    private Integer rentId;
    @SerializedName("rent_book_sku")
    @Expose
    private String rentBookSku;
    @SerializedName("rent_book_name")
    @Expose
    private String rentBookName;
    @SerializedName("rent_book_image")
    @Expose
    private String rentBookImage;
    @SerializedName("rent_book_download_link")
    @Expose
    private String rentBookDownloadLink;
    @SerializedName("rent_meta_read_progress")
    @Expose
    private Integer rentMetaReadProgress;
    @SerializedName("rent_meta_last_read_page")
    @Expose
    private Integer rentMetaLastReadPage;

    private BookState bookState;

    public Integer getRentId() {
        return rentId;
    }

    public void setRentId(Integer rentId) {
        this.rentId = rentId;
    }

    public String getRentBookSku() {
        return rentBookSku;
    }

    public void setRentBookSku(String rentBookSku) {
        this.rentBookSku = rentBookSku;
    }

    public String getRentBookName() {
        return rentBookName;
    }

    public void setRentBookName(String rentBookName) {
        this.rentBookName = rentBookName;
    }

    public void setRentBookImage(String rentBookImage) {
        this.rentBookImage = rentBookImage;
    }

    public String getRentBookImage(){
        return this.rentBookImage;
    }

    public String getRentBookDownloadLink() {
        return rentBookDownloadLink;
    }

    public void setRentBookDownloadLink(String rentBookDownloadLink) {
        this.rentBookDownloadLink = rentBookDownloadLink;
    }

    public Integer getRentMetaReadProgress() {
        return rentMetaReadProgress;
    }

    public void setRentMetaReadProgress(Integer rentMetaReadProgress) {
        this.rentMetaReadProgress = rentMetaReadProgress;
    }

    public Integer getRentMetaLastReadPage() {
        return rentMetaLastReadPage;
    }

    public void setRentMetaLastReadPage(Integer rentMetaLastReadPage) {
        this.rentMetaLastReadPage = rentMetaLastReadPage;
    }

    @Override
    public String toString() {
        return "RentedBook{" +
                "rentId=" + rentId +
                ", rentBookSku='" + rentBookSku + '\'' +
                ", rentBookName='" + rentBookName + '\'' +
                ", rentBookImage='" + rentBookImage + '\'' +
                ", rentBookDownloadLink='" + rentBookDownloadLink + '\'' +
                ", rentMetaReadProgress=" + rentMetaReadProgress +
                ", rentMetaLastReadPage=" + rentMetaLastReadPage +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(rentBookSku);
        parcel.writeString(rentBookName);
        parcel.writeString(rentBookImage);
        parcel.writeString(rentBookDownloadLink);
    }

    protected RentedBook(Parcel in) {
        rentBookSku = in.readString();
        rentBookName = in.readString();
        rentBookImage = in.readString();
        rentBookDownloadLink = in.readString();
    }

    public static final Creator<RentedBook> CREATOR = new Creator<RentedBook>() {
        @Override
        public RentedBook createFromParcel(Parcel in) {
            return new RentedBook(in);
        }

        @Override
        public RentedBook[] newArray(int size) {
            return new RentedBook[size];
        }
    };
}

