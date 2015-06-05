package com.mobileobservinglog.support.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joe on 6/3/15.
 */
public class ScheduledDownloadsDao extends DatabaseHelper {
    private final String TABLE_NAME = "scheduledDownloads";
    private final String PATH_COLUMN_NAME = "downloadPath";

    public ScheduledDownloadsDao(Context context) {
        super(context);
    }

    public void scheduleChartsToDownload(List<String> downloadPaths) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        ContentValues cv = new ContentValues(1);
        try {
            for(String path : downloadPaths) {
                cv.put(PATH_COLUMN_NAME, path);
                db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            }
            db.setTransactionSuccessful();
        } catch(SQLiteException e) {
            e.printStackTrace();
        }

        db.endTransaction();
    }

    public void cancelChartToDownload(String downloadPath) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            db.delete(TABLE_NAME, PATH_COLUMN_NAME + "=?", new String[]{downloadPath});
            db.setTransactionSuccessful();
        } catch(SQLiteException e) {
            e.printStackTrace();
        }

        db.endTransaction();
    }

    public List<String> getScheduledDownloads() {
        List<String> result = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{PATH_COLUMN_NAME}, PATH_COLUMN_NAME + " IS NOT NULL", null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                result.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }

        if(cursor != null) {
            cursor.close();
        }

        return result;
    }

    public int getScheduledDownloadCount() {
        int result = 0;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {"count(*)"}, PATH_COLUMN_NAME + " IS NOT NULL", null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }

        if(cursor != null) {
            cursor.close();
        }
        return result;
    }
}
