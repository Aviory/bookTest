package com.getbooks.android.api;

import com.getbooks.android.model.Book;
import com.getbooks.android.model.Library;
import com.getbooks.android.model.PurchasedBook;
import com.getbooks.android.model.RentedBook;
import com.getbooks.android.model.enums.BookState;
import com.getbooks.android.util.LogUtil;

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

    public void getAllRentedBook(String deviceToken) {
        mCompositeSubscription = new CompositeSubscription();

        ApiService apiService = ApiManager.getClientApiAry().create(ApiService.class);
        Subscription subscriptions = Observable.zip(
                apiService.getAllRentedBooks("aff_pelephone", deviceToken),
                apiService.getAllPurchasedBooks("aff_pelephone", deviceToken),
                (book, purchasedBook) -> {
                    Library library = new Library();
                    List<PurchasedBook> purchasedBooks = purchasedBook;
                    for (PurchasedBook bookPurchased : purchasedBooks){
                        bookPurchased.setBookState(BookState.DOWNLOAD);
                    }
                    List<RentedBook> rentedBooks = book;
                    for (RentedBook rentedBook : book){
                        rentedBook.setBookState(BookState.DOWNLOAD);
                    }
                    List<Book> allBook = new ArrayList<Book>();
                    allBook.addAll(rentedBooks);
                    allBook.addAll(purchasedBooks);
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
