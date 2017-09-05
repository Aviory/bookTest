package com.getbooks.android.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.getbooks.android.Const;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.model.BookModel;
import com.getbooks.android.model.PurchasedBook;
import com.getbooks.android.model.RentedBook;
import com.getbooks.android.model.UserSession;
import com.getbooks.android.model.enums.BookState;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.activities.AuthorizationActivity;
import com.getbooks.android.ui.adapter.RecyclerShelvesAdapter;
import com.getbooks.android.util.LogUtil;
import com.getbooks.android.util.UiUtil;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
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

        void onCompleted(List<BookModel> library);

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
                    List<BookModel> allLibraryBookModels = new ArrayList<BookModel>();
                    if (listRentedResponse.code() == 200) {
                        rentedBooks.addAll(listRentedResponse.body());
                        for (RentedBook rentedBook : rentedBooks) {
                            BookModel bookModel = new BookModel();
                            bookModel.setUserId(userId);
                            bookModel.url = rentedBook.getRentBookDownloadLink();
                            bookModel.coverUrl = rentedBook.getRentBookImage();
                            bookModel.fileName = rentedBook.getRentBookName().replaceAll("\\P{L}", "");
//                            bookModel.setBookName(rentedBook.getRentBookName().replaceAll("\\P{L}", ""));
//                            bookModel.setImageDownloadLink(rentedBook.getRentBookImage());
//                            bookModel.setBookDownloadLink(rentedBook.getRentBookDownloadLink());
                            bookModel.setBookSku(rentedBook.getRentBookSku());
                            bookModel.setBookState(BookState.CLOUD_BOOK.getState());
                            bookModel.setIsBookRented(true);
                            allLibraryBookModels.add(bookModel);

                        }
                        Log.d("PPPPPPPPPPP-Rented", String.valueOf(rentedBooks.size()));
                    } else if (listRentedResponse.code() == 404) {
//                        UiUtil.showToast(context, R.string.emty_rented_list);
                    }

                    if (listPurchasedResponse.code() == 200) {
                        purchasedBooks.addAll(listPurchasedResponse.body());
                        for (PurchasedBook purchasedBook : purchasedBooks) {
                            BookModel bookModel = new BookModel();
                            bookModel.setUserId(userId);
                            bookModel.fileName = purchasedBook.getPurchasedBookName().replaceAll("\\P{L}", "");
//                            bookModel.setBookName(purchasedBook.getPurchasedBookName().replaceAll("\\P{L}", ""));
//                            bookModel.setImageDownloadLink(purchasedBook.getPurchasedBookImage());
                            bookModel.url = purchasedBook.getPurchasedBookDownloadLink();
                            bookModel.coverUrl = purchasedBook.getPurchasedBookImage();
//                            bookModel.setBookDownloadLink(purchasedBook.getPurchasedBookDownloadLink());
                            bookModel.setBookSku(purchasedBook.getPurchasedBookSku());
                            bookModel.setBookState(BookState.CLOUD_BOOK.getState());
                            bookModel.setIsBookRented(false);
                            allLibraryBookModels.add(bookModel);
                        }
                        Log.d("PPPPPPPPPPP-Purchased", String.valueOf(purchasedBooks.size()));
                    } else if (listPurchasedResponse.code() == 404) {
//                        UiUtil.showToast(context, R.string.empty_purchased_list);
                    }

                    List<BookModel> library = new ArrayList<BookModel>();
                    library.addAll(checkDownloadedBook(allLibraryBookModels, userId, context));
                    return library;
                })
//                .doOnUnsubscribe(() -> {
//                    LogUtil.log(this, "OnUnsubscribe");
//
//                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<BookModel>>() {
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
                    public void onNext(List<BookModel> library) {
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

    public void returnRentedBook(String deviceToken, Activity context, BookModel bookModel,
                                 List<BookModel> library, RecyclerShelvesAdapter shelvesAdapter) {
        ApiService apiService = ApiManager.getClientApiAry().create(ApiService.class);

        apiService.returnBookRented(Const.WEBSITECODE, deviceToken, String.valueOf(bookModel.bookCode))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnNext(responseBodyResponse -> {
                    if (responseBodyResponse.code() == 204) {
                        BookDataBaseLoader.getInstanceDb(context).deleteBookFromDb(bookModel);
                        library.remove(bookModel);
                        shelvesAdapter.upDateLibrary(library);
                    }
                }).subscribe();
    }

    private List<BookModel> checkDownloadedBook(List<BookModel> allBookModels, int userId, Context context) {
        List<BookModel> allBooksLibrary = new ArrayList<>();
        allBooksLibrary.addAll(BookDataBaseLoader.getInstanceDb(context).getAllUserBookOnDevise(userId));
        Log.d("QQQQQ-", String.valueOf(userId));
        Log.d("QQQQQ-", allBooksLibrary.toString());
        Log.d("QQQQQ-", String.valueOf(allBooksLibrary.size()));
        if (!allBooksLibrary.isEmpty()) {
            for (BookModel bookModel : allBookModels) {
                if (!allBooksLibrary.contains(bookModel)) {
                    allBooksLibrary.add(bookModel);
                }
            }
        } else {
            allBooksLibrary.addAll(allBookModels);
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
