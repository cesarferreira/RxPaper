package com.cesarferreira.rxpaper.sample;

import android.app.Application;
import com.cesarferreira.rxpaper.RxPaper;

public class ApplicationSample extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		RxPaper.init(getApplicationContext());
	}
}
