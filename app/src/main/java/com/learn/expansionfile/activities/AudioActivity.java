package com.learn.expansionfile.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.learn.expansionfile.R;
import com.learn.expansionfile.provider.ZipContentProvider;

import butterknife.InjectView;
import nl.changer.audiowife.AudioWife;

/**
 * Created by randiwaranugraha on 6/1/15.
 */
public class AudioActivity extends BaseActivity {

    public static final String TAG = AudioActivity.class.getSimpleName();

    @InjectView(R.id.audioContainer) LinearLayout audioContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_audio);

        String fileName = getIntent().getStringExtra(FILENAME);
        Log.d(TAG, "fileName: " + fileName);

        Uri audioUri = ZipContentProvider.buildUri(fileName);
        Log.d(TAG, "audioUri: " + audioUri.toString());

        AudioWife.getInstance().init(getApplicationContext(), audioUri).useDefaultUi(audioContainer, getLayoutInflater());
    }
}