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
        ContentUris.appendId(builder, beginDate/* - (1000 * 60 * 60 * 24)*/); // Définis la première date de recharche
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

        events.sort(Comparator.comparingLong(Event::getBegin));

        return events;
    }

    public static ArrayList<Event> getCurrentEvents(Calendar calendar, ArrayList<Event> events, int max){

        long now = System.currentTimeMillis()/1000/60;
        ArrayList<Event> eventsFiltered = new ArrayList<>();

        for(Event event : events){
            if(!event.isAllDay() && event.shouldBeShown(calendar) && event.getBegin() <= now && event.getEnd() > now){
                if(eventsFiltered.size() < max){
                    eventsFiltered.add(event);
                    if(eventsFiltered.size() == max) break;
                }else break;
            }
        }
        return eventsFiltered;

    }

    public static ArrayList<Event> getNextEvents(Calendar calendar, ArrayList<Event> events, int count){

        long now = System.currentTimeMillis()/1000/60;
        ArrayList<Event> eventsFiltered = new ArrayList<>();

        for(Event event : events){
            if(!event.isAllDay() && event.shouldBeShown(calendar) && event.getBegin() > now && event.getRemainingMinutesBeforeBegin() < 60*12){
                if(eventsFiltered.size() < count){
                    eventsFiltered.add(event);
                    if(eventsFiltered.size() == count) break;
                }else break;
            }
        }
        return eventsFiltered;

    }
    public static ArrayList<Event> getCurrentFullDayEvent(Calendar calendar, ArrayList<Event> events, int count){

        long now = System.currentTimeMillis()/1000/60;
        ArrayList<Event> eventsFiltered = new ArrayList<>();

        for(Event event : events){
            if(event.isAllDay() && event.shouldBeShown(calendar)){
                if(eventsFiltered.size() < count){
                    eventsFiltered.add(event);
                    if(eventsFiltered.size() == count) break;
                }else break;

            }
        }
        return eventsFiltered;

    }
}
