package fr.themsou.calendarwatchface;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.wearable.provider.WearableCalendarContract;
import android.util.Log;

class CalendarReader {

    private static final String TAG = "MyWatchFace";
    static Cursor cursor;

    static void tryGetCalendars(Context context){

        Log.d(TAG, "----- Getting events... -----");

        long begin = System.currentTimeMillis();
        Uri.Builder builder = WearableCalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, begin + 1000 * 60 * 60 * 24);
        final Cursor cursor = context.getContentResolver().query(builder.build(),null, null, null, null);

        while(cursor.moveToNext()){

            int startMinute = cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.BEGIN)) / 1000;
            int endMinute = cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.END)) / 1000;

            String name = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.TITLE));

            boolean allDay = cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.ALL_DAY)) == 1;

            Log.d(TAG, name + " -> start: " + ((System.currentTimeMillis() - startMinute)/60/60) + " | end: " + (System.currentTimeMillis() - endMinute) + " | allDay = " + allDay);

        }
        cursor.close();
    }

}
