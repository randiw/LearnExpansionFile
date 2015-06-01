package com.learn.expansionfile.helper;

import com.squareup.otto.Bus;

/**
 * Created by randiwaranugraha on 3/31/15.
 */
public final class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {

    }
}