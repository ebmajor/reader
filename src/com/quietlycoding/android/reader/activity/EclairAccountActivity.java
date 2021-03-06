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
package com.quietlycoding.android.reader.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.quietlycoding.android.reader.provider.AccountProvider;
import com.quietlycoding.android.reader.util.AccountStore;

/**
 * This activity will display a selection dialog if no account has been selected
 * for use with reader yet. If the user has an account that is associated with
 * reader stats on the last sync and next scheduled sync are displayed.
 * 
 * @note this activity is only called on devices running Android 2.0 and higher;
 *       if you are looking for the code on earlier devices see
 * @{link org.androidnerds.reader.activity.AccountActivity}
 * 
 * @author mike.novak
 */
public class EclairAccountActivity extends Activity {
    private static final String TAG = "Reader.EclairAccountActivity";
    private static final int ACCT_LIST = 1;

    private String[] mAccounts;
    private int mAccount;
    private Dialog mDialog;
    private AccountProvider mProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AccountStore store = AccountStore.getInstance();
        mAccounts = store.getAccounts(this);
        mAccount = 0;

        mProvider = new AccountProvider(this);
        final String master = mProvider.getMasterAccount();

        if (master == null) {
            onCreateDialog(ACCT_LIST);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
        case ACCT_LIST:
            builder.setTitle("Select a Google Reader account");
            builder.setSingleChoiceItems(mAccounts, mAccount, mAccountListener);
            mDialog = builder.create();
            break;
        }

        mDialog.show();
        return mDialog;
    }

    private final DialogInterface.OnClickListener mAccountListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            if (which < 0) {
                close();
            }

            mAccount = which;

            final String name = mAccounts[which];
            final AccountManager manager = AccountManager.get(EclairAccountActivity.this);
            final Account[] accounts = manager.getAccountsByType("com.google");
            Account account = null;

            for (int i = 0; i < accounts.length; i++) {
                if (accounts[i].name.equals(name)) {
                    account = accounts[i];
                    break;
                }
            }

            try {
                final AccountManagerFuture<Bundle> future = manager.getAuthToken(account, "ah",
                        null, EclairAccountActivity.this, null, null);

                final Bundle authTokenBundle = future.getResult();
                setTokenForAccount(authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString());
            } catch (final OperationCanceledException e) {
                Log.d(TAG, "User decided not to follow through.");
                close();
            } catch (final Exception e1) {
                Log.d(TAG, "Exception has been caught:: " + e1.toString());
                close();
            }
        }
    };

    private void close() {
        finish();
    }

    /**
     * This method takes the token returned from the AccountManager and stores
     * it in the sqlite database use with the AccountStore class in the rest of
     * the application.
     */
    private void setTokenForAccount(String token) {
        mProvider.addAccount(mAccounts[mAccount], "", token, true);
        close();
    }
}
