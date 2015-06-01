package com.learn.expansionfile.events;

import com.google.android.vending.expansion.downloader.DownloadProgressInfo;

/**
 * Created by randiwaranugraha on 6/1/15.
 */
public class ProgressUpdateEvent {

    public final DownloadProgressInfo progressInfo;

    public ProgressUpdateEvent(DownloadProgressInfo progressInfo) {
        this.progressInfo = progressInfo;
    }

    @Override
    public String toString() {
        if(progressInfo == null) {
            return "progressInfo null";
        }

        return progressInfo.toString();
    }
}