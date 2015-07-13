package com.mobileobservinglog;

import android.os.Bundle;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by joe on 5/31/15.
 */
public class ObservingLogActivity extends ObservingLogActivityParent {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);
    }
}
