package com.getbooks.android.util;

import com.getbooks.android.model.BookModel;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by avi on 30.08.17.
 */

public class CompareUtil {

    public static List<BookModel> compareByAuthorName(List<BookModel> list){
        LogUtil.log("compare: compareByAuthorName size: ", String.valueOf(list.size()));
        for (BookModel book: list ) {
            if(book==null)
                LogUtil.log("compare: ", "getBookAuthors book null");
            else if(book.getBookAuthors()!=null)
                LogUtil.log("compare: ", String.valueOf(book.getBookAuthors()));
            else
                LogUtil.log("compare: ", "getBookAuthors null");
        }

        Comparator<BookModel> comp = new AuthorNameComparator();
        TreeSet<BookModel> treeSetBookName = new TreeSet<BookModel>(comp);
        treeSetBookName.addAll(list);
        List<BookModel> newList = new LinkedList<BookModel>(treeSetBookName);
        LogUtil.log("compare: compareByAuthorName newList size: ", String.valueOf(list.size()));
        for (BookModel book: newList ) {
            if(book.getBookAuthors()!=null)
                LogUtil.log("compare newList: ", String.valueOf(book.getBookAuthors()));
            else
                LogUtil.log("compare newList: ", "getBookAuthors  null");
        }
        return newList;
    }

    public static List<BookModel> compareByBookName(List<BookModel> list){
        Comparator<BookModel> comp = new BookNameComparator();
        TreeSet<BookModel> treeSetBookName = new TreeSet<BookModel>(comp);
        treeSetBookName.addAll(list);
        List<BookModel> newList = new LinkedList<BookModel>(treeSetBookName);
        return newList;
    }

    public static List<BookModel> compareByReadDate(List<BookModel> list){
        for (BookModel book: list ) {
            if(book.getCreatedDate()!=null)
                LogUtil.log("compare: ", String.valueOf(book.getReadDateTime().getTime()));
            else
                LogUtil.log("compare: ", "create date null");
        }

        Comparator<BookModel> comp = new ReadDateComparator();
        TreeSet<BookModel> treeSetBookName = new TreeSet<BookModel>(comp);
        treeSetBookName.addAll(list);
        List<BookModel> newList = new LinkedList<BookModel>(treeSetBookName);

        for (BookModel book: newList ) {
            if(book.getCreatedDate()!=null)
                LogUtil.log("compare newList: ", String.valueOf(book.getReadDateTime().getTime()));
            else
                LogUtil.log("compare newList: ", "create date null");
        }
        return newList;
    }

    public static List<BookModel> compareByAddDate(List<BookModel> list){
        Comparator<BookModel> comp = new AddDateComparator();
        TreeSet<BookModel> treeSetBookName = new TreeSet<BookModel>(comp);
        treeSetBookName.addAll(list);
        List<BookModel> newList = new LinkedList<BookModel>(treeSetBookName);
        return newList;
    }

    private static class BookNameComparator implements Comparator<BookModel> {

        @Override
        public int compare(BookModel a, BookModel b) {
            if(a.fileName!=null && b.fileName!=null)
                return a.fileName.compareTo(b.fileName);

            else if(a.fileName==null && b.fileName!=null){
                return 1;
            } else{
                return -1;
            }
        }
    }
    private static class AuthorNameComparator implements Comparator<BookModel>{

        @Override
        public int compare(BookModel a, BookModel b) {
            if(a.getBookAuthors()!=null && b.getBookAuthors()!=null)
                return a.getBookAuthors().compareTo(b.getBookAuthors());

            else if(a.getBookAuthors()==null && b.getBookAuthors()!=null){
                return 1;
            } else{
                return -1;
            }
        }
    }
    private static class ReadDateComparator implements Comparator<BookModel>{

        @Override
        public int compare(BookModel a, BookModel b) {

            if(a.getReadDateTime()!=null && b.getReadDateTime()!=null){
                if(a.getReadDateTime().before(b.getReadDateTime()))
                    return -1;
                else if(a.getReadDateTime().after(b.getReadDateTime()))
                    return 1;
            }else if(a.getReadDateTime()==null && b.getReadDateTime()!=null){
               return 1;
            } else{
                return -1;
            }
            return 0;
        }
    }
    private static class AddDateComparator implements Comparator<BookModel>{

        @Override
        public int compare(BookModel a, BookModel b) {
            if(a.getCreatedDate()!=null && b.getCreatedDate()!=null){
                if(a.getCreatedDate().before(b.getCreatedDate()))
                    return -1;
                else if(a.getCreatedDate().after(b.getCreatedDate()))
                    return 1;
            }else if(a.getCreatedDate()==null && b.getCreatedDate()!=null){
                return 1;
            } else{
                return -1;
            }
            return 0;
        }
    }
}
