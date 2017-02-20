package com.cesarferreira.rxpaper;

import android.content.Context;
import com.cesarferreira.rxpaper.exceptions.UnableToPerformOperationException;
import io.paperdb.Book;
import io.paperdb.Paper;
import java.util.List;
import rx.Observable;
import rx.Subscriber;

public class RxPaper {

	private Book mBook;

	public static void init(Context context) {
		Paper.init(context);
	}

	/**
	 * Uses Paper's default book to create the observable operations
	 *
	 * @return instance of {@link RxPaper}
	 */
	public static RxPaper book() {
		return book(null);
	}

	/**
	 * Uses Paper's with a custom book to create the observable operations
	 *
	 * @return instance of {@link RxPaper}
	 */
	public static RxPaper book(String customBook) {
		return new RxPaper(customBook);
	}

	private RxPaper(String customBook) {
		mBook = (customBook != null) ? Paper.book(customBook) : Paper.book();
	}

	/**
	 * Gets the paper book used for the rx operations
	 *
	 * @return paper book {@link Book}
	 */
	public Book getBook() {
		return mBook;
	}

	/**
	 * Saves any types of POJOs or collections in Book storage.
	 *
	 * @param key object key is used as part of object's file name
	 * @param value object to save, must have no-arg constructor, can't be null.
	 * @return this Book instance
	 */
	public <T> Observable<T> write(final String key, final T value) {

		return Observable.create(new Observable.OnSubscribe<T>() {
			@Override
			public void call(Subscriber<? super T> subscriber) {

				if (!subscriber.isUnsubscribed()) {
					try {
						mBook.write(key, value);
						subscriber.onNext(value);
					} catch (Exception e) {
						subscriber.onError(new UnableToPerformOperationException("Can't write: " + e.getMessage()));
					}
					subscriber.onCompleted();

				}
			}
		});

	}

	/**
	 * Instantiates saved object using original object class (e.g. LinkedList). Support limited
	 * backward and forward compatibility: removed fields are ignored, new fields have their default
	 * values. <p> All instantiated objects must have no-arg constructors.
	 *
	 * @param key object key to read
	 * @param defaultValue will be returned if key doesn't exist
	 * @return the saved object instance observable or null
	 */
	public <T> Observable<T> read(final String key, final T defaultValue) {

		return Observable.create(new Observable.OnSubscribe<T>() {
			@Override
			public void call(Subscriber<? super T> subscriber) {

				if (!subscriber.isUnsubscribed()) {
					try {
						subscriber.onNext(mBook.read(key, defaultValue));
					} catch (Exception e) {
						subscriber.onError(new UnableToPerformOperationException("Error while reading the key" +
							key + ", details: " + e.getMessage()));
					}
					subscriber.onCompleted();

				}
			}
		});
	}

	/**
	 * Instantiates saved object using original object class (e.g. LinkedList). Support limited
	 * backward and forward compatibility: removed fields are ignored, new fields have their default
	 * values. <p> All instantiated objects must have no-arg constructors.
	 *
	 * @param key object key to read
	 * @return an Observable with the saved object instance or null
	 */
	public <T> Observable<T> read(final String key) {

		return Observable.create(new Observable.OnSubscribe<T>() {
			@Override
			public void call(Subscriber<? super T> subscriber) {
				if (!subscriber.isUnsubscribed()) {
					try {
						T value = mBook.read(key);
						//Eval this, maybe not error
						//if (value == null) {
						//    subscriber.onError(new AndroidException(key + " is empty"));
						//} else {
						subscriber.onNext(value);
						//}
					} catch (Exception e) {
						subscriber.onError(e);
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
	public <T> Observable<T> delete(final String key) {

		return Observable.create(new Observable.OnSubscribe<T>() {
			@Override
			public void call(Subscriber<? super T> subscriber) {

				if (!subscriber.isUnsubscribed()) {
					try {
						T value = mBook.read(key);
						mBook.delete(key);
						subscriber.onNext(value);
					} catch (Exception e) {
						subscriber.onError(e);
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
						subscriber.onNext(mBook.exist(key));
					} catch (Exception e) {
						subscriber.onError(e);
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
						subscriber.onNext(mBook.getAllKeys());
					} catch (Exception e) {
						subscriber.onError(e);
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
						mBook.destroy();
						subscriber.onNext(true);
					} catch (Exception e) {
						subscriber.onError(e);
					}
					subscriber.onCompleted();

				}
			}
		});
	}

}