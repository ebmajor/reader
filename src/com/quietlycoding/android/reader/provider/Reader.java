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
package com.quietlycoding.android.reader.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Reader {

    public static final String AUTHORITY = "com.quietlycoding.android.reader.provider.Reader";

    public interface Channels extends BaseColumns {

        /**
         * The Uri for accessing a specific channel.
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/channels");

        public static final String DEFAULT_SORT_ORDER = "title ASC";

        /**
         * Channel title, can be modified by the user but is pulled from the feed itself by default.
         */
        public static final String TITLE = "title";

        /**
         * The feed url to pull, this is the actual url and not the Google Reader version.
         */
        public static final String URL = "url";

        /**
         * The feed website's favicon, if this isn't present the default app icon will be used.
         */
        public static final String ICON = "icon";
        public static final String ICON_URL = "icon_url";

        /**
         * The logo attribute from the xml feed, if this exists we'll use it.
         */
        public static final String LOGO = "logo";

        public static final String LABEL = "label";

        /**
         * This field determines if the item in the database needs to sync with Google Reader
         */
        public static final String SYNC = "sync";

        public static final class SQL {
            public static final String TABLE = "channels";

            public static final String CREATE = "CREATE TABLE " + TABLE 
                    + " (_id INTEGER PRIMARY KEY, " + TITLE + " TEXT UNIQUE, " + URL + " TEXT UNIQUE, "
                    + ICON + " TEXT, " + ICON_URL + " TEXT, " + LOGO + " TEXT, "
                    + SYNC + " INTEGER(1) DEFAULT '0');";

            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;
        }
    }

    public interface Posts extends BaseColumns {

        /**
         * Uri for accessing a specific post.
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/posts");

        /**
         * Uri for accessing a list of posts for a specific channel.
         */
        public static final Uri CONTENT_URI_LIST = 
                Uri.parse("content://" + AUTHORITY + "/postlist");

        public static final String DEFAULT_SORT_ORDER = "posted_on DESC";

        /**
         * Reference to the channel _id that a post belongs to.
         */
        public static final String CHANNEL_ID = "channel_id";

        /**
         * Boolean value to determine whether the post has been read or not.
         */
        public static final String READ = "read";

        /**
         *
         */
        public static final String STARRED = "starred";

        /**
         * The post subject.
         */
        public static final String TITLE = "title";

        /**
         * The post author.
         */
        public static final String AUTHOR = "author";

        /**
         * The url to allow the user to see the full post.
         */
        public static final String URL = "url";

        /**
         * The body of the post.
         */
        public static final String BODY = "body";

        /**
         * Date of the post.
         */
        public static final String DATE = "posted_on";

        /**
         * This field determines if the item needs to sync with Google Reader
         */
        public static final String SYNC = "sync";

        public static final class SQL {

            public static final String TABLE = "posts";

            public static final String CREATE = "CREATE TABLE " + TABLE
                    + " (_id INTEGER PRIMARY KEY, " + CHANNEL_ID + " INTEGER, " + TITLE
                    + " TEXT, " + URL + " TEXT, " + DATE + " DATETIME, " + BODY + " TEXT, " 
                    + AUTHOR + " TEXT, " + READ + " INTEGER(1) DEFAULT '0', " 
                    + STARRED + " INTEGER(1) DEFAULT '0', " + SYNC + " INTEGER(1) DEFAULT '0');";

            public static final String[] INDEX  = new String[] {
                    "CREATE UNIQUE INDEX unq_post ON " + TABLE + " (" + TITLE + ", " + URL + ");",
                    "CREATE INDEX idx_channel ON " + TABLE + " (" + CHANNEL_ID + ");",
                    "CREATE INDEX idx_starred ON " + TABLE + " (" + STARRED + ");"
            };

            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;
        }
		
    }

    public interface Labels extends BaseColumns {
        
        /**
         * Uri for accessing the list of labels.
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/labels");

        /**
         * Uri for accessing channels under a label.
         */
        public static final Uri CONTENT_URI_CHANNEL_LABELS =
                Uri.parse("content://" + AUTHORITY + "/channels/label");

        /**
         * Uri for accessing posts under a label.
         */
        public static final Uri CONTENT_URI_POST_LABELS =
                Uri.parse("content://" + AUTHORITY + "/posts/label");

        public static final String DEFAULT_SORT_ORDER = "title ASC";

        /**
         * the label id is the id string returned by Google Reader
         */
        public static final String LABEL_ID = "label_id";

        /**
         * the title field is the human readable label
         */
        public static final String TITLE = "title";

        /**
         * the sort id is return from Google Reader
         */
        public static final String SORT_ID = "sort_id";

        /**
         * the sync field signifies whether this item needs to sync with Google Reader
         */
        public static final String SYNC = "sync";

        public static final class SQL {
            public static final String TABLE = "labels";

            public static final String CREATE = "CREATE TABLE " + TABLE
                    + " (_id INTEGER PRIMARY KEY, " + LABEL_ID + " INTEGER, " + TITLE
                    + " TEXT, " + SORT_ID + " TEXT, " + SYNC + " INTEGER(1) DEFAULT '0');";

            public static final String[] INDEX = new String[] {
                    "CREATE UNIQUE INDEX unq_label ON " + TABLE + " (" + TITLE + ");",
                    "CREATE INDEX idx_label ON " + TABLE + " (" + TITLE + ");"
            };

            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;
        }
    }
}
