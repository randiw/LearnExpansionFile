package com.learn.expansionfile.activities;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.learn.expansionfile.R;
import com.learn.expansionfile.view.adapter.ZipFileAdapter;

import java.io.IOException;

import butterknife.InjectView;


public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.title) TextView title;
    @InjectView(R.id.list) ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_main);

        try {
            ZipResourceFile expansionFile = APKExpansionSupport.getAPKExpansionZipFile(getApplication(), 1, 0);
            title.setText(expansionFile.toString());

            ZipResourceFile.ZipEntryRO[] entries = expansionFile.getAllEntries();
            ZipFileAdapter fileAdapter = new ZipFileAdapter(getApplicationContext(), entries);
            list.setAdapter(fileAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}