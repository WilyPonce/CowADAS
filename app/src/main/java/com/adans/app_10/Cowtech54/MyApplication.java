package com.adans.app_10.Cowtech54;

import android.app.Application;

public class MyApplication extends Application {

    public boolean isBound;

    public boolean isBound() {
        return isBound;
    }

    public void setBound(boolean bound) {
        isBound = bound;
    }
}
