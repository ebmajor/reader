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

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * This class starts the Google Reader synchronization for devices running
 * Android 2.0 and later. Thanks to Sam Steele over at Last.fm for the great
 * tutorial on SyncAdapters. A link is provided in the AUTHORS file to his blog.
 * 
 * @author mike.novak
 */
public class SyncAdapterService extends Service {
    private static final String TAG = "Reader.SyncAdapterService";
    private static SyncAdapterImpl mSyncAdapter = null;

    public SyncAdapterService() {
        super();
    }

    private class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        private Context mContext;

        public SyncAdapterImpl(Context context) {
            super(context, true);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority,
                ContentProviderClient provider, SyncResult result) {
            try {
                SyncAdapterService.performSync(mContext, account, extras, authority, provider,
                        result);
            } catch (final OperationCanceledException e) {
                Log.d(TAG, "The operation has been canceled.");
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        ret = getSyncAdapter().getSyncAdapterBinder();
        return ret;
    }

    private SyncAdapterImpl getSyncAdapter() {
        if (mSyncAdapter == null) {
            mSyncAdapter = new SyncAdapterImpl(this);
        }

        return mSyncAdapter;
    }

    private static void performSync(Context context, Account account, Bundle extras,
            String authority, ContentProviderClient provider, SyncResult result)
            throws OperationCanceledException {

    }
}
