package com.learn.expansionfile.events;

/**
 * Created by randiwaranugraha on 6/1/15.
 */
public class PostExecuteEvent {

    public static final String TAG = PostExecuteEvent.class.getSimpleName();
    public final boolean result;

    public PostExecuteEvent(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return TAG + " result: " + result;
    }
}