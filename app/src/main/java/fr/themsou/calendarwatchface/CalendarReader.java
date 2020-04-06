package fr.themsou.calendarwatchface;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.wearable.provider.WearableCalendarContract;
import android.util.Log;
import java.util.ArrayList;
import java.util.Comparator;

class CalendarReader {

    private static final String TAG = "CalendarWatchFace";

    static ArrayList<Event> get24HEvents(Context context){

        Log.d(TAG, "----- Getting events... -----");

        ArrayList<Event> events = new ArrayList<>();

        long beginDate = System.currentTimeMillis();
        Uri.Builder builder = WearableCalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, beginDate); // Définis la première date de recharche
        ContentUris.appendId(builder, beginDate + (1000 * 60 * 60 * 24)); // Définis la seconde date de recharche
        final Cursor cursor = context.getContentResolver().query(builder.build(),null, null, null, null);

        while(cursor.moveToNext()){

            long begin = cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.BEGIN))/1000/60;
            long end = cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.END))/1000/60;
            String name = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.TITLE));
            boolean allDay = cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.ALL_DAY)) == 1;

            events.add(new Event(name, begin, end, allDay));

        }
        cursor.close();

        events.sort(new Comparator<Event>() {
            @Override public int compare(Event o1, Event o2) {
                return Long.compare(o1.getBegin(), o2.getBegin());
            }
        });

        return events;
    }

}
