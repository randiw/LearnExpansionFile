package com.learn.expansionfile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.learn.expansionfile.R;
import com.learn.expansionfile.provider.ZipContentProvider;
import com.learn.expansionfile.view.adapter.ZipFileAdapter;

import java.io.IOException;

import butterknife.InjectView;
import butterknife.OnItemClick;


public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.title) TextView title;
    @InjectView(R.id.list) ListView list;

    private ZipFileAdapter fileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_main);

        try {
            ZipResourceFile expansionFile = APKExpansionSupport.getAPKExpansionZipFile(getApplication(), ZipContentProvider.MAIN_VERSION, ZipContentProvider.PATCH_VERSION);
            title.setText(expansionFile.toString());

            ZipResourceFile.ZipEntryRO[] entries = expansionFile.getAllEntries();
            fileAdapter = new ZipFileAdapter(getApplicationContext(), entries);
            list.setAdapter(fileAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnItemClick(R.id.list)
    public void pickItem(int position) {
        ZipResourceFile.ZipEntryRO entry = fileAdapter.getItem(position);
        String fileName = entry.mFileName;

        if(fileName.endsWith(".mp4")) {
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            intent.putExtra(VideoActivity.FILENAME, fileName);
            startActivity(intent);
        } else if(fileName.endsWith(".mp3")) {
            Intent intent = new Intent(MainActivity.this, AudioActivity.class);
            intent.putExtra(AudioActivity.FILENAME, fileName);
            startActivity(intent);
        }
    }
}