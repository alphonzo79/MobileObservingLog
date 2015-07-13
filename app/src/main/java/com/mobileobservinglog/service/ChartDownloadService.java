package com.mobileobservinglog.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.mobileobservinglog.AvailableCatalogsTab;
import com.mobileobservinglog.support.SettingsContainer;
import com.mobileobservinglog.support.database.ScheduledDownloadsDao;
import com.mobileobservinglog.support.database.SettingsDAO;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        //Download the images
        //check the settings table for the files location
        SettingsContainer settingsRef = SettingsContainer.getSettingsContainer();
        String fileLocationString = settingsRef.getPersistentSetting(settingsRef.STAR_CHART_DIRECTORY, ChartDownloadService.this);
        Log.d("JoeTest", "FileLocationString is " + fileLocationString);
        File starChartRoot = null;
        SettingsDAO settingsDb = new SettingsDAO(ChartDownloadService.this);

        //if it's not set yet, then first look for an external storage card and establish the file location
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        //First, check whether we have established a file location already. If not then do so, with the preference going to the external system (for size)
        if (fileLocationString.equals("NULL")){
            //If the external card is not available, then establish a file location on the internal file system
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // We can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;
                fileLocationString = SettingsContainer.EXTERNAL;
            } else{
                fileLocationString = SettingsContainer.INTERNAL;
            }

            //If we just established the file location, store it in the database
            settingsDb.setPersistentSetting(SettingsContainer.STAR_CHART_DIRECTORY, fileLocationString);
        }

        //Now actually get the file location
        if (fileLocationString.equals(SettingsContainer.EXTERNAL)){
            starChartRoot = getExternalFilesDir(null);
        }
        else{
            starChartRoot = getFilesDir();
        }

        addNoMediaFile(starChartRoot);

        //Get a list of the file paths we need to fetch/save
        ScheduledDownloadsDao dao = new ScheduledDownloadsDao(this);
        List<String> filePaths = dao.getScheduledDownloads();

        //Establish connection and download the files
        Set<String> knownDirectories = new HashSet<>();
        for (String path : filePaths){

            File file = new File(starChartRoot + path);

            //If the file already exists (maybe from a previous, unsuccessful attempt to install the images) then we will skip this image and move to the next
            if (path != null && !path.equals("NULL") && !file.exists()){
                if(!knownDirectories.contains(extractDirectoryPath(path))) {
                    knownDirectories.add(createDirectoryStructure(starChartRoot.toString(), path));
                }

                try{
                    //setup input streams
                    URL imageFile = new URL(SettingsContainer.IMAGE_DOWNLOAD_ROOT + path);
                    URLConnection ucon = imageFile.openConnection();
                    InputStream is = ucon.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);

                    //Read in the remote file until we hit a -1 bit
                    ByteArrayBuffer buf = new ByteArrayBuffer(50);
                    int current = 0;
                    while ((current = bis.read()) != -1){
                        buf.append((byte) current);
                    }

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(buf.toByteArray());
                    fos.close();
                    dao.cancelChartToDownload(path);
                } catch(IOException e){
                    //delete the file if it exists in case it is corrupt. Set success to false so we can display an error later
                    if (file.exists()){
                        file.delete();
                    }
                }
            }
        }
    }

    private String createDirectoryStructure(String root, String filepath) {
        //Cut the filename off the end of the path
        String directoryPath = extractDirectoryPath(filepath);
        File directoryBuilder = new File(root + directoryPath);
        directoryBuilder.mkdirs();
        return directoryPath;
    }

    private String extractDirectoryPath(String filePath) {
        int index = filePath.lastIndexOf("/");
        return filePath.subSequence(0, index).toString();
    }

    private void addNoMediaFile(File rootDirectory){
        try{
            File noMedia = new File(rootDirectory + ".nomedia");
            FileOutputStream fos = new FileOutputStream(noMedia);
            fos.write(1);
            fos.close();
        }
        catch(IOException e){
            //delete the file if it exists in case it is corrupt. Set success to false so we can display an error later
            e.printStackTrace();
        }
    }
}
