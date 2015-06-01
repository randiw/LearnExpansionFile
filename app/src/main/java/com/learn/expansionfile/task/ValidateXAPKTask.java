package com.learn.expansionfile.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.Helpers;
import com.learn.expansionfile.events.PostExecuteEvent;
import com.learn.expansionfile.events.PreExecuteEvent;
import com.learn.expansionfile.events.ProgressUpdateEvent;
import com.learn.expansionfile.helper.BusProvider;
import com.learn.expansionfile.helper.XAPKFile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

/**
 * Created by randiwaranugraha on 6/1/15.
 */
public class ValidateXAPKTask extends AsyncTask<Object, DownloadProgressInfo, Boolean> {

    public static final String TAG = ValidateXAPKTask.class.getSimpleName();
    private static final float SMOOTHING_FACTOR = 0.005f;

    private final Context context;
    private final XAPKFile[] xAPKS;

    private boolean cancelValidation;

    public ValidateXAPKTask(Context context, XAPKFile[] xAPKS) {
        this.context = context;
        this.xAPKS = xAPKS;
    }

    @Override
    protected void onPreExecute() {
        BusProvider.getInstance().post(new PreExecuteEvent());
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        for (XAPKFile xf : xAPKS) {
            String fileName = Helpers.getExpansionAPKFileName(context, xf.isMain, xf.fileVersion);
            if (!Helpers.doesFileExist(context, fileName, xf.fileSize, false)) {
                return false;
            }

            fileName = Helpers.generateSaveFileName(context, fileName);
            ZipResourceFile zrf;
            byte[] buf = new byte[1024 * 256];

            try {
                zrf = new ZipResourceFile(fileName);
                ZipResourceFile.ZipEntryRO[] entries = zrf.getAllEntries();

                long totalCompressedLength = 0;
                for (ZipResourceFile.ZipEntryRO entry : entries) {
                    totalCompressedLength += entry.mCompressedLength;
                }

                float averageVerifySpeed = 0;
                long totalBytesRemaining = totalCompressedLength;
                long timeRemaining;

                for (ZipResourceFile.ZipEntryRO entry : entries) {
                    if (-1 != entry.mCRC32) {
                        long length = entry.mUncompressedLength;
                        CRC32 crc = new CRC32();
                        DataInputStream dis = null;

                        try {
                            dis = new DataInputStream(zrf.getInputStream(entry.mFileName));

                            long startTime = SystemClock.uptimeMillis();
                            while (length > 0) {
                                int seek = (int) (length > buf.length ? buf.length : length);
                                dis.readFully(buf, 0, seek);
                                crc.update(buf, 0, seek);
                                length -= seek;
                                long currentTime = SystemClock.uptimeMillis();
                                long timePassed = currentTime - startTime;
                                if (timePassed > 0) {
                                    float currentSpeedSample = (float) seek / (float) timePassed;
                                    if (0 != averageVerifySpeed) {
                                        averageVerifySpeed = SMOOTHING_FACTOR * currentSpeedSample + (1 - SMOOTHING_FACTOR) * averageVerifySpeed;
                                    } else {
                                        averageVerifySpeed = currentSpeedSample;
                                    }

                                    totalBytesRemaining -= seek;
                                    timeRemaining = (long) (totalBytesRemaining / averageVerifySpeed);
                                    this.publishProgress(new DownloadProgressInfo(totalCompressedLength, totalCompressedLength - totalBytesRemaining, timeRemaining, averageVerifySpeed));
                                }
                                startTime = currentTime;
                                if (cancelValidation) {
                                    return true;
                                }
                            }

                            if (crc.getValue() != entry.mCRC32) {
                                Log.e(TAG, "CRC does not match for entry: " + entry.mFileName + " in file: " + entry.getZipFileName());
                                return false;
                            }
                        } finally {
                            if (null != dis) {
                                dis.close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(DownloadProgressInfo... values) {
        BusProvider.getInstance().post(new ProgressUpdateEvent(values[0]));
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        BusProvider.getInstance().post(new PostExecuteEvent(result));
        super.onPostExecute(result);
    }

    public void setCancelValidation(boolean cancelValidation) {
        this.cancelValidation = cancelValidation;
    }
}
