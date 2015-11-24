package com.cesarferreira.rxpaper;

import android.content.Context;

import io.paperdb.Paper;
import rx.Observable;

public class RxPaper {

    private Context mContext;
    private static RxPaper mRxPeople;


    public static RxPaper with(Context context) {
        mRxPeople = new RxPaper(context);
        Paper.init(context);
        return mRxPeople;
    }

    private RxPaper(Context context) {
        mContext = context;
    }


    public <T> Observable<Boolean> write(String key, T value) {

        try {
            Paper.book().write(key, value);
            return Observable.just(true);
        } catch (Exception e) {
            return Observable.just(false);
        }
    }

    public <T> Observable<T> read(String key, T defaultValue) {
        return Observable.just(Paper.book().read(key, defaultValue));
    }

    public <T> Observable<T> read(String key) {
        T anObject = Paper.book().read(key);
        return Observable.just(anObject);
    }

    public <T> Observable<Boolean> delete(String key) {

        try {
            Paper.book().delete(key);
            return Observable.just(true);
        } catch (Exception e) {
            return Observable.just(false);
        }
    }

    public Observable<Boolean> destroy() {

        try {
            Paper.book().destroy();
            return Observable.just(true);
        } catch (Exception e) {
            return Observable.just(false);
        }
    }
}
