package fr.themsou.calendarwatchface;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.provider.CalendarContract;
import android.telecom.Call;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

class Designer {

    private static final String TAG = "CalendarWatchFace";

    private Paint paintTop;
    private Paint paintBottom;

    private Paint paintTicks;
    private Paint paintSelectedTicks;
    private Paint paintCurrentTick;
    private Path pathTicks;
    private Path pathSelectedTicks;
    private Path pathCurrentTick;

    private Paint paintHour;
    private Paint paintSeconds;
    private Paint paintDate;

    private Paint paintCurrentEvent;
    private Paint paintCurrentEventData;
    private Paint paintNextEvent;
    private Paint paintNextEventData;


    private ArrayList<Event> events;

    private MyWatchFace.Engine engine;

    private DateEvents dateEvents;

    Designer(MyWatchFace.Engine engine) {
        this.engine = engine;
    }

    void setupPaints() {

        paintTop = new Paint();
        paintBottom = new Paint();


        // TICKS

        paintTicks = new Paint();
        paintTicks.setColor(Color.rgb(150, 150, 150));
        paintTicks.setStyle(Paint.Style.FILL);
        paintTicks.setAntiAlias(true);

        paintSelectedTicks = new Paint();
        paintSelectedTicks.setColor(Color.rgb(255, 141, 26));
        paintSelectedTicks.setStyle(Paint.Style.FILL);
        paintSelectedTicks.setAntiAlias(true);

        paintCurrentTick = new Paint();
        paintCurrentTick.setColor(Color.rgb(42, 123, 155));
        paintCurrentTick.setStyle(Paint.Style.FILL);
        paintCurrentTick.setAntiAlias(true);

        // TEXT

        paintHour = new Paint();
        paintHour.setTextSize(140);
        paintHour.setTextScaleX(0.7f);
        paintHour.setTypeface(engine.FONT_TEXTMEONE_REGULAR);
        paintHour.setTextAlign(Paint.Align.CENTER);
        paintHour.setColor(Color.WHITE);
        paintHour.setAntiAlias(true);

        paintSeconds = new Paint();
        paintSeconds.setTextSize(50);
        paintSeconds.setTypeface(engine.FONT_TEXTMEONE_REGULAR);
        paintSeconds.setColor(Color.WHITE);
        paintSeconds.setAntiAlias(true);

        paintDate = new Paint();
        paintDate.setTextSize(27);
        paintDate.setTypeface(engine.FONT_DIN_BOLD);
        paintDate.setTextAlign(Paint.Align.CENTER);
        paintDate.setColor(Color.WHITE);
        paintDate.setAntiAlias(true);

        // EVENTS

        paintCurrentEvent = new Paint();
        paintCurrentEvent.setTextSize(27);
        paintCurrentEvent.setTypeface(engine.FONT_TEXTMEONE_REGULAR);
        paintCurrentEvent.setTextAlign(Paint.Align.CENTER);
        paintCurrentEvent.setColor(Color.LTGRAY);
        paintCurrentEvent.setAntiAlias(true);

        paintCurrentEventData = new Paint();
        paintCurrentEventData.setTextSize(22);
        paintCurrentEventData.setTextScaleX(0.9f);
        paintCurrentEventData.setTypeface(engine.FONT_TEXTMEONE_REGULAR);
        paintCurrentEventData.setTextAlign(Paint.Align.CENTER);
        paintCurrentEventData.setColor(Color.rgb(43, 152, 206));
        paintCurrentEventData.setAntiAlias(true);

        paintNextEvent = new Paint();
        paintNextEvent.setTextSize(22);
        paintNextEvent.setTypeface(engine.FONT_TEXTMEONE_REGULAR);
        paintNextEvent.setTextAlign(Paint.Align.CENTER);
        paintNextEvent.setColor(Color.rgb(170, 180, 0));
        paintNextEvent.setAntiAlias(true);

        paintNextEventData = new Paint();
        paintNextEventData.setTextSize(22);
        paintNextEventData.setTypeface(engine.FONT_TEXTMEONE_REGULAR);
        paintNextEventData.setTextAlign(Paint.Align.CENTER);
        paintNextEventData.setColor(Color.rgb(170, 180, 0));
        paintNextEventData.setAntiAlias(true);

        updatePaintsToFullMode();

        dateEvents = new DateEvents(new CallBack() {
            @Override public void call() { // MINUTES
                setupCurrentTickPath();
            }
        }, new CallBack() {
            @Override public void call() { // HOUR
                updateCalendar();
            }
        });
    }

    void updatePaintsToFullMode() {

        // Enable ShadowLayer and AntiAliasing

        paintHour.setShadowLayer(1, 1, 1, paintHour.getColor());
        paintSeconds.setShadowLayer(1, 1, 1, paintSeconds.getColor());
        paintDate.setShadowLayer(1, 1, 1, paintDate.getColor());

        paintCurrentEvent.setShadowLayer(1, 1, 1, paintCurrentEvent.getColor());
        paintCurrentEventData.setShadowLayer(1, 1, 1, paintCurrentEventData.getColor());
        paintNextEvent.setShadowLayer(1, 1, 1, paintNextEvent.getColor());
        paintNextEventData.setShadowLayer(1, 1, 1, paintNextEventData.getColor());


        CalendarReader.resetPermissionData();
    }

    void updatePaintsToAmbientMode() {

        // Disable ShadowLayer and AntiAliasing

        paintHour.clearShadowLayer();
        paintDate.clearShadowLayer();
        paintSeconds.clearShadowLayer();
        paintCurrentEvent.clearShadowLayer();
        paintCurrentEventData.clearShadowLayer();
        paintNextEvent.clearShadowLayer();
        paintNextEventData.clearShadowLayer();

    }

    void draw(Canvas canvas) {

        if(pathTicks == null) setupPaths();
        if(events == null) updateCalendar();
        if(pathCurrentTick == null) setupCurrentTickPath();

        dateEvents.tick(engine.calendar);

        canvas.drawRect(0, 0, engine.displayWidth, engine.displayCenterY, paintTop);
        canvas.drawRect(0, engine.displayCenterY, engine.displayWidth, engine.displayHeight, paintBottom);

        Event current = CalendarReader.getCurrentEvent(events);

        canvas.drawText(getTime(), engine.displayCenterX, engine.displayCenterY - 20, paintHour);
        if(!engine.isAmbient){

            canvas.drawText(":" + getSeconds(), engine.displayCenterX + 107, engine.displayCenterY - 20, paintSeconds);
            canvas.drawText(getFullDate(), engine.displayCenterX, engine.displayCenterY - 130, paintDate);

            Event next = CalendarReader.getNextEvent(events);
            if(next != null){
                canvas.drawText("Prochain à " + getTime(next.getSinceDayMinuteBegin(engine.calendar)), engine.displayCenterX, engine.displayCenterY + 120, paintNextEventData);
                canvas.drawText(next.getName(), engine.displayCenterX, engine.displayCenterY + 150, paintNextEvent);
            }else{
                canvas.drawText("Aucun à suivre", engine.displayCenterX, engine.displayCenterY + 120, paintNextEventData);
            }
            if(current != null){
                canvas.drawText(current.getName(), engine.displayCenterX, engine.displayCenterY + 40, paintCurrentEvent);
                canvas.drawText(getTime(current.getSinceDayMinuteBegin(engine.calendar)) + " > " + getTime(current.getSinceDayMinuteEnd(engine.calendar)) + " - " + current.getSinceNowMinutesEnd() + "mn restantes"
                        , engine.displayCenterX, engine.displayCenterY + 70, paintCurrentEventData);
            }else{
                canvas.drawText("Aucun évènement", engine.displayCenterX, engine.displayCenterY + 40, paintCurrentEvent);
            }

        }else{
            canvas.drawText(getShortDate(), engine.displayCenterX, engine.displayCenterY - 130, paintDate);

            if(current != null){
                canvas.drawText(current.getName(), engine.displayCenterX, engine.displayCenterY + 40, paintCurrentEvent);
                canvas.drawText(getTime(current.getSinceDayMinuteBegin(engine.calendar)) + " > " + getTime(current.getSinceDayMinuteEnd(engine.calendar)) + " - " + current.getSinceNowMinutesEnd() + "mn restantes"
                        , engine.displayCenterX, engine.displayCenterY + 75, paintCurrentEventData);
            }
        }

        // TICKS

        canvas.drawPath(pathTicks, paintTicks);
        canvas.drawPath(pathSelectedTicks, paintSelectedTicks);
        canvas.drawPath(pathCurrentTick, paintCurrentTick);
    }

    void singleTap(int x, int y){
        if(y >= engine.displayCenterY){
            Intent i;
            try {
                // { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] flg=0x10000000 pkg=com.google.android.wearable.app cmp=com.google.android.wearable.app/com.google.android.clockwork.home.calendar.AgendaActivity }
                i = engine.myWatchFace.getPackageManager().getLaunchIntentForPackage("com.google.android.wearable.app");
                if(i == null) throw new PackageManager.NameNotFoundException();
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                engine.myWatchFace.startActivity(i);
            }catch(PackageManager.NameNotFoundException e) {
                // Appli non présente
                Log.d(TAG, "Application calendrier introuvable Packages disponibles : ");

                final PackageManager pm = engine.myWatchFace.getPackageManager();
                List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

                for (ApplicationInfo packageInfo : packages) {
                    Log.d(TAG, "Installed package :" + packageInfo.packageName);
                    Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
                    Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
                    Log.d(TAG, " ");
                }
            }
        }
    }

    private void updateCalendar() {
        Log.d(TAG, "updateCalendar");

        if(CalendarReader.checkPermission(engine)){
            events = CalendarReader.get24HEvents(engine.myWatchFace, engine.calendar);
            setupSelectedTicksPath();
        }

    }

    private void setupCurrentTickPath(){

        pathCurrentTick = new Path();
        pathCurrentTick.setFillType(Path.FillType.WINDING);

        int tickIndex = (int) ((engine.calendar.get(Calendar.HOUR_OF_DAY)*60 + engine.calendar.get(Calendar.MINUTE)) / 15f);

        float tickRadius = engine.displayCenterX - 18;
        float tickRot = (float) -(Math.PI*2 / 96*tickIndex);
        float x = (float) Math.sin(tickRot) * tickRadius;
        float y = (float) Math.cos(tickRot) * tickRadius;

        pathCurrentTick.addCircle(engine.displayCenterX + x, engine.displayCenterY + y, 4, Path.Direction.CW);

    }

    private void setupSelectedTicksPath(){
        Log.d(TAG, "setupSelectedTicksPath");

        pathSelectedTicks = new Path();
        pathSelectedTicks.setFillType(Path.FillType.WINDING);

        float tickRadius = engine.displayCenterX - 9;

        for(Event event : events){

            if(event.isAllDay()) continue; // Enlève les évènements qui prennent toute la journée

            long begin = event.getSinceDayMinuteBegin(engine.calendar);
            if(begin > 24*60) continue; // Enlève les évènement qui n'est pas compris dans la journée.
            long end = begin + event.getDuration(); // plus optimisé de faire une soustraction que plusieurs long calculs.

            int beginIndex = (int) ((begin + 7.5) / 15f);
            int endIndex = (int) ((end - 7.5) / 15f);

            Log.d(TAG, event.getName() + " " + (begin/60) + ":" + (begin%60) + " (" + (beginIndex/4.0) + ") -> " + (end/60) + ":" + (end%60) + " (" + (endIndex/4.0) + ")");

            for(int tickIndex = beginIndex; tickIndex <= endIndex; tickIndex++){

                float tickRot = (float) -(Math.PI*2 / 96*tickIndex);
                float x = (float) Math.sin(tickRot) * tickRadius;
                float y = (float) Math.cos(tickRot) * tickRadius;

                int radius = (tickIndex % 4 == 0) ? 3 : 2;
                pathSelectedTicks.addCircle(engine.displayCenterX + x, engine.displayCenterY + y, radius, Path.Direction.CW);

            }
        }
    }

    private void setupPaths(){
        Log.d(TAG, "setupPaths");

        pathSelectedTicks = new Path();
        pathSelectedTicks.setFillType(Path.FillType.WINDING);

        pathTicks = new Path();
        pathTicks.setFillType(Path.FillType.WINDING);

        float tickRadius = engine.displayCenterX - 9;
        for(int tickIndex = 0; tickIndex < 96; tickIndex++){

            float tickRot = (float) (Math.PI * 2 / 96 * tickIndex);
            float x = (float) Math.sin(tickRot) * tickRadius;
            float y = (float) Math.cos(tickRot) * tickRadius;

            int radius = (tickIndex % 4 == 0) ? 3 : 2;
            pathTicks.addCircle(engine.displayCenterX + x, engine.displayCenterY + y, radius, Path.Direction.CW);

        }

    }

    private String getTime(long mn){
        int hour = (int) (mn) / 60;
        int minutes = (int) (mn) % 60;

        String stringHour = ((hour < 10) ? "0" : "") + hour;
        String stringMinutes = ((minutes < 10) ? "0" : "") + minutes;

        return stringHour + ":" + stringMinutes;
    }
    private String getTime(){
        int hour = engine.calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = engine.calendar.get(Calendar.MINUTE);

        String stringHour = ((hour < 10) ? "0" : "") + hour;
        String stringMinutes = ((minutes < 10) ? "0" : "") + minutes;


        return stringHour + ":" + stringMinutes;
    }
    private String getSeconds(){

        int seconds = engine.calendar.get(Calendar.SECOND);

        return ((seconds < 10) ? "0" : "") + seconds;

    }
    private String getShortDate(){

        int dayMonth = engine.calendar.get(Calendar.DAY_OF_MONTH);
        int month = engine.calendar.get(Calendar.MONTH);

        String stringMonth = ((month < 10) ? "0" : "") + month;

        return getWeakDay() + " " + dayMonth + "/" + stringMonth;
    }
    private String getFullDate(){

        int dayMonth = engine.calendar.get(Calendar.DAY_OF_MONTH);

        return getWeakDay() + " " + dayMonth + " " + getMonthName();

    }
    private String getWeakDay(){

        switch(engine.calendar.get(Calendar.DAY_OF_WEEK)){
            case 2:
                return "Lun";
            case 3:
                return "Mar";
            case 4:
                return "Mer";
            case 5:
                return "Jeu";
            case 6:
                return "Ven";
            case 7:
                return "Sam";
            case 1:
                return "Dim";
            default:
                return "---";
        }
    }
    private String getMonthName(){

        switch(engine.calendar.get(Calendar.MONTH)){
            case 0:
                return "Janvier";
            case 1:
                return "Février";
            case 2:
                return "Mars";
            case 3:
                return "Avril";
            case 4:
                return "Mai";
            case 5:
                return "Juin";
            case 6:
                return "Juillet";
            case 7:
                return "Août";
            case 8:
                return "Septembre";
            case 9:
                return "Octobre";
            case 10:
                return "Novembre";
            case 11:
                return "Décembre";
            default:
                return "-----";
        }
    }

    /*void drawHands(Canvas canvas){

        // Draw the background.
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBackgroundPaint);

        //These calculations reflect the rotation in degrees per unit of time, e.g., 360 / 60 = 6 and 360 / 12 = 30.

        final float secondsRotation = (engine.calendar.get(Calendar.SECOND) + engine.calendar.get(Calendar.MILLISECOND) / 1000f) * 6f;
        final float minutesRotation = engine.calendar.get(Calendar.MINUTE) * 6f;
        final float hoursRotation = (engine.calendar.get(Calendar.HOUR) * 30) + (engine.calendar.get(Calendar.MINUTE) / 2f);

        // save the canvas state before we begin to rotate it
        canvas.save();

        canvas.rotate(hoursRotation, engine.displayCenterX, engine.displayCenterY);
        canvas.drawLine(engine.displayCenterX, engine.displayCenterY, engine.displayCenterX, engine.displayCenterY - 100, mHandPaint);

        canvas.rotate(minutesRotation - hoursRotation, engine.displayCenterX, engine.displayCenterY);
        canvas.drawLine(engine.displayCenterX, engine.displayCenterY, engine.displayCenterX, engine.displayCenterY - 150, mHandPaint);

        if(!engine.isAmbient){
            canvas.rotate(secondsRotation - minutesRotation, engine.displayCenterX, engine.displayCenterY);
            canvas.drawLine(engine.displayCenterX, engine.displayCenterY, engine.displayCenterX, engine.displayCenterY - 180, mHandPaint);
        }
        // restore the canvas' original orientation.
        canvas.restore();


    }*/


}
