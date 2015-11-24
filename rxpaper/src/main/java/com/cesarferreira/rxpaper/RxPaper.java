package com.cesarferreira.rxpaper;

import android.content.Context;
import android.text.TextUtils;

import io.paperdb.Paper;
import rx.Observable;

public class RxPaper {

    private Context mContext;
    private static RxPaper mRxPeople;
    private static String mCustomBook;


    public static RxPaper with(Context context) {
        return init(context, "");
    }

    public static RxPaper with(Context context, String customBook) {
        return init(context, customBook);
    }

    private static RxPaper init(Context context, String customBook) {
        mRxPeople = new RxPaper(context);
        mCustomBook = customBook;
        Paper.init(context);
        return mRxPeople;
    }

    private RxPaper(Context context) {
        mContext = context;
    }

    private static boolean hasBook() {
        return !TextUtils.isEmpty(mCustomBook);
    }

    public <T> Observable<Boolean> write(String key, T value) {

        try {
            if (hasBook()) {
                Paper.book(mCustomBook).write(key, value);
            } else {
                Paper.book().write(key, value);
            }

            return Observable.just(true);
        } catch (Exception e) {
            return Observable.just(false);
        }
    }

    public <T> Observable<T> read(String key, T defaultValue) {

        T value;

        if (hasBook()) {
            value = Paper.book(mCustomBook).read(key, defaultValue);
        } else {
            value = Paper.book().read(key, defaultValue);
        }

        return Observable.just(value);
    }

    public <T> Observable<T> read(String key) {
        T value;

        if (hasBook()) {
            value = Paper.book(mCustomBook).read(key);
        } else {
            value = Paper.book().read(key);
        }

        return Observable.just(value);
    }

    public Observable<Boolean> delete(String key) {

        try {
            if (hasBook()) {
                Paper.book(mCustomBook).delete(key);
            } else {
                Paper.book().delete(key);
            }
            return Observable.just(true);
        } catch (Exception e) {
            return Observable.just(false);
        }
    }

    public Observable<Boolean> destroy() {

        try {
            if (hasBook()) {
                Paper.book(mCustomBook).destroy();
            } else {
                Paper.book().destroy();
            }

            return Observable.just(true);
        } catch (Exception e) {
            return Observable.just(false);
        }
    }
}
