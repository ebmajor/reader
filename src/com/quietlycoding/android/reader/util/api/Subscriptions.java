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
package com.quietlycoding.android.reader.util.api;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The Subscriptions class manages adding, editing, removing and listing all the
 * subscriptions for the given Google Reader account.
 * 
 * @author mike.novak
 */
public class Subscriptions {
    private static final String SUB_URL = "https://www.google.com/reader/api/0/subscription";
    private static final String TAG = "Reader.Api";

    /**
     * This method queries Google Reader for the list of subscribed feeds.
     * 
     * @param sid
     *            authentication code to pass along in a cookie.
     * @return arr returns a JSONArray of JSONObjects for each feed.
     * 
     *         The JSONObject returned by the service looks like this: id: this
     *         is the feed url. title: this is the title of the feed. sortid:
     *         this has not been figured out yet. firstitemsec: this has not
     *         been figured out yet.
     */
    public static JSONArray getSubscriptionList(String sid) {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpGet get = new HttpGet(SUB_URL + "/list?output=json");
        final BasicClientCookie cookie = Authentication.buildCookie(sid);

        try {
            client.getCookieStore().addCookie(cookie);

            final HttpResponse response = client.execute(get);
            final HttpEntity respEntity = response.getEntity();

            Log.d(TAG, "Response from server: " + response.getStatusLine());

            final InputStream in = respEntity.getContent();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line = "";
            String arr = "";
            while ((line = reader.readLine()) != null) {
                arr += line;
            }

            final JSONObject obj = new JSONObject(arr);
            final JSONArray array = obj.getJSONArray("subscriptions");

            reader.close();
            client.getConnectionManager().shutdown();

            return array;
        } catch (final Exception e) {
            Log.d(TAG, "Exception caught:: " + e.toString());
            return null;
        }
    }
}
