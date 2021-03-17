package com.smartvision.webappessentials;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
//    private Tracker tracker;
//    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
//
//    public enum TrackerName {
//        APP_TRACKER, // Tracker used only in this app.
//    }
//    synchronized Tracker getTracker(TrackerName trackerId) {
//        if (!mTrackers.containsKey(trackerId)) {
//            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//            Tracker t = analytics.newTracker(getString(R.string.analytics_property_id));
//            mTrackers.put(trackerId, t);
//        }
//        return mTrackers.get(trackerId);
//    }
    long Delay = 4000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.splash);

        Timer RunSplash = new Timer();
        TimerTask ShowSplash = new TimerTask() {
            @Override
            public void run() {
                finish();
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }
        };
        RunSplash.schedule(ShowSplash, Delay);


//
//        // ---------------------- ANALYTICS ---------------------
//
//        if (getString(R.string.analytics_property_id) != null && !getString(R.string.analytics_property_id).isEmpty()) {
//            GoogleAnalytics.getInstance(this).newTracker(getString(R.string.analytics_property_id));
//            GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
//            tracker = getTracker(TrackerName.APP_TRACKER);
//            tracker.setScreenName("SplashActivity");
//            tracker.send(new HitBuilders.AppViewBuilder().build());
//        }
//
//        Thread splashThread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    int waited = 0;
//                    while (waited < Integer.parseInt(getString(R.string.splash_delay))) {
//                        sleep(100);
//                        waited += 100;
//                    }
//
//                } catch (InterruptedException e) {
//                    // do nothing
//                } finally {
//                    finish();
//                    Intent i = new Intent(getBaseContext(), MainActivity.class);
//                    startActivity(i);
//                }
//            }
//        };
//        splashThread.start();
    }
}