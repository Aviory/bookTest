package com.getbooks.android.api;

import android.app.Activity;
import android.content.Context;

import com.getbooks.android.Const;
import com.getbooks.android.model.Book;
import com.getbooks.android.model.Library;
import com.getbooks.android.model.PurchasedBook;
import com.getbooks.android.model.RentedBook;
import com.getbooks.android.model.enums.BookState;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.activities.AuthorizationActivity;
import com.getbooks.android.util.LogUtil;
import com.getbooks.android.util.UiUtil;

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

        void onCompleted(Library library);

        void onFinish();
    }

    public void getAllUserBook(String deviceToken, Context context) {
        mCompositeSubscription = new CompositeSubscription();

        ApiService apiService = ApiManager.getClientApiAry().create(ApiService.class);

        Subscription subscriptions = Observable.zip(
                apiService.getAllRentedBooks("aff_pelephone", deviceToken),
                apiService.getAllPurchasedBooks("aff_pelephone", deviceToken),
                (listRentedResponse, listPurchasedResponse) -> {
                    Library library = new Library();
                    List<Book> allBook = new ArrayList<Book>();
                    List<RentedBook> rentedBooks = new ArrayList<RentedBook>();
                    List<PurchasedBook> purchasedBooks = new ArrayList<PurchasedBook>();
                    if (listRentedResponse.code() == 200) {
                        rentedBooks.addAll(listRentedResponse.body());
                        for (RentedBook rentedBook : rentedBooks) {
                            rentedBook.setBookState(BookState.DOWNLOAD);
                        }
                        allBook.addAll(rentedBooks);
                    } else if (listRentedResponse.code() == 404) {
//                        UiUtil.showToast(context, R.string.emty_rented_list);
                    }

                    if (listPurchasedResponse.code() == 200) {
                        purchasedBooks.addAll(listPurchasedResponse.body());
                        for (PurchasedBook purchasedBook : purchasedBooks) {
                            purchasedBook.setBookState(BookState.DOWNLOAD);
                        }
                        allBook.addAll(purchasedBooks);
                    } else if (listPurchasedResponse.code() == 404) {
//                        UiUtil.showToast(context, R.string.empty_purchased_list);
                    }

                    library.setAllBook(allBook);
                    return library;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Library>() {
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
                    public void onNext(Library library) {
                        library.setSetTimeLoad(System.currentTimeMillis());
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
                        UiUtil.openActivity(context, AuthorizationActivity.class, true);
                    }
                }).subscribe();
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
