/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calendar.selectcalendars;

import com.android.calendar.AsyncQueryService;
import com.android.calendar.CalendarController.EventInfo;
import com.android.calendar.CalendarController.EventType;
import com.android.calendar.R;
import com.android.calendar.CalendarController;
import com.android.calendar.Utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract.Calendars;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectVisibleCalendarsFragment extends Fragment
        implements AdapterView.OnItemClickListener, CalendarController.EventHandler {

    private static final String TAG = "Calendar";
    private static final String IS_PRIMARY = "\"primary\"";
    private static final String SELECTION = Calendars.SYNC_EVENTS + "=?";
    private static final String[] SELECTION_ARGS = new String[] {"1"};

    private static final String[] PROJECTION = new String[] {
        Calendars._ID,
        Calendars.ACCOUNT_NAME,
        Calendars.OWNER_ACCOUNT,
        Calendars.CALENDAR_DISPLAY_NAME,
        Calendars.CALENDAR_COLOR,
        Calendars.VISIBLE,
        Calendars.SYNC_EVENTS,
        "(" + Calendars.ACCOUNT_NAME + "=" + Calendars.OWNER_ACCOUNT + ") AS " + IS_PRIMARY,
      };
    private static int mUpdateToken;
    private static int mQueryToken;
    private static int mCalendarItemLayout = R.layout.mini_calendar_item;

    private View mView = null;
    private ListView mList;
    private SelectCalendarsSimpleAdapter mAdapter;
    private Activity mContext;
    private AsyncQueryService mService;
    private Cursor mCursor;

    // Refresh list identifier
    private final int REFRESH_CALENDARS_LIST = 1001;
    // Refresh list delay 300ms to make sure the Database has
    // updated completely.
    private final long REFRESH_CALENDARS_DELAY = 200l;

    // The handler used to refresh the Calendar group list.
    private Handler mCalendarsRefreshHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == REFRESH_CALENDARS_LIST) {
                eventsChanged();
            }
        };
    };

    // ContentObserver which listened the changes of Calendars DB, if
    // the DB changed, refresh the select visible calendars list.
    private ContentObserver mCalendarsObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            refreshCalendarsListDelayed(REFRESH_CALENDARS_DELAY);
        }
    };

    private void refreshCalendarsListDelayed(long delayMillis) {
        if (mCalendarsRefreshHandler != null) {
            mCalendarsRefreshHandler.removeMessages(REFRESH_CALENDARS_LIST);
            // Add a delay to avoid to refresh the calendars list every time.
            mCalendarsRefreshHandler.sendEmptyMessageDelayed(REFRESH_CALENDARS_LIST, delayMillis);
        }
    }

    public SelectVisibleCalendarsFragment() {
    }

    public SelectVisibleCalendarsFragment(int itemLayout) {
        mCalendarItemLayout = itemLayout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mService = new AsyncQueryService(activity) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                mAdapter.changeCursor(cursor);
                mCursor = cursor;
            }
        };
        // Register a ContentObserver here for listen the changes of Calendars DB.
        if (mContext != null && mContext.getContentResolver() != null) {
            mContext.getContentResolver().registerContentObserver(Calendars.CONTENT_URI,
                    true, mCalendarsObserver);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Unregister the ContentObserver when this Fragment detached from Activity.
        if (mContext != null && mContext.getContentResolver() != null) {
            mContext.getContentResolver().unregisterContentObserver(mCalendarsObserver);
        }
        if (mCursor != null) {
            mAdapter.changeCursor(null);
            mCursor.close();
            mCursor = null;
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.select_calendars_fragment, null);
        mList = (ListView)mView.findViewById(R.id.list);

        // Hide the Calendars to Sync button on tablets for now.
        // Long terms stick it in the list of calendars
        if (Utils.getConfigBool(getActivity(), R.bool.multiple_pane_config)) {
            // Don't show dividers on tablets
            mList.setDivider(null);
            View v = mView.findViewById(R.id.manage_sync_set);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new SelectCalendarsSimpleAdapter(mContext, mCalendarItemLayout, null);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
        if (mAdapter == null || mAdapter.getCount() <= position) {
            return;
        }
        toggleVisibility(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set the Calendars list to empty every time
        // to avoid the list flash when database changed.
        if (mAdapter != null && mCursor != null) {
            mAdapter.changeCursor(null);
            mCursor.close();
            mCursor = null;
        }
        // Read database and refresh Calendars list delayed.
        refreshCalendarsListDelayed(REFRESH_CALENDARS_DELAY);
    }

    /*
     * Write back the changes that have been made.
     */
    public void toggleVisibility(int position) {
        Log.d(TAG, "Toggling calendar at " + position);
        mUpdateToken = mService.getNextToken();
        Uri uri = ContentUris.withAppendedId(Calendars.CONTENT_URI, mAdapter.getItemId(position));
        ContentValues values = new ContentValues();
        // Toggle the current setting
        int visibility = mAdapter.getVisible(position)^1;
        values.put(Calendars.VISIBLE, visibility);
        mService.startUpdate(mUpdateToken, null, uri, values, null, null, 0);
        mAdapter.setVisible(position, visibility);
    }

    @Override
    public void eventsChanged() {
        if (mService != null) {
            mService.cancelOperation(mQueryToken);
            mQueryToken = mService.getNextToken();
            mService.startQuery(mQueryToken, null, Calendars.CONTENT_URI, PROJECTION, SELECTION,
                    SELECTION_ARGS, Calendars.ACCOUNT_NAME);
        }
    }

    @Override
    public long getSupportedEventTypes() {
        return EventType.EVENTS_CHANGED;
    }

    @Override
    public void handleEvent(EventInfo event) {
        if (event.eventType == EventType.EVENTS_CHANGED) {
            eventsChanged();
        }
    }
}
