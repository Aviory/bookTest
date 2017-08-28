package com.getbooks.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.getbooks.android.model.enums.BookState;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by marina on 11.08.17.
 */

public class Book implements Parcelable {
    private String mBookAuthors;
    private int mUserId;
    private String mBookName;
    private String mBookImage;
    private String mBookSku;
    private String mBookContentID;
    private boolean mBookIsDeleted;
    private boolean mBookIsEncrypted;
    private String mBookLanguage;
    private String mBookLanguageDirection;
    private String mBookPublishedYear;
    private String mBookPublishers;
    private Calendar mReadDateTime;
    private int mUpdateDate;
    private Calendar mCreatedDate;
    private boolean mIsBookAtTheEnd;
    private int mLastPage;
    private String mImageDownloadLink;
    private String mBookDownloadLink;
    private BookState mBookState;
    private int mProgress = 0;
    private int mViewPosition = 0;
    private boolean mIsBookFirstOpen;
    private int mLastChapter;
    private String mChapterList;
    private String mPagesPerArticleList;
    private int mBookPhysicalPage;
    private int mLastReadingParagraph;

    private boolean mIsBookRented;

    private byte[] mBookInstance;

    public Book() {
    }

    protected Book(Parcel in) {
        mBookAuthors = in.readString();
        mUserId = in.readInt();
        mBookName = in.readString();
        mBookImage = in.readString();
        mBookSku = in.readString();
        mBookContentID = in.readString();
        mBookIsDeleted = in.readByte() != 0;
        mBookIsEncrypted = in.readByte() != 0;
        mBookLanguage = in.readString();
        mBookLanguageDirection = in.readString();
        mBookPublishedYear = in.readString();
        mBookPublishers = in.readString();
        mUpdateDate = in.readInt();
        mIsBookAtTheEnd = in.readByte() != 0;
        mLastPage = in.readInt();
        mImageDownloadLink = in.readString();
        mBookDownloadLink = in.readString();
        mProgress = in.readInt();
        mViewPosition = in.readInt();
        mIsBookFirstOpen = in.readByte() != 0;
        mLastChapter = in.readInt();
        mChapterList = in.readString();
        mPagesPerArticleList = in.readString();
        mBookPhysicalPage = in.readInt();
        mLastReadingParagraph = in.readInt();
        mIsBookRented = in.readByte() != 0;
        mBookInstance = in.createByteArray();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getBookName() {
        return mBookName;
    }

    public void setBookName(String bookName) {
        this.mBookName = bookName;
    }

    public boolean isIsBookFirstOpen() {
        return mIsBookFirstOpen;
    }

    public void setIsBookFirstOpen(boolean isBookFirstOpen) {
        this.mIsBookFirstOpen = isBookFirstOpen;
    }

    public String getBookSku() {
        return mBookSku;
    }

    public void setBookSku(String bookSku) {
        this.mBookSku = bookSku;
    }

    public String getImageDownloadLink() {
        return mImageDownloadLink;
    }

    public void setImageDownloadLink(String imageDownloadLink) {
        this.mImageDownloadLink = imageDownloadLink;
    }

    public String getBookDownloadLink() {
        return mBookDownloadLink;
    }

    public void setBookDownloadLink(String bookDownloadLink) {
        this.mBookDownloadLink = bookDownloadLink;
    }

    public BookState getBookState() {
        return mBookState;
    }

    public void setBookState(String bookState) {
        if (BookState.CLOUD_BOOK.getState().equals(bookState)) {
            this.mBookState = BookState.CLOUD_BOOK;
        } else if (BookState.PURCHASED_BOOK.getState().equals(bookState)) {
            this.mBookState = BookState.PURCHASED_BOOK;
        } else if (BookState.RENTED_BOOK.getState().equals(bookState)) {
            this.mBookState = BookState.RENTED_BOOK;
        }
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
    }

    public int getViewPosition() {
        return mViewPosition;
    }

    public void setViewPosition(int viewPosition) {
        this.mViewPosition = viewPosition;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public String getBookImage() {
        return mBookImage;
    }

    public void setBookImage(String bookImage) {
        this.mBookImage = bookImage;
    }

    public String getBookContentID() {
        return mBookContentID;
    }

    public void setBookContentID(String bookContentID) {
        mBookContentID = bookContentID;
    }

    public String getBookPublishers() {
        return mBookPublishers;
    }

    public void setBookPublishers(String bookPublishers) {
        mBookPublishers = bookPublishers;
    }

    public String getBookAuthors() {
        return mBookAuthors;
    }

    public void setBookAuthors(String bookAuthors) {
        mBookAuthors = bookAuthors;
    }

    public String getBookLanguage() {
        return mBookLanguage;
    }

    public void setBookLanguage(String bookLanguage) {
        mBookLanguage = bookLanguage;
    }

    public String getBookLanguageDirection() {
        return mBookLanguageDirection;
    }

    public void setBookLanguageDirection(String bookLanguageDirection) {
        mBookLanguageDirection = bookLanguageDirection;
    }

    public boolean isBookIsEncrypted() {
        return mBookIsEncrypted;
    }

    public void setBookIsEncrypted(boolean bookIsEncrypted) {
        mBookIsEncrypted = bookIsEncrypted;
    }

    public String getBookPublishedYear() {
        return mBookPublishedYear;
    }

    public void setBookPublishedYear(String bookPublishedYear) {
        mBookPublishedYear = bookPublishedYear;
    }

    public Calendar getReadDateTime() {
        return mReadDateTime;
    }

    public void setReadDateTime(Calendar readDateTime) {
        mReadDateTime = readDateTime;
    }

    public boolean isBookAtTheEnd() {
        return mIsBookAtTheEnd;
    }

    public void setBookAtTheEnd(boolean bookAtTheEnd) {
        mIsBookAtTheEnd = bookAtTheEnd;
    }

    public boolean isBookIsDeleted() {
        return mBookIsDeleted;
    }

    public void setBookIsDeleted(boolean bookIsDeleted) {
        mBookIsDeleted = bookIsDeleted;
    }

    public int getUpdateDate() {
        return mUpdateDate;
    }

    public void setUpdateDate(int updateDate) {
        mUpdateDate = updateDate;
    }

    public int getLastPage() {
        return mLastPage;
    }

    public void setLastPage(int lastPage) {
        this.mLastPage = lastPage;
    }

    public Calendar getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(Calendar createdDate) {
        this.mCreatedDate = createdDate;
    }

    public byte[] getBookInstance() {
        return mBookInstance;
    }

    public void setBookInstance(byte[] bookInstance) {
        this.mBookInstance = bookInstance;
    }

    public int getLastChapter() {
        return mLastChapter;
    }

    public void setLastChapter(int lastChapter) {
        this.mLastChapter = lastChapter;
    }

    public String getChapterList() {
        return mChapterList;
    }

    public void setChapterList(String chapterList) {
        this.mChapterList = chapterList;
    }

    public String getPagesPerArticleList() {
        return mPagesPerArticleList;
    }

    public void setPagesPerArticleList(String pagesPerArticleList) {
        this.mPagesPerArticleList = pagesPerArticleList;
    }

    public int getBookPhysicalPage() {
        return mBookPhysicalPage;
    }

    public void setBookPhysicalPage(int bookPhysicalPage) {
        this.mBookPhysicalPage = bookPhysicalPage;
    }

    public int getLastReadingParagraph() {
        return mLastReadingParagraph;
    }

    public void setLastReadingParagraph(int lastReadingParagraph) {
        this.mLastReadingParagraph = lastReadingParagraph;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;

        Book that = (Book) o;

        if (mUserId != that.mUserId) return false;
        if (!mBookName.equals(that.mBookName)) return false;
        return mBookSku.equals(that.mBookSku);

    }

    @Override
    public int hashCode() {
        int result = mUserId;
        result = 31 * result + mBookName.hashCode();
        result = 31 * result + mBookSku.hashCode();
        return result;
    }

    public boolean isIsBookRented() {
        return mIsBookRented;
    }

    public void setIsBookRented(boolean isBookRented) {
        this.mIsBookRented = isBookRented;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mBookAuthors);
        parcel.writeInt(mUserId);
        parcel.writeString(mBookName);
        parcel.writeString(mBookImage);
        parcel.writeString(mBookSku);
        parcel.writeString(mBookContentID);
        parcel.writeByte((byte) (mBookIsDeleted ? 1 : 0));
        parcel.writeByte((byte) (mBookIsEncrypted ? 1 : 0));
        parcel.writeString(mBookLanguage);
        parcel.writeString(mBookLanguageDirection);
        parcel.writeString(mBookPublishedYear);
        parcel.writeString(mBookPublishers);
        parcel.writeInt(mUpdateDate);
        parcel.writeByte((byte) (mIsBookAtTheEnd ? 1 : 0));
        parcel.writeInt(mLastPage);
        parcel.writeString(mImageDownloadLink);
        parcel.writeString(mBookDownloadLink);
        parcel.writeInt(mProgress);
        parcel.writeInt(mViewPosition);
        parcel.writeByte((byte) (mIsBookFirstOpen ? 1 : 0));
        parcel.writeInt(mLastChapter);
        parcel.writeString(mChapterList);
        parcel.writeString(mPagesPerArticleList);
        parcel.writeInt(mBookPhysicalPage);
        parcel.writeInt(mLastReadingParagraph);
        parcel.writeByte((byte) (mIsBookRented ? 1 : 0));
        parcel.writeByteArray(mBookInstance);
    }

    @Override
    public String toString() {
        return "Book{" +
                "mBookAuthors='" + mBookAuthors + '\'' +
                ", mUserId=" + mUserId +
                ", mBookName='" + mBookName + '\'' +
                ", mBookImage='" + mBookImage + '\'' +
                ", mBookSku='" + mBookSku + '\'' +
                ", mBookContentID='" + mBookContentID + '\'' +
                ", mBookIsDeleted=" + mBookIsDeleted +
                ", mBookIsEncrypted=" + mBookIsEncrypted +
                ", mBookLanguage='" + mBookLanguage + '\'' +
                ", mBookLanguageDirection='" + mBookLanguageDirection + '\'' +
                ", mBookPublishedYear='" + mBookPublishedYear + '\'' +
                ", mBookPublishers='" + mBookPublishers + '\'' +
                ", mReadDateTime=" + mReadDateTime +
                ", mUpdateDate=" + mUpdateDate +
                ", mCreatedDate=" + mCreatedDate +
                ", mIsBookAtTheEnd=" + mIsBookAtTheEnd +
                ", mLastPage=" + mLastPage +
                ", mImageDownloadLink='" + mImageDownloadLink + '\'' +
                ", mBookDownloadLink='" + mBookDownloadLink + '\'' +
                ", mBookState=" + mBookState +
                ", mProgress=" + mProgress +
                ", mViewPosition=" + mViewPosition +
                ", mIsBookFirstOpen=" + mIsBookFirstOpen +
                ", mLastChapter=" + mLastChapter +
                ", mChapterList='" + mChapterList + '\'' +
                ", mPagesPerArticleList='" + mPagesPerArticleList + '\'' +
                ", mBookPhysicalPage=" + mBookPhysicalPage +
                ", mLastReadingParagraph=" + mLastReadingParagraph +
                ", mIsBookRented=" + mIsBookRented +
                ", mBookInstance=" + Arrays.toString(mBookInstance) +
                '}';
    }
}
