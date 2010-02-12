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
import java.util.ArrayList;

/**
 * The Tags class contains the starred url, the labels, and broadcast, and
 * blogger followings; the return from the server is a JSONArray
 * 
 * @author mike.novak
 */
public class Tags {
    private static final String URL = "https://www.google.com/reader/api/0/tag";
    private static final String TAG = "Reader.Api";

    /**
     * This method pulls the tags from Google Reader, its used by the methods in
     * this class to communicate before parsing the specific results.
     * 
     * @param sid
     *            the Google Reader authentication string.
     * @return arr JSONArray of the items from the server.
     */
    private static JSONArray pullTags(String sid) {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpGet get = new HttpGet(URL);
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
            final JSONArray array = obj.getJSONArray("tags");

            return array;
        } catch (final Exception e) {
            Log.d(TAG, "Exception caught:: " + e.toString());
            return null;
        }
    }

    /**
     * This method grabs the labels and puts them in an ArrayList to return back
     * to the caller, basically labels are folders which have different
     * subscribed feeds in them; See
     * {@link org.androidnerds.feeds.reader.Subscriptions#subscriptionsInLabel}
     * for how to pull the subscribed feeds for a given label.
     * 
     * @param sid
     *            the Google Reader authentication string.
     * @return labels the ArrayList of labels
     */
    public static ArrayList<Label> getLabels(String sid) {
        final JSONArray tags = pullTags(sid);
        final int size = tags.length();
        final ArrayList<Label> labels = new ArrayList<Label>(size);

        try {
            for (int i = 0; i < size; i++) {
                if (!tags.isNull(i)) {
                    final JSONObject tag = tags.getJSONObject(i);
                    final String id = tag.getString("id");

                    if (id.contains("label")) {
                        final String label = id.substring(id.lastIndexOf("/") + 1);
                        final Label l = new Label(label, id);
                        l.setSortId(tag.getString("sortid"));
                        labels.add(l);
                    }
                }
            }
        } catch (final Exception e) {
            Log.d(TAG, "Exception caught:: " + e.toString());
        }

        return labels;
    }
}
