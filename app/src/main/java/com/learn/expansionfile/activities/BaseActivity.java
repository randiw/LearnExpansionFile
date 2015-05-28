package com.learn.expansionfile.activities;

import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;

import butterknife.ButterKnife;

/**
 * Created by randiwaranugraha on 5/28/15.
 */
public abstract class BaseActivity extends FragmentActivity {

    protected void setupLayout(@LayoutRes int layout) {
        setContentView(layout);
        ButterKnife.inject(this);
    }
}