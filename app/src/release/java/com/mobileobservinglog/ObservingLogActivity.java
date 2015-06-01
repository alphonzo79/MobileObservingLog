package com.mobileobservinglog;

import android.os.Bundle;

import com.crashlytics.android.Crashlytics;

/**
 * Created by joe on 5/31/15.
 */
public class ObservingLogActivity extends ObservingLogActivityParent {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Crashlytics.start(this);
        super.onCreate(savedInstanceState);
    }
}
