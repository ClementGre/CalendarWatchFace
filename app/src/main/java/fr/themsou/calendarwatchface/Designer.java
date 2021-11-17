package fr.themsou.calendarwatchface;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class Designer {

    private static final String TAG = "CalendarWatchFace";

    /*
    private Paint paintTicks = new Paint();
    private Paint paintSelectedTicks = new Paint();
    private Paint paintCurrentTick = new Paint();
    private Path pathTicks;
    private Path pathSelectedTicks;
    private Path pathCurrentTick;*/

    private final Paint paintBackground = new Paint();

    private final Paint paintDate = new Paint();
    private final Paint paintHour = new Paint();
    private final Paint paintHourDetails = new Paint();

    private final Paint paintEventNameBig = new Paint();
    private final Paint paintEventName = new Paint();
    private final Paint paintEventDetails = new Paint();


    private ArrayList<Event> events;
    private final MyWatchFace.Engine engine;
    private DateEvents dateEvents;
    public Designer(MyWatchFace.Engine engine){
        this.engine = engine;
    }

    void setupPaints(){
        paintBackground.setColor(Color.BLACK);

        // TICKS

        /*paintTicks = new Paint();
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
        paintCurrentTick.setAntiAlias(true);*/

        // Hour / Date

        paintDate.setTextSize(27);
        paintDate.setTypeface(engine.FONT_RUBIK);
        paintDate.setTextAlign(Paint.Align.CENTER);
        paintDate.setColor(Color.WHITE);
        paintDate.setStrokeWidth(1);

        paintHour.setTextSize(115);
        paintHour.setTextScaleX(0.7f);
        paintHour.setTypeface(engine.FONT_RUBIK);
        paintHour.setTextAlign(Paint.Align.CENTER);
        paintHour.setColor(Color.WHITE);
        paintHour.setStrokeWidth(1);

        paintHourDetails.setTextSize(40);
        paintHourDetails.setTypeface(engine.FONT_RUBIK);
        paintHourDetails.setTextAlign(Paint.Align.CENTER);
        paintHourDetails.setColor(Color.WHITE);
        paintHourDetails.setStrokeWidth(1);


        // EVENTS

        paintEventNameBig.setTextSize(27);
        paintEventNameBig.setTypeface(engine.FONT_RUBIK);
        paintEventNameBig.setTextAlign(Paint.Align.CENTER);
        paintEventNameBig.setColor(Color.rgb(43, 152, 206));
        paintEventNameBig.setUnderlineText(true);

        paintEventName.setTextSize(22);
        paintEventName.setTypeface(engine.FONT_RUBIK);
        paintEventName.setTextAlign(Paint.Align.CENTER);
        paintEventName.setColor(Color.rgb(43, 152, 206));

        paintEventDetails.setTextSize(20);
        paintEventDetails.setTypeface(engine.FONT_RUBIK);
        paintEventDetails.setTextAlign(Paint.Align.CENTER);
        paintEventDetails.setColor(Color.rgb(173, 173, 173));

        updatePaintsToFullMode();

        dateEvents = new DateEvents(new CallBack() {
            @Override public void call() { // MINUTES
                /*setupCurrentTickPath();*/
            }
        }, new CallBack() {
            @Override public void call() { // HOUR
                updateCalendar();
            }
        });
    }

    void updatePaintsToFullMode() {

        // Enable ShadowLayer and AntiAliasing
        paintDate.setAntiAlias(true);
        paintHour.setAntiAlias(true);
        paintHourDetails.setAntiAlias(true);

        paintEventNameBig.setAntiAlias(true);
        paintEventName.setAntiAlias(true);
        paintEventDetails.setAntiAlias(true);

        paintDate.setShadowLayer(1, 1, 1, paintDate.getColor());
        paintHour.setShadowLayer(1, 1, 1, paintHour.getColor());
        paintHourDetails.setShadowLayer(1, 1, 1, paintHourDetails.getColor());

        paintEventNameBig.setShadowLayer(1, 1, 1, paintEventNameBig.getColor());
        paintEventName.setShadowLayer(1, 1, 1, paintEventName.getColor());
        paintEventDetails.setShadowLayer(1, 1, 1, paintEventDetails.getColor());

        // Switch to fill mode
        paintDate.setStyle(Paint.Style.FILL);
        paintHour.setStyle(Paint.Style.FILL);
        paintHourDetails.setStyle(Paint.Style.FILL);
        paintHourDetails.setColor(Color.WHITE);

        CalendarReader.resetPermissionData();
    }

    void updatePaintsToAmbientMode() {

        // Disable ShadowLayer and AntiAliasing

        paintDate.setAntiAlias(false);
        paintHour.setAntiAlias(false);
        paintHourDetails.setAntiAlias(false);

        paintEventNameBig.setAntiAlias(false);
        paintEventName.setAntiAlias(false);
        paintEventDetails.setAntiAlias(false);

        paintDate.clearShadowLayer();
        paintHour.clearShadowLayer();
        paintHourDetails.clearShadowLayer();
        paintEventNameBig.clearShadowLayer();
        paintEventName.clearShadowLayer();
        paintEventDetails.clearShadowLayer();

        // Switch to stroke mode
        paintDate.setStyle(Paint.Style.STROKE);
        paintHour.setStyle(Paint.Style.STROKE);
        paintHourDetails.setStyle(Paint.Style.STROKE);
        paintHourDetails.setColor(Color.rgb(43, 152, 206));


    }

    void draw(Canvas canvas) {

        /*if(pathTicks == null) setupPaths();*/
        if(events == null) updateCalendar();
        /*if(pathCurrentTick == null) setupCurrentTickPath();*/

        dateEvents.tick(engine.calendar);
        canvas.drawRect(0, 0, engine.displayWidth, engine.displayHeight, paintBackground);

        ArrayList<Event> currentEvents = CalendarReader.getCurrentEvents(events, 3);
        ArrayList<Event> nextEvents = CalendarReader.getNextEvents(events, 3 - currentEvents.size());
        ArrayList<Event> dayEvents = CalendarReader.getCurrentFullDayEvent(engine.calendar, events, 2);

        canvas.drawText(getTime(), engine.displayCenterX, engine.displayCenterY, paintHour);

        if(!engine.isAmbient){

            canvas.drawText(getFullDate(), engine.displayCenterX, 103, paintDate);
            canvas.drawText(":" + getSeconds(), engine.displayCenterX + 156, engine.displayCenterY, paintHourDetails);

            int y = 41;
            for(Event event : dayEvents){
                canvas.drawText("- " + event.getName() + " -", engine.displayCenterX, y, paintEventName);
                y += 26;
            }

            y = 241;
            for(Event event : currentEvents){

                canvas.drawText("- " + event.getName() + " -", engine.displayCenterX, y, paintEventNameBig);
                y += 22;
                canvas.drawText(getTime(event.getBeginDateInDayMinutes(engine.calendar)) + " - " + getTime(event.getEndDateInDayMinutes(engine.calendar)) + " | " + getTime(event.getRemainingMinutesBeforeEnd(), false)
                        , engine.displayCenterX, y, paintEventDetails);
                y += 35;
            }
            for(Event event : nextEvents){

                canvas.drawText("- " + event.getName() + " -", engine.displayCenterX, y, paintEventName);
                y += 22;
                canvas.drawText(getTime(event.getBeginDateInDayMinutes(engine.calendar)) + " - " + getTime(event.getBeginDateInDayMinutes(engine.calendar)) + " | " + getTime(event.getRemainingMinutesBeforeEnd(), false)
                        , engine.displayCenterX, y, paintEventDetails);
                y += 28;
            }

        }else{
            canvas.drawText(getShortDate(), engine.displayCenterX, 103, paintDate);

            int y = 41;
            for(Event event : dayEvents){
                canvas.drawText("- " + event.getName() + " -", engine.displayCenterX, y, paintEventName);
                y += 26;
            }

            y = 241;
            for(Event event : currentEvents){
                if(y == 241) canvas.drawText(getTime(event.getRemainingMinutesBeforeEnd(), false), engine.displayCenterX + 156, engine.displayCenterY, paintHourDetails);

                canvas.drawText("- " + event.getName() + " -", engine.displayCenterX, y, paintEventNameBig);
                y += 22;
                canvas.drawText(getTime(event.getBeginDateInDayMinutes(engine.calendar)) + " - " + getTime(event.getEndDateInDayMinutes(engine.calendar)) + " | " + getTime(event.getRemainingMinutesBeforeEnd(), false)
                        , engine.displayCenterX, y, paintEventDetails);
                y += 35;
            }
            for(Event event : nextEvents){
                if(y == 241) canvas.drawText(getTime(event.getRemainingMinutesBeforeBegin(), false), engine.displayCenterX + 156, engine.displayCenterY, paintHourDetails);

                canvas.drawText("- " + event.getName() + " -", engine.displayCenterX, y, paintEventName);
                y += 22;
                canvas.drawText(getTime(event.getBeginDateInDayMinutes(engine.calendar)) + " - " + getTime(event.getBeginDateInDayMinutes(engine.calendar)) + " | " + getTime(event.getRemainingMinutesBeforeEnd(), false)
                        , engine.displayCenterX, y, paintEventDetails);
                y += 28;
            }

        }

        // TICKS

        /*canvas.drawPath(pathTicks, paintTicks);
        canvas.drawPath(pathSelectedTicks, paintSelectedTicks);
        canvas.drawPath(pathCurrentTick, paintCurrentTick);*/
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
                Log.d(TAG, "Application calendrier introuvable. Packages disponibles : ");

                final PackageManager pm = engine.myWatchFace.getPackageManager();
                List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

                for(ApplicationInfo packageInfo : packages) {
                    Log.d(TAG, "Installed package: " + packageInfo.packageName);
                    Log.d(TAG, "Source dir: " + packageInfo.sourceDir);
                    Log.d(TAG, "Start Activity: " + pm.getLaunchIntentForPackage(packageInfo.packageName));
                    Log.d(TAG, " ");
                }
            }
        }
    }

    private void updateCalendar() {
        Log.d(TAG, "updateCalendar");

        if(CalendarReader.checkPermission(engine)){
            events = CalendarReader.get24HEvents(engine.myWatchFace, engine.calendar);
            /*setupSelectedTicksPath();*/
        }

    }

    /*private void setupCurrentTickPath(){

        pathCurrentTick = new Path();
        pathCurrentTick.setFillType(Path.FillType.WINDING);

        int tickIndex = (int) ((engine.calendar.get(Calendar.HOUR_OF_DAY)*60 + engine.calendar.get(Calendar.MINUTE)) / 15f);

        float tickRadius = engine.displayCenterX - 18;
        float tickRot = (float) -(Math.PI*2 / 96*tickIndex);
        float x = (float) Math.sin(tickRot) * tickRadius;
        float y = (float) Math.cos(tickRot) * tickRadius;

        pathCurrentTick.addCircle(engine.displayCenterX + x, engine.displayCenterY + y, 4, Path.Direction.CW);

    }*/

    /*private void setupSelectedTicksPath(){
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
    }*/

    /*private void setupPaths(){
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

    }*/

    private String getTime(long mn){
        return getTime(mn, true);
    }
    private String getTime(long mn, boolean hourZero){
        int hour = (int) (mn) / 60;
        int minutes = (int) (mn) % 60;

        String stringHour = ((hour < 10 && hourZero) ? "0" : "") + hour;
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
        String stringMonth = ((month < 10) ? "0" : "") + (month+1);

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


}
