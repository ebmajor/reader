/* Copyright (C) 2009, 2010 QuietlyCoding <mike@quietlycoding.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.quietlycoding.android.reader.sync;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.quietlycoding.android.reader.Constants;
import com.quietlycoding.android.reader.R;
import com.quietlycoding.android.reader.activity.AccountActivity;
import com.quietlycoding.android.reader.activity.ChannelList;
import com.quietlycoding.android.reader.activity.EclairAccountActivity;
import com.quietlycoding.android.reader.provider.AccountProvider;
import com.quietlycoding.android.reader.provider.Reader;
import com.quietlycoding.android.reader.util.api.Subscriptions;

/**
 * The SyncService can be started either by the AlarmManager or directly
 * by the user via the applications sync menu option.
 *
 * @author mike.novak
 */
public class SyncService extends Service {

    private static final String TAG = "Reader.SyncService";
    
    private NotificationManager mManager;
    private int mPostCount;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        SharedPreferences preferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
        mPostCount = preferences.getInt("num.posts", 25);

        AccountProvider provider = new AccountProvider(this);
        String token = provider.getAuthToken(provider.getMasterAccount());
        
        //if the token is null we need to send a notification to the user we can't sync.
        if (token == null) {
            notifyFailure();
        }

        notifyActive();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mManager.cancel(R.string.reader_sync);
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mManager.cancel(R.string.reader_sync);
        stopSelf();
        
        return false;
    }

    private final ISyncService.Stub mBinder = new ISyncService.Stub() {
        public void manualSync() {

        }
    };

    private void notifyFailure() {
        Intent intent = null;
    
        if (Constants.PRE_ECLAIR) {
            intent = new Intent(this, AccountActivity.class);
        } else {
            intent = new Intent(this, EclairAccountActivity.class);
        }

        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, 0);
        Notification n = new Notification(android.R.drawable.stat_notify_error, "Please authenticate with Google Reader!",
                System.currentTimeMillis());
        n.setLatestEventInfo(this, "Reader", "Google Reader Sync Error", pending);
        mManager.notify(R.string.sync_error, n);
        stopSelf();
    }

    private void notifyActive() {
        Intent intent = new Intent(this, ChannelList.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, 0);
        Notification n = new Notification(android.R.drawable.stat_notify_sync,
                "Syncing with Google Reader", System.currentTimeMillis());
        n.setLatestEventInfo(this, "Reader", "Syncing with Google Reader", pending);
        n.flags = Notification.FLAG_ONGOING_EVENT;

        mManager.notify(R.string.reader_sync, n);
    }

    private Runnable sync = new Runnable() {
        public void run() {
            Log.i(TAG, "Syncing with Google Reader...");

            syncSubscriptions();

            return;
        }
    };

    /**
     * This method synchronizes the channels in the app with subscriptions on Google Reader.
     */
    private void syncSubscriptions() {
        
    }
}

