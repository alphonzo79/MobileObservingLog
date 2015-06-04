package com.mobileobservinglog.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by joe on 6/3/15.
 */
public class ChartDownloadService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ChartDownloadService(String name) {
        super(name);
    }

    public ChartDownloadService() {
        this("ChartDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //todo
    }
}
