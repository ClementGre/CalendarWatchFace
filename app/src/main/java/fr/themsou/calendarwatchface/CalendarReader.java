package fr.themsou.calendarwatchface;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.wearable.provider.WearableCalendarContract;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

class CalendarReader {

    private static final String TAG = "CalendarWatchFace";

    private static int hasPermission = -1;

    static boolean checkPermission(MyWatchFace.Engine engine){

        if(hasPermission != -1) return hasPermission==1;

        if(ActivityCompat.checkSelfPermission(engine.myWatchFace, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED){

            Intent myIntent = new Intent(engine.myWatchFace.getBaseContext(), PermissionRequestActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            myIntent.putExtra("KEY_PERMISSIONS", Manifest.permission.READ_CALENDAR);
            engine.myWatchFace.startActivity(myIntent);

            hasPermission = 0;
            return false;
        }
        hasPermission = 1;
        return true;

    }

    static void resetPermissionData(){
        hasPermission = -1;
    }

    static ArrayList<Event> get24HEvents(Context context, Calendar calendar){

        Log.d(TAG, "----- Getting events... -----");

        ArrayList<Event> events = new ArrayList<>();

        long beginDate = System.currentTimeMillis() - (calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE))*60*1000;

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

    public static Event getCurrentEvent(ArrayList<Event> events){

        long now = System.currentTimeMillis()/1000/60;

        for(Event event : events){
            if(event.getBegin() <= now && event.getEnd() > now){
                return event;
            }
        }
        return null;

    }
    public static Event getNextEvent(ArrayList<Event> events){

        long now = System.currentTimeMillis()/1000/60;

        for(Event event : events){
            if(event.getBegin() > now){
                return event;
            }
        }
        return null;

    }
}
