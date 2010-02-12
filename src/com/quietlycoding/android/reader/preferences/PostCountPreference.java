/* Copyright (C) 2009 Michael Novak <mike@androidnerds.org>
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

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.quietlycoding.android.reader.R;
import com.quietlycoding.android.reader.widget.NumberPicker;

/**
 * The PostCountPreference allows the user to set the number of posts to download from 
 * each feed, this preference uses a somewhat custom widget NumberPicker
 *
 * @author mike@androidnerds.org
 */
public class PostCountPreference extends DialogPreference {

    private static final String TAG = "PostCountPreference";

    public PostCountPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setDialogLayoutResource(R.layout.number_picker_pref);   
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        NumberPicker picker = (NumberPicker) view.findViewById(R.id.pref_num_picker);
        picker.setCurrent(getValue());
        
    }

    public PostCountPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public PostCountPreference(Context context) {
        this(context, null);
    }

    public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "which: " + which);

        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            NumberPicker picker = (NumberPicker) getDialog().findViewById(R.id.pref_num_picker);

            saveValue(picker.getCurrent());
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            break;
        default:
            break;
        }
    }

    /**
     * Saves the value from the NumberPicker into the SharedPreferences as an int,
     * this value is used in the sync service when syncing the posts for each feed.
     *
     * @param val the value grabbed from the NumberPicker widget.
     */
    private void saveValue(int val) {
        getEditor().putInt("num_posts", val).commit();
    }

    /**
     * The method returns the integer value of the post count from the shared 
     * preferences for the application, if no value has been set yet the method
     * returns the default value of 25.
     *
     * @return val the value from preferences or a default of 25.
     */
    private int getValue() {
        return getSharedPreferences().getInt("num_posts", 25);
    }
}