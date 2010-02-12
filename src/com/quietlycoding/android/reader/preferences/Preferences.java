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
package com.quietlycoding.android.reader.preferences;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.os.Bundle;

import com.quietlycoding.android.reader.R;

public class Preferences extends PreferenceActivity {

    public Preferences() {
        
    }

    protected int resourceId() {
        return R.xml.preferences;
    }

    @Override
    public void onCreate(Bundle appState) {
        super.onCreate(appState);

        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName("reader-prefs");
        manager.setSharedPreferencesMode(MODE_PRIVATE);
        addPreferencesFromResource(resourceId());

        Preference p = findPreference("pref_account");

        if (p != null) {
            p.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference p, Object newObjValue) {
                    Boolean newValue = (Boolean) newObjValue;
                    if (newValue == null) {
                        return false;
                    }

                    return true;
                }
            });
        }
    }
}
