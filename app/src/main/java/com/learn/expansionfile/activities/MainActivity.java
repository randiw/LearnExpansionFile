package com.learn.expansionfile.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.learn.expansionfile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.title) TextView title;
    @InjectView(R.id.list) ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_main);
    }
}