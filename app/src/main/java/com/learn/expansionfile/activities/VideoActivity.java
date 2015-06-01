package com.learn.expansionfile.activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.learn.expansionfile.R;
import com.learn.expansionfile.provider.ZipContentProvider;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by randiwaranugraha on 5/28/15.
 */
public class VideoActivity extends BaseActivity {

    public static final String TAG = VideoActivity.class.getSimpleName();

    @InjectView(R.id.videoview) VideoView videoView;

    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_video);

        String fileName = getIntent().getStringExtra(FILENAME);
        Log.d(TAG, "fileName: " + fileName);
        setupVideo(fileName);
    }

    private void setupVideo(String fileName) {
        mediaController = new MediaController(this);
        mediaController.setMediaPlayer(videoView);
        mediaController.setAnchorView(videoView);

        Uri videoUri = ZipContentProvider.buildUri(fileName);
        Log.d(TAG, "videoUri: " + videoUri.toString());

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaController.show();
            }
        });
    }

    @OnClick(R.id.videoview)
    public void toggleController() {
        if (mediaController.isShowing()) {
            mediaController.hide();
        } else {
            mediaController.show(3000);
        }
    }
}