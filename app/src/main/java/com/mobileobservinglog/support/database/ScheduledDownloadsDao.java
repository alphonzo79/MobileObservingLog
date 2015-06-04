package com.mobileobservinglog.support.database;

import android.content.Context;

import java.util.List;

/**
 * Created by joe on 6/3/15.
 */
public class ScheduledDownloadsDao extends DatabaseHelper {
    public ScheduledDownloadsDao(Context context) {
        super(context);
    }

    public void scheduleChartsToDownload(List<String> downloadPaths) {
        //todo
    }

    public void cancelChartToDownload(String downloadPath) {
        //todo
    }

    public List<String> getScheduledDownloads() {
        //todo
        return null;
    }

    public int getScheduledDownloadCount() {
        //todo
        return 0;
    }
}
