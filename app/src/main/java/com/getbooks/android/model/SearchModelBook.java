package com.getbooks.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by marinaracu on 06.09.17.
 */

public class SearchModelBook implements Parcelable {
    public String text;
    public String nodeName;
    public int uniqueIndex;
    public int startOffset;
    public int endOffset;
    public int chapterIndex;
    public String chapterTitle;
    public int pageIndex;
    public double pagePositionInChapter;
    public double pagePositionInBook;
    public int numberOfSearched;
    public int numberOfSearchedInChapter;
    public int numberOfPagesInChapter;
    public int numberOfChaptersInBook;
    public int mode;

    public SearchModelBook(String text, String nodeName, int uniqueIndex, int startOffset, int endOffset, int chapterIndex, String chapterTitle, int pageIndex, double pagePositionInChapter, double pagePositionInBook, int numberOfSearched, int numberOfSearchedInChapter, int numberOfPagesInChapter, int numberOfChaptersInBook,int mode) {
        this.text = text;
        this.nodeName = nodeName;
        this.uniqueIndex = uniqueIndex;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.chapterIndex = chapterIndex;
        this.chapterTitle = chapterTitle;
        this.pageIndex = pageIndex;
        this.pagePositionInChapter = pagePositionInChapter;
        this.pagePositionInBook = pagePositionInBook;
        this.numberOfSearched = numberOfSearched;
        this.numberOfSearchedInChapter = numberOfSearchedInChapter;
        this.numberOfPagesInChapter = numberOfPagesInChapter;
        this.numberOfChaptersInBook = numberOfChaptersInBook;
        this.mode = mode;
    }

    public SearchModelBook(Parcel in) {
        text = in.readString();
        nodeName = in.readString();
        uniqueIndex = in.readInt();
        startOffset = in.readInt();
        endOffset = in.readInt();
        chapterIndex = in.readInt();
        chapterTitle = in.readString();
        pageIndex = in.readInt();
        pagePositionInChapter = in.readDouble();
        pagePositionInBook = in.readDouble();
        numberOfSearched = in.readInt();
        numberOfSearchedInChapter = in.readInt();
        numberOfPagesInChapter = in.readInt();
        numberOfChaptersInBook = in.readInt();
    }

    public static final Creator<SearchModelBook> CREATOR = new Creator<SearchModelBook>() {
        @Override
        public SearchModelBook createFromParcel(Parcel in) {
            return new SearchModelBook(in);
        }

        @Override
        public SearchModelBook[] newArray(int size) {
            return new SearchModelBook[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeString(nodeName);
        parcel.writeInt(uniqueIndex);
        parcel.writeInt(startOffset);
        parcel.writeInt(endOffset);
        parcel.writeInt(chapterIndex);
        parcel.writeString(chapterTitle);
        parcel.writeInt(pageIndex);
        parcel.writeDouble(pagePositionInChapter);
        parcel.writeDouble(pagePositionInBook);
        parcel.writeInt(numberOfSearched);
        parcel.writeInt(numberOfSearchedInChapter);
        parcel.writeInt(numberOfPagesInChapter);
        parcel.writeInt(numberOfChaptersInBook);
    }

    @Override
    public String toString() {
        return "SearchModelBook{" +
                "text='" + text + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", uniqueIndex=" + uniqueIndex +
                ", startOffset=" + startOffset +
                ", endOffset=" + endOffset +
                ", chapterIndex=" + chapterIndex +
                ", chapterTitle='" + chapterTitle + '\'' +
                ", pageIndex=" + pageIndex +
                ", pagePositionInChapter=" + pagePositionInChapter +
                ", pagePositionInBook=" + pagePositionInBook +
                ", numberOfSearched=" + numberOfSearched +
                ", numberOfSearchedInChapter=" + numberOfSearchedInChapter +
                ", numberOfPagesInChapter=" + numberOfPagesInChapter +
                ", numberOfChaptersInBook=" + numberOfChaptersInBook +
                '}';
    }
}
