package com.learn.expansionfile.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.learn.expansionfile.R;

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

        mediaController = new MediaController(this);
        mediaController.setMediaPlayer(videoView);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
//        videoView.setVideoURI(Uri.parse(uriPath));
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