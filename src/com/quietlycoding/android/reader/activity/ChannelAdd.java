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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.quietlycoding.android.reader.R;
import com.quietlycoding.android.reader.parser.PostListParser;
import com.quietlycoding.android.reader.provider.Reader;

import java.net.MalformedURLException;
import java.net.URL;

public class ChannelAdd extends Activity {

    private static final String TAG = "Reader.ChannelAdd";

    private EditText mUrlText;

    protected ProgressDialog mBusy;
    final Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.channel_add);

        mUrlText = (EditText) findViewById(R.id.url_edit);
        final Button add = (Button) findViewById(R.id.feed_add);

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addChannel();
            }
        });
    }

    private static URL getDefaultFavicon(String url) {
        try {
            final URL orig = new URL(url);
            final URL iconUrl = new URL(orig.getProtocol(), orig.getHost(), orig.getPort(),
                    "/favicon.ico");

            return iconUrl;
        } catch (final MalformedURLException e) {
            Log.d(TAG, "Exception caught: " + e.toString());
            return null;
        }
    }

    private void addChannel() {
        final String url = mUrlText.getText().toString();

        mBusy = ProgressDialog.show(ChannelAdd.this, "Downloading", "Accessing XML Feed...", true,
                false);

        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    final PostListParser parser = new PostListParser(getContentResolver());

                    final long id = parser.syncDb(null, -1, url);

                    if (id >= 0) {
                        final URL iconUrl = getDefaultFavicon(url);
                        parser.updateFavicon(id, iconUrl);
                    }

                    mHandler.post(new Runnable() {
                        public void run() {
                            mBusy.dismiss();
                            final Uri uri = ContentUris.withAppendedId(Reader.Channels.CONTENT_URI,
                                    id);
                            getIntent().setData(uri);

                            setResult(RESULT_OK, getIntent());
                            finish();
                        }
                    });
                } catch (final Exception e) {
                    Log.d("::Exception::", e.toString());
                    final String errmsg = e.getMessage();
                    final String errmsgFull = e.toString();

                    mHandler.post(new Runnable() {
                        public void run() {
                            mBusy.dismiss();

                            final String errstr = ((errmsgFull != null) ? errmsgFull : errmsg);
                            new AlertDialog.Builder(ChannelAdd.this).setTitle("Feed Error")
                                    .setMessage(
                                            "An error was encountered while accessing the feed: "
                                                    + errstr).setPositiveButton("Ok",
                                            new DialogInterface.OnClickListener() {
                                                // whatever.
                                                public void onClick(DialogInterface dialog,
                                                        int whichButton) {

                                                }
                                            }).create();
                        }
                    });
                }
            }
        };

        t.start();
    }
}
