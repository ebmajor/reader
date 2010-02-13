package com.quietlycoding.android.reader.preferences;
 
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.util.AttributeSet;
import android.util.Log;
 
public class ReaderRingtonePreference extends RingtonePreference {
        private static final String TAG = "Reader.RingtonePreference";
        private long mProviderId;
 
        public ReaderRingtonePreference(Context context, AttributeSet attrs) {
                super(context, attrs);
 
                Intent intent = ((Activity) context).getIntent();
                mProviderId = intent.getLongExtra("providerId", -1);
 
                if (mProviderId < 0) {
                        Log.e(TAG, "RingtonePreference needs a provider id.");
                }
        }
 
        protected Uri onRestoreRingtone() {
                String ringtone = this.getSharedPreferences().getString("notification-ringtone", "");
                Uri result = Uri.parse(ringtone);
 
                return result;
        }
 
        protected void onSaveRingtone(Uri ringtone) {
                getEditor().putString("notification-ringtone", ringtone.toString()).commit();
        }
}
