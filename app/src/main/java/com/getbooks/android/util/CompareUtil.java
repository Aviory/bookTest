package com.getbooks.android.util;

import android.util.Log;

import com.getbooks.android.model.BookModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by avi on 30.08.17.
 */

public class CompareUtil {

    public static List<BookModel> compareByAuthorName(List<BookModel> list) {
        List<BookModel> sortedList = new ArrayList<>();
        sortedList.addAll(list);
        Collections.sort(sortedList, new AuthorNameComparator(true));
        return sortedList;
    }

    public static List<BookModel> compareByBookName(List<BookModel> list) {
        List<BookModel> sortedList = new ArrayList<>();
        sortedList.addAll(list);
        Collections.sort(sortedList, new BookNameComparator(true));
        return sortedList;
    }

    public static List<BookModel> compareByReadDate(List<BookModel> list) {
        List<BookModel> sortedList = new ArrayList<>();
        sortedList.addAll(list);
        Collections.sort(sortedList, new DateComparator(true));
        return sortedList;
    }

    public static List<BookModel> compareByAddDate(List<BookModel> list) {
        List<BookModel> sortedList = new ArrayList<>();
        sortedList.addAll(list);
        Collections.sort(sortedList, new AddDateComparator(true));
        return sortedList;
    }


    private static class BookNameComparator implements Comparator<BookModel> {

        private boolean reverse;

        public BookNameComparator(boolean reverse) {
            this.reverse = reverse;
        }

        public int compare(BookModel o1, BookModel o2) {
            if (o1.fileName == null || o2.fileName == null) {
                return o2.fileName != null ? (reverse ? 1 : -1) : (o1.fileName != null ? (reverse ? -1 : 1) : 0);
            }
            int result = o1.fileName.compareTo(o2.fileName);
            return reverse ? result * -1 : result;
        }
    }

    private static class AuthorNameComparator implements Comparator<BookModel> {

        private boolean reverse;

        public AuthorNameComparator(boolean reverse) {
            this.reverse = reverse;
        }

        public int compare(BookModel o1, BookModel o2) {
            if (o1.getBookAuthors() == null || o2.getBookAuthors() == null) {
                return o2.getBookAuthors() != null ? (reverse ? 1 : -1) : (o1.getBookAuthors() != null ? (reverse ? -1 : 1) : 0);
            }
            int result = o1.getBookAuthors().compareTo(o2.getBookAuthors());
            return reverse ? result * -1 : result;
        }
    }


    private static class AddDateComparator implements Comparator<BookModel> {

        private boolean reverse;

        public AddDateComparator(boolean reverse) {
            this.reverse = reverse;
        }

        public int compare(BookModel o1, BookModel o2) {
            if (o1.getCreatedDate() == null || o2.getCreatedDate() == null) {
                return o2.getCreatedDate() != null ? (reverse ? 1 : -1) : (o1.getCreatedDate() != null ? (reverse ? -1 : 1) : 0);
            }
            int result = o1.getCreatedDate().compareTo(o2.getCreatedDate());
            return reverse ? result * -1 : result;
        }
    }


    private static class DateComparator implements Comparator<BookModel> {

        private boolean reverse;

        public DateComparator(boolean reverse) {
            this.reverse = reverse;
        }

        public int compare(BookModel o1, BookModel o2) {
            if (o1.getReadDateTime() == null || o2.getReadDateTime() == null) {
                return o2.getReadDateTime() != null ? (reverse ? 1 : -1) : (o1.getReadDateTime() != null ? (reverse ? -1 : 1) : 0);
            }
            int result = o1.getReadDateTime().compareTo(o2.getReadDateTime());
            return reverse ? result * -1 : result;
        }
    }
}
