package com.cesarferreira.rxpaper;

import android.content.Context;
import android.text.TextUtils;

import com.cesarferreira.rxpaper.exceptions.UnableToPerformOperationException;

import java.util.List;

import io.paperdb.Paper;
import rx.Observable;
import rx.Subscriber;

public class RxPaper {

    private Context mContext;
    private static RxPaper mRxPaper;
    private static String mCustomBook;


    public synchronized static RxPaper with(Context context) {
        return init(context, "");
    }

    public synchronized static RxPaper with(Context context, String customBook) {
        return init(context, customBook);
    }

    private synchronized static RxPaper init(Context context, String customBook) {
        mRxPaper = new RxPaper(context, customBook);
        Paper.init(context);
        return mRxPaper;
    }

    private RxPaper(Context context, String customBook) {
        this.mContext = context;
        mCustomBook = customBook;

    }

    private static boolean hasBook() {
        return !TextUtils.isEmpty(mCustomBook);
    }


    /**
     * Saves any types of POJOs or collections in Book storage.
     *
     * @param key   object key is used as part of object's file name
     * @param value object to save, must have no-arg constructor, can't be null.
     * @return this Book instance
     */
    public <T> Observable<Boolean> write(final String key, final T value) {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {

                if (!subscriber.isUnsubscribed()) {
                    try {
                        if (hasBook()) {
                            Paper.book(mCustomBook).write(key, value);
                        } else {
                            Paper.book().write(key, value);
                        }

                        subscriber.onNext(true);
                    } catch (Exception e) {
                        subscriber.onError(new UnableToPerformOperationException("Can't write"));
                    }
                    subscriber.onCompleted();

                }
            }
        });


    }

    /**
     * Instantiates saved object using original object class (e.g. LinkedList). Support limited
     * backward and forward compatibility: removed fields are ignored, new fields have their
     * default values.
     * <p/>
     * All instantiated objects must have no-arg constructors.
     *
     * @param key          object key to read
     * @param defaultValue will be returned if key doesn't exist
     * @return the saved object instance observable or null
     */
    public <T> Observable<T> read(final String key, final T defaultValue) {

        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {

                if (!subscriber.isUnsubscribed()) {

                    T value;

                    if (hasBook()) {
                        value = Paper.book(mCustomBook).read(key, defaultValue);
                    } else {
                        value = Paper.book().read(key, defaultValue);
                    }

                    if (value == null) {
                        subscriber.onError(new UnableToPerformOperationException(key + " is empty"));
                    } else {
                        subscriber.onNext(value);
                    }
                    subscriber.onCompleted();

                }
            }
        });
    }

    /**
     * Instantiates saved object using original object class (e.g. LinkedList). Support limited
     * backward and forward compatibility: removed fields are ignored, new fields have their
     * default values.
     * <p/>
     * All instantiated objects must have no-arg constructors.
     *
     * @param key object key to read
     * @return an Observable with the saved object instance or null
     */
    public <T> Observable<T> read(final String key) {

        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {

                if (!subscriber.isUnsubscribed()) {

                    T value;

                    if (hasBook()) {
                        value = Paper.book(mCustomBook).read(key);
                    } else {
                        value = Paper.book().read(key);
                    }

                    if (value == null) {
                        subscriber.onError(new UnableToPerformOperationException(key + " is empty"));
                    } else {
                        subscriber.onNext(value);
                    }

                    subscriber.onCompleted();

                }
            }
        });


    }


    /**
     * Delete saved object for given key if it is exist.
     *
     * @param key object key
     */
    public Observable<Boolean> delete(final String key) {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {

                if (!subscriber.isUnsubscribed()) {

                    try {
                        if (hasBook()) {
                            Paper.book(mCustomBook).delete(key);
                        } else {
                            Paper.book().delete(key);
                        }
                        subscriber.onNext(true);
                    } catch (Exception e) {
                        subscriber.onError(new UnableToPerformOperationException("Can't delete"));
                    }

                    subscriber.onCompleted();
                }
            }
        });


    }

    /**
     * Check if given key exist.
     *
     * @param key object key
     */
    public Observable<Boolean> exists(final String key) {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {

                if (!subscriber.isUnsubscribed()) {

                    try {
                        boolean exists;
                        if (hasBook()) {
                            exists = Paper.book(mCustomBook).exist(key);
                        } else {
                            exists = Paper.book().exist(key);
                        }
                        subscriber.onNext(exists);
                    } catch (Exception e) {
                        subscriber.onError(new UnableToPerformOperationException("Can't check if key exists"));
                    }

                    subscriber.onCompleted();
                }
            }
        });
    }

    public Observable<List<String>> getAllKeys() {

        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {

                if (!subscriber.isUnsubscribed()) {

                    try {
                        List<String> keys;
                        if (hasBook()) {
                            keys = Paper.book(mCustomBook).getAllKeys();
                        } else {
                            keys = Paper.book().getAllKeys();
                        }
                        if (keys.size() == 0) {
                            subscriber.onError(new UnableToPerformOperationException("No keys for this book"));
                        } else {
                            subscriber.onNext(keys);
                        }

                    } catch (Exception e) {
                        subscriber.onError(new UnableToPerformOperationException("Can't collect all keys"));
                    }

                    subscriber.onCompleted();
                }
            }
        });
    }

    public Observable<Boolean> destroy() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    try {
                        if (hasBook()) {
                            Paper.book(RxPaper.mCustomBook).destroy();
                        } else {
                            Paper.book().destroy();
                        }
                        subscriber.onNext(true);
                    } catch (Exception e) {
                        subscriber.onError(new UnableToPerformOperationException("Can't destroy"));
                    }
                    subscriber.onCompleted();

                }
            }
        });
    }
}
