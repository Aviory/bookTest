package com.getbooks.android.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.getbooks.android.Const;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.model.Book;
import com.getbooks.android.model.PurchasedBook;
import com.getbooks.android.model.RentedBook;
import com.getbooks.android.model.UserSession;
import com.getbooks.android.model.enums.BookState;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.activities.AuthorizationActivity;
import com.getbooks.android.util.LogUtil;
import com.getbooks.android.util.UiUtil;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
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

        void onCompleted(List<Book> library);

        void onFinish();
    }

    public void getAllUserBook(String deviceToken, Context context, int userId) {
        mCompositeSubscription = new CompositeSubscription();

        ApiService apiService = ApiManager.getClientApiAry().create(ApiService.class);

        Subscription subscriptions = Observable.zip(
                apiService.getAllRentedBooks(Const.WEBSITECODE, deviceToken),
                apiService.getAllPurchasedBooks(Const.WEBSITECODE, deviceToken),
                (listRentedResponse, listPurchasedResponse) -> {
                    List<RentedBook> rentedBooks = new ArrayList<RentedBook>();
                    List<PurchasedBook> purchasedBooks = new ArrayList<PurchasedBook>();
                    List<Book> allLibraryBooks = new ArrayList<Book>();
                    if (listRentedResponse.code() == 200) {
                        rentedBooks.addAll(listRentedResponse.body());
                        for (RentedBook rentedBook : rentedBooks) {
                            Book book = new Book();
                            book.setUserId(userId);
                            book.setBookName(rentedBook.getRentBookName().replaceAll("\\P{L}", ""));
                            book.setImageDownloadLink(rentedBook.getRentBookImage());
                            book.setBookDownloadLink(rentedBook.getRentBookDownloadLink());
                            book.setBookSku(rentedBook.getRentBookSku());
                            book.setBookState(BookState.CLOUD_BOOK.getState());
                            book.setIsBookRented(true);
                            allLibraryBooks.add(book);

                        }
                    } else if (listRentedResponse.code() == 404) {
//                        UiUtil.showToast(context, R.string.emty_rented_list);
                    }

                    if (listPurchasedResponse.code() == 200) {
                        purchasedBooks.addAll(listPurchasedResponse.body());
                        for (PurchasedBook purchasedBook : purchasedBooks) {
                            Book book = new Book();
                            book.setUserId(userId);
                            book.setBookName(purchasedBook.getPurchasedBookName().replaceAll("\\P{L}", ""));
                            book.setImageDownloadLink(purchasedBook.getPurchasedBookImage());
                            book.setBookDownloadLink(purchasedBook.getPurchasedBookDownloadLink());
                            book.setBookSku(purchasedBook.getPurchasedBookSku());
                            book.setBookState(BookState.CLOUD_BOOK.getState());
                            book.setIsBookRented(false);
                            allLibraryBooks.add(book);
                        }
                    } else if (listPurchasedResponse.code() == 404) {
//                        UiUtil.showToast(context, R.string.empty_purchased_list);
                    }

                    List<Book> library = new ArrayList<Book>();
                    library.addAll(checkDownloadedBook(allLibraryBooks, userId, context));
                    return library;
                })
                .doOnUnsubscribe(() -> {
                    LogUtil.log(this, "OnUnsubscribe");

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Book>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.log(this, "OnCompleted");
                        if (mCallBack != null) {
                            mCallBack.onFinish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof SocketException) {
                            Toast.makeText(context,
                                    "HTTP FAILED: java.net.SocketException: Socket closed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        LogUtil.log(this, e.getMessage());
                        e.printStackTrace();
                        if (mCallBack != null) {
                            mCallBack.onError(e);
                        }
                    }

                    @Override
                    public void onNext(List<Book> library) {
                        if (mCallBack != null) {
                            mCallBack.onCompleted(library);
                        }
                    }
                });
        mCompositeSubscription.add(subscriptions);
    }


    public void getUserSession(String deviseToken, Context context) {
        ApiService apiService = ApiManager.getClientApiAry().create(ApiService.class);

        apiService.detUserSession(Const.WEBSITECODE, deviseToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Response<UserSession>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getAllUserBook(deviseToken, context, Prefs.getUserSession(context, Const.USER_SESSION_ID));
                    }

                    @Override
                    public void onNext(Response<UserSession> userSessionResponse) {
                        if (userSessionResponse.isSuccessful()) {
                            UserSession userSession = userSessionResponse.body();
                            Prefs.saveUserSession(context, Const.USER_SESSION_ID, userSession.getCustomerId());
                            LogUtil.log(this, "Save User session id");
                            getAllUserBook(deviseToken, context, Prefs.getUserSession(context, Const.USER_SESSION_ID));
                        }
                    }
                });
    }


    public void deleteUserSession(String deviceToken, Activity context) {
        ApiService apiService = ApiManager.getClientApiAry().create(ApiService.class);

        apiService.deleteDeviseSession(Const.WEBSITECODE, deviceToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnNext(responseBodyResponse -> {
                    if (responseBodyResponse.code() == 204) {
                        Prefs.clearPrefs(context);
                        UiUtil.openActivity(context, AuthorizationActivity.class, true, "", "", "", "");
                    }
                }).subscribe();
    }

    private List<Book> checkDownloadedBook(List<Book> allBooks, int userId, Context context) {
        List<Book> allBooksLibrary = new ArrayList<>();
        allBooksLibrary.addAll(BookDataBaseLoader.createBookDBLoader(context).getAllUserBookOnDevise(userId));
        Log.d("QQQQQ-", String.valueOf(userId));
        Log.d("QQQQQ-", allBooksLibrary.toString());
        Log.d("QQQQQ-", String.valueOf(allBooksLibrary.size()));
        if (!allBooksLibrary.isEmpty()) {
            for (Book book : allBooks) {
                if (!allBooksLibrary.contains(book)) {
                    allBooksLibrary.add(book);
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
