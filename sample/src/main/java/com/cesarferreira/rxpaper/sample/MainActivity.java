package com.cesarferreira.rxpaper.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.cesarferreira.rxpaper.RxPaper;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final Context ctx = getApplicationContext();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Person value = new Person("cesar", "ferreira");

                final String key = "key";

                RxPaper.with(ctx)
                        .write(key, value)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Boolean isSuccessfull) {
                                if (isSuccessfull) {
                                    log("SUCCESS WRITE!!!");


                                    Person defaultValue = null;

                                    RxPaper.with(ctx)
                                            .read(key, defaultValue)
                                            .subscribeOn(Schedulers.computation())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Subscriber<Person>() {
                                                @Override
                                                public void onCompleted() {

                                                }

                                                @Override
                                                public void onError(Throwable e) {

                                                }

                                                @Override
                                                public void onNext(Person person) {
                                                    log(String.valueOf(person));

                                                    Snackbar.make(view, String.valueOf(person), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();

                                                }
                                            });
                                }
                            }
                        });
            }
        });

    }

    private void log(String s) {
        Log.d("tag", s);
    }


}
