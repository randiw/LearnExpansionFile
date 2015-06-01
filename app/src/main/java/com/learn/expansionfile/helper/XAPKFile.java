package com.learn.expansionfile.helper;

/**
 * Created by randiwaranugraha on 6/1/15.
 */
public class XAPKFile {

    public final boolean isMain;
    public final int fileVersion;
    public final long fileSize;

    public XAPKFile(boolean isMain, int fileVersion, long fileSize) {
        this.isMain = isMain;
        this.fileVersion = fileVersion;
        this.fileSize = fileSize;
    }
}