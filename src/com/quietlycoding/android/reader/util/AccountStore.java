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
package com.quietlycoding.android.reader.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import com.quietlycoding.android.reader.Constants;
import com.quietlycoding.android.reader.provider.AccountProvider;
import com.quietlycoding.android.reader.util.api.Authentication;

public abstract class AccountStore {

    private static final String TAG = "Reader.AccountStore";

    public static AccountStore getInstance() {
        if (Constants.PRE_ECLAIR) {
            return PreEclairAccount.Holder.sInstance;
        } else {
            return EclairAccount.Holder.sInstance;
        }
    }

    public abstract String getAccountToken(Context context, String account, String pass);

    public abstract String[] getAccounts(Context context);

    private static class PreEclairAccount extends AccountStore {

        private static class Holder {
            private static final PreEclairAccount sInstance = new PreEclairAccount();
        }

        @Override
        public String getAccountToken(Context context, String account, String pass) {
            Log.d(TAG, "PreEclair token request.");

            return Authentication.getAuthToken(account, pass);
        }

        @Override
        public String[] getAccounts(Context context) {
            final AccountProvider provider = new AccountProvider(context);
            final String user = provider.getMasterAccount();

            if (user == null) {
                return null;
            } else {
                return new String[] { user };
            }
        }

    }

    private static class EclairAccount extends AccountStore {

        private static class Holder {
            private static final EclairAccount sInstance = new EclairAccount();
        }

        @Override
        public String getAccountToken(Context context, String account, String pass) {
            final AccountProvider provider = new AccountProvider(context);
            final String token = provider.getAuthToken(provider.getMasterAccount());

            return token;
        }

        @Override
        public String[] getAccounts(Context context) {
            final AccountManager manager = AccountManager.get(context);
            final Account[] accts = manager.getAccountsByType("com.google");
            final String[] names = new String[accts.length];

            for (int i = 0; i < accts.length; i++) {
                Log.d(TAG, "Account: " + accts[i]);
                names[i] = accts[i].name;
            }

            return names;
        }
    }
}
