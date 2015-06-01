package com.learn.expansionfile.provider;

import android.net.Uri;

import com.android.vending.expansion.zipfile.APEZProvider;

import java.io.File;

/**
 * Created by randiwaranugraha on 5/29/15.
 */
public class ZipContentProvider extends APEZProvider {

    private static final String AUTHORITY = "com.learn.expansionfile.provider.ZipContentProvider";

    public static final int MAIN_VERSION = 5;
    public static final long MAIN_FILE_SIZE = 60354140L;
    public static final int PATCH_VERSION = 0;

    public static Uri buildUri(String pathIntoApk) {
        StringBuilder builder = new StringBuilder("content://");
        builder.append(AUTHORITY);
        builder.append(File.separator);
        builder.append(pathIntoApk);

        return Uri.parse(builder.toString());
    }

    @Override
    public String getAuthority() {
        return AUTHORITY;
    }

    @Override
    public int getMainVersion() {
        return MAIN_VERSION;
    }

    @Override
    public int getPatchVersion() {
        return PATCH_VERSION;
    }
}