package fr.themsou.calendarwatchface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import java.util.Calendar;

class Designer {

    private Paint paintClear;

    private Paint paintTicks;

    private Paint paintHour;
    private Paint paintDate;

    private boolean isBaseDraw = false;

    private MyWatchFace.Engine engine;
    Designer(MyWatchFace.Engine engine) {
        this.engine = engine;
    }

    void setupPaints(){

        paintClear = new Paint();
        paintClear.setColor(Color.BLACK);

        paintTicks = new Paint();
        paintTicks.setColor(Color.rgb(200, 200, 200));
        paintTicks.setStrokeWidth(5);
        paintTicks.setStyle(Paint.Style.FILL_AND_STROKE);
        paintTicks.setShadowLayer(6, 0, 0, Color.BLACK);

        paintHour = new Paint();
        paintHour.setTextSize(130);
        paintHour.setTypeface(engine.FONT_TEXTMEONE_REGULAR);
        paintHour.setColor(Color.WHITE);
        paintHour.setTextAlign(Paint.Align.CENTER);


        paintDate = new Paint();
        paintHour.setTextSize(25);
        paintHour.setTypeface(engine.FONT_DIN_BOLD);
        paintDate.setColor(Color.WHITE);
        paintDate.setTextAlign(Paint.Align.CENTER);


        updatePaintsToFullMode();
    }

    void updatePaintsToFullMode(){
        // Enable ShadowLayer and AntiAliasing

        paintTicks.setAntiAlias(true);
        //paintTicks.setShadowLayer(6, 0, 0, Color.BLACK);

        paintHour.setAntiAlias(true);
        //paintHour.setShadowLayer(6, 0, 0, Color.BLACK);

        paintDate.setAntiAlias(true);
        //paintDate.setShadowLayer(6, 0, 0, Color.BLACK);

    }
    void updatePaintsToAmbientMode(){
        // Disable ShadowLayer and AntiAliasing

        //paintTicks.setAntiAlias(false);
        paintTicks.clearShadowLayer();

        paintHour.setAntiAlias(false);
        paintHour.clearShadowLayer();

        paintDate.setAntiAlias(false);
        paintDate.clearShadowLayer();

    }

    void invalidateBase(){
        isBaseDraw = false;
    }
    private void drawBase(Canvas canvas){

        float tickRadius = engine.displayCenterX - 6;
        for(int tickIndex = 0; tickIndex < 96; tickIndex++){
            float tickRot = (float) (tickIndex * Math.PI * 2 / 96);

            float x = (float) Math.sin(tickRot) * tickRadius;
            float y = (float) -Math.cos(tickRot) * tickRadius;

            if(tickIndex % 4 == 0){
                canvas.drawCircle(engine.displayCenterX + x,  engine.displayCenterY + y, 2, paintTicks);
            }else{
                canvas.drawCircle(engine.displayCenterX + x,  engine.displayCenterY + y, 1, paintTicks);
            }
        }

    }

    void draw(Canvas canvas){

        if(!isBaseDraw){
            paintClear.setColor(Color.BLACK);
            canvas.drawRect(0, 0, engine.displayWidth, engine.displayHeight, paintClear);
            drawBase(canvas);
        }else{
            paintClear.setColor(Color.GRAY);
            canvas.drawRect(40, 40, engine.displayWidth-80, engine.displayHeight-80, paintClear);
        }

        if(!engine.isAmbient){

            canvas.drawText(getTime()+":"+getSeconds(),  engine.displayCenterX, engine.displayCenterY-engine.displayCenterY/2, paintHour);
            canvas.drawText(getFullDate(),  engine.displayCenterX, engine.displayCenterY, paintDate);

        }else{

            canvas.drawText(getTime(),  engine.displayCenterX, engine.displayCenterY-engine.displayCenterY/2, paintHour);
            canvas.drawText(getShortDate(),  engine.displayCenterX, engine.displayCenterY, paintDate);

        }



    }

    private String getTime(){
        int hour = engine.calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = engine.calendar.get(Calendar.HOUR_OF_DAY);

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
