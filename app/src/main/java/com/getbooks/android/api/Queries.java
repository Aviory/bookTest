package com.getbooks.android.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.getbooks.android.Const;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.model.BookDetail;
import com.getbooks.android.model.PurchasedBook;
import com.getbooks.android.model.RentedBook;
import com.getbooks.android.model.enums.BookState;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.servises.DownloadService;
import com.getbooks.android.ui.activities.AuthorizationActivity;
import com.getbooks.android.util.LogUtil;
import com.getbooks.android.util.UiUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by marina on 25.07.17.
 */

public class Queries {

    private CompositeSubscription mCompositeSubscription;
    private CallBack mCallBack;

    public interface CallBack {
        void onError(Throwable throwable);

        void onCompleted(List<BookDetail> library);

        void onFinish();
    }

    public void getAllUserBook(String deviceToken, Context context, int userId) {
        mCompositeSubscription = new CompositeSubscription();

        ApiService apiService = ApiManager.getClientApiAry().create(ApiService.class);

        Subscription subscriptions = Observable.zip(
                apiService.getAllRentedBooks("aff_pelephone", deviceToken),
                apiService.getAllPurchasedBooks("aff_pelephone", deviceToken),
                (listRentedResponse, listPurchasedResponse) -> {
                    List<RentedBook> rentedBooks = new ArrayList<RentedBook>();
                    List<PurchasedBook> purchasedBooks = new ArrayList<PurchasedBook>();
                    List<BookDetail> allLibraryBooks = new ArrayList<BookDetail>();
                    if (listRentedResponse.code() == 200) {
                        rentedBooks.addAll(listRentedResponse.body());
                        for (RentedBook rentedBook : rentedBooks) {
                            BookDetail bookDetail = new BookDetail();
                            bookDetail.setUserId(userId);
                            bookDetail.setBookName(rentedBook.getRentBookName());
                            bookDetail.setImageDownloadLink(rentedBook.getRentBookImage());
                            bookDetail.setBookDownloadLink(rentedBook.getRentBookDownloadLink());
                            bookDetail.setBookSku(rentedBook.getRentBookSku());
                            bookDetail.setBookState(BookState.CLOUD_BOOK.getState());
                            bookDetail.setIsBookRented(true);
                            allLibraryBooks.add(bookDetail);

                        }
                    } else if (listRentedResponse.code() == 404) {
//                        UiUtil.showToast(context, R.string.emty_rented_list);
                    }

                    if (listPurchasedResponse.code() == 200) {
                        purchasedBooks.addAll(listPurchasedResponse.body());
                        for (PurchasedBook purchasedBook : purchasedBooks) {
                            BookDetail bookDetail = new BookDetail();
                            bookDetail.setUserId(userId);
                            bookDetail.setBookName(purchasedBook.getPurchasedBookName());
                            bookDetail.setImageDownloadLink(purchasedBook.getPurchasedBookImage());
                            bookDetail.setBookDownloadLink(purchasedBook.getPurchasedBookDownloadLink());
                            bookDetail.setBookSku(purchasedBook.getPurchasedBookSku());
                            bookDetail.setBookState(BookState.CLOUD_BOOK.getState());
                            bookDetail.setIsBookRented(false);
                            allLibraryBooks.add(bookDetail);
                        }
                    } else if (listPurchasedResponse.code() == 404) {
//                        UiUtil.showToast(context, R.string.empty_purchased_list);
                    }

                    List<BookDetail> library = new ArrayList<BookDetail>();
                    library.addAll(checkDownloadedBook(allLibraryBooks, userId, context));
                    return library;
                })
                .doOnUnsubscribe(() -> {
                    LogUtil.log(this, "OnUnsubscribe");

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<BookDetail>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.log(this, "OnCompleted");
                        if (mCallBack != null) {
                            mCallBack.onFinish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.log(this, e.getMessage());
                        e.printStackTrace();
                        if (mCallBack != null) {
                            mCallBack.onError(e);
                        }
                    }

                    @Override
                    public void onNext(List<BookDetail> library) {
                        if (mCallBack != null) {
                            mCallBack.onCompleted(library);
                        }
                    }
                });
        mCompositeSubscription.add(subscriptions);
    }

    public void deleteUserSession(String deviceToken, Activity context) {
        ApiService apiService = ApiManager.getClientApiAry().create(ApiService.class);

        apiService.deleteDeviseSession(Const.WEBSITECODE, deviceToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnNext(responseBodyResponse -> {
                    if (responseBodyResponse.code() == 204) {
                        Prefs.clearPrefs(context);
                        BookDataBaseLoader.createBookDBLoader(context).deleteUserSession(Prefs.getUserSession(context, Const.USER_SESSION_ID));
                        UiUtil.openActivity(context, AuthorizationActivity.class, true);
                    }
                }).subscribe();
    }

    private List<BookDetail> checkDownloadedBook(List<BookDetail> allBooks, int userId, Context context) {
        List<BookDetail> allBooksLibrary = new ArrayList<>();
        allBooksLibrary.addAll(BookDataBaseLoader.createBookDBLoader(context).getAllUserBookOnDevise(userId));
        Log.d("QQQQQ-", String.valueOf(userId));
        Log.d("QQQQQ-", allBooksLibrary.toString());
        Log.d("QQQQQ-", String.valueOf(allBooksLibrary.size()));
        if (!allBooksLibrary.isEmpty()) {
            for (BookDetail bookDetail : allBooks) {
                if (!allBooksLibrary.contains(bookDetail)) {
                    allBooksLibrary.add(bookDetail);
                }
            }
        } else {
            allBooksLibrary.addAll(allBooks);
        }
        return allBooksLibrary;
    }

    public void onStop() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
        mCallBack = null;
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }
}
