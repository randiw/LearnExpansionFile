package com.learn.expansionfile.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Messenger;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.crashlytics.android.Crashlytics;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;
import com.learn.expansionfile.R;
import com.learn.expansionfile.events.PostExecuteEvent;
import com.learn.expansionfile.events.PreExecuteEvent;
import com.learn.expansionfile.events.ProgressUpdateEvent;
import com.learn.expansionfile.helper.BusProvider;
import com.learn.expansionfile.helper.XAPKFile;
import com.learn.expansionfile.provider.ZipContentProvider;
import com.learn.expansionfile.services.MyDownloaderService;
import com.learn.expansionfile.task.ValidateXAPKTask;
import com.squareup.otto.Subscribe;

import io.fabric.sdk.android.Fabric;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by randiwaranugraha on 5/28/15.
 */
public class SplashActivity extends BaseActivity implements IDownloaderClient {

    public static final String TAG = SplashActivity.class.getSimpleName();

    private static final XAPKFile[] xAPKS = {
            new XAPKFile(true, ZipContentProvider.MAIN_VERSION, ZipContentProvider.MAIN_FILE_SIZE)
    };

    @InjectView(R.id.downloaderDashboard) LinearLayout dashboard;
    @InjectView(R.id.progressBar) ProgressBar progressBar;
    @InjectView(R.id.statusText) TextView statusText;
    @InjectView(R.id.progressAsFraction) TextView progressFraction;
    @InjectView(R.id.progressAsPercentage) TextView progressPercent;
    @InjectView(R.id.progressAverageSpeed) TextView averageSpeed;
    @InjectView(R.id.progressTimeRemaining) TextView timeRemaining;
    @InjectView(R.id.approveCellular) LinearLayout cellMessage;
    @InjectView(R.id.pauseButton) Button pauseButton;
    @InjectView(R.id.wifiSettingsButton) Button wifiSettingsButton;

    private boolean isStatePaused;
    private int state;

    private ValidateXAPKTask validationTask;
    private IDownloaderService remoteService;
    private IStub downloaderClientStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        downloaderClientStub = DownloaderClientMarshaller.CreateStub(this, MyDownloaderService.class);
        setupLayout(R.layout.activity_splash);

        if(!expansionFilesDelivered()) {

            try {
                Intent launchIntent = SplashActivity.this.getIntent();
                Intent intentToLaunchThisActivityFromNotification = new Intent(SplashActivity.this, SplashActivity.this.getClass());
                intentToLaunchThisActivityFromNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentToLaunchThisActivityFromNotification.setAction(launchIntent.getAction());

                if(launchIntent.getCategories() != null) {
                    for(String category : launchIntent.getCategories()) {
                        intentToLaunchThisActivityFromNotification.addCategory(category);
                    }
                }

                PendingIntent pendingIntent = PendingIntent.getActivity(SplashActivity.this, 0, intentToLaunchThisActivityFromNotification, PendingIntent.FLAG_UPDATE_CURRENT);

                int startResult = DownloaderClientMarshaller.startDownloadServiceIfRequired(this, pendingIntent, MyDownloaderService.class);
                if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED) {
                    return;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Cannot find own package! MAYDAY!");
                e.printStackTrace();
            }

        } else {
            validateXAPKZipFiles();
        }
    }

    @Override
    protected void onStart() {
        if(null != downloaderClientStub) {
            downloaderClientStub.connect(this);
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onStop() {
        if(null != downloaderClientStub) {
            downloaderClientStub.disconnect(this);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(validationTask != null) {
            validationTask.setCancelValidation(true);
        }
        super.onDestroy();
    }

    @OnClick(R.id.pauseButton)
    public void pause() {
        if(isStatePaused) {
            remoteService.requestContinueDownload();
        } else {
            remoteService.requestPauseDownload();
        }
        setButtonPausedState(!isStatePaused);
    }

    @OnClick(R.id.wifiSettingsButton)
    public void wifiSettings() {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @OnClick(R.id.resumeOverCellular)
    public void resumeOverCellular() {
        remoteService.setDownloadFlags(IDownloaderService.FLAGS_DOWNLOAD_OVER_CELLULAR);
        remoteService.requestContinueDownload();
        cellMessage.setVisibility(View.GONE);
    }

    private void setState(int newState) {
        if(state != newState) {
            state = newState;
            statusText.setText(Helpers.getDownloaderStringResourceIDFromState(newState));
        }
    }

    private void setButtonPausedState(boolean paused) {
        isStatePaused = paused;
        int stringResourceID = paused ? R.string.text_button_resume : R.string.text_button_pause;
        pauseButton.setText(stringResourceID);
    }

    private boolean expansionFilesDelivered() {
        for(XAPKFile xf : xAPKS) {
            String fileName = Helpers.getExpansionAPKFileName(getApplicationContext(), xf.isMain, xf.fileVersion);
            if(!Helpers.doesFileExist(getApplicationContext(), fileName, xf.fileSize, false)) {
                return false;
            }
        }

        return true;
    }

    @Subscribe
    public void onPreExecuteEvent(PreExecuteEvent event) {
        dashboard.setVisibility(View.VISIBLE);
        cellMessage.setVisibility(View.GONE);
        statusText.setText(R.string.text_verifying_download);
        pauseButton.setText(R.string.text_button_cancel_verify);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validationTask != null) {
                    validationTask.setCancelValidation(true);
                }
            }
        });
    }

    @Subscribe
    public void onProgressUpdateEvent(ProgressUpdateEvent event) {
        onDownloadProgress(event.progressInfo);
    }

    @Subscribe
    public void onPostExecuteEvent(PostExecuteEvent event) {
        if(event.result) {
            dashboard.setVisibility(View.VISIBLE);
            cellMessage.setVisibility(View.GONE);
            statusText.setText(R.string.text_validation_complete);
            pauseButton.setText(android.R.string.ok);
            pauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            });
        } else {
            dashboard.setVisibility(View.VISIBLE);
            cellMessage.setVisibility(View.GONE);
            statusText.setText(R.string.text_validation_failed);
            pauseButton.setText(android.R.string.cancel);
            pauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void validateXAPKZipFiles() {
        validationTask = new ValidateXAPKTask(getApplicationContext(), xAPKS);
        validationTask.execute(new Object());
    }

    @Override
    public void onServiceConnected(Messenger m) {
        remoteService = DownloaderServiceMarshaller.CreateProxy(m);
        remoteService.onClientUpdated(downloaderClientStub.getMessenger());
    }

    @Override
    public void onDownloadStateChanged(int newState) {
        setState(newState);
        boolean showDashboard = true;
        boolean showCellMessage = false;
        boolean paused;
        boolean indeterminate;

        switch (newState) {
            case IDownloaderClient.STATE_IDLE:
                paused = false;
                indeterminate = true;
                break;

            case IDownloaderClient.STATE_CONNECTING:
            case IDownloaderClient.STATE_FETCHING_URL:
                showDashboard = true;
                paused = false;
                indeterminate = true;
                break;

            case IDownloaderClient.STATE_DOWNLOADING:
                paused = false;
                showDashboard = true;
                indeterminate = false;
                break;

            case IDownloaderClient.STATE_FAILED_CANCELED:
            case IDownloaderClient.STATE_FAILED:
            case IDownloaderClient.STATE_FAILED_FETCHING_URL:
            case IDownloaderClient.STATE_FAILED_UNLICENSED:
                paused = true;
                showDashboard = true;
                indeterminate = false;
                break;

            case IDownloaderClient.STATE_PAUSED_NEED_CELLULAR_PERMISSION:
            case IDownloaderClient.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION:
                showDashboard = false;
                paused = true;
                indeterminate = false;
                showCellMessage = true;
                break;

            case IDownloaderClient.STATE_PAUSED_BY_REQUEST:
                paused = true;
                indeterminate = false;
                break;

            case IDownloaderClient.STATE_PAUSED_ROAMING:
            case IDownloaderClient.STATE_PAUSED_SDCARD_UNAVAILABLE:
                paused = true;
                indeterminate = false;
                break;

            case IDownloaderClient.STATE_COMPLETED:
                showDashboard = false;
                paused = false;
                indeterminate = false;
                validateXAPKZipFiles();
                break;

            default:
                paused = true;
                indeterminate = true;
                showDashboard = true;
                break;
        }

        int newDashboardVisibility = showDashboard ? View.VISIBLE : View.GONE;
        if(dashboard.getVisibility() != newDashboardVisibility) {
            dashboard.setVisibility(newDashboardVisibility);
        }

        int cellMessageVisibility = showCellMessage ? View.VISIBLE : View.GONE;
        if(cellMessage.getVisibility() != cellMessageVisibility) {
            cellMessage.setVisibility(cellMessageVisibility);
        }

        progressBar.setIndeterminate(indeterminate);
        setButtonPausedState(paused);
    }

    @Override
    public void onDownloadProgress(DownloadProgressInfo progress) {
        averageSpeed.setText(getString(R.string.kilobytes_per_second, Helpers.getSpeedString(progress.mCurrentSpeed)));
        timeRemaining.setText(getString(R.string.time_remaining, Helpers.getTimeRemaining(progress.mTimeRemaining)));

        progressBar.setMax((int) (progress.mOverallTotal >> 8));
        progressBar.setProgress((int) (progress.mOverallProgress >> 8));
        progressPercent.setText(Long.toString(progress.mOverallProgress * 100));
        progressFraction.setText(Helpers.getDownloadProgressString(progress.mOverallProgress, progress.mOverallTotal));
    }
}