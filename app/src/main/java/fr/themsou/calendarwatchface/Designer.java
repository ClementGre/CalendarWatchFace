package fr.themsou.calendarwatchface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.util.Calendar;
import java.util.Random;

class Designer {

    private Paint paintTop;
    private Paint paintBottom;

    private Paint paintTicks;
    private Paint paintSelectedTicks;
    private Path pathTicks;
    private Path pathSelectedTicks;

    private Paint paintHour;
    private Paint paintSeconds;
    private Paint paintDate;

    private MyWatchFace.Engine engine;
    Designer(MyWatchFace.Engine engine) {
        this.engine = engine;
    }

    void setupPaints(){

        paintTop = new Paint();
        paintBottom = new Paint();


        // TICKS

        paintTicks = new Paint();
        paintTicks.setColor(Color.rgb(150, 150 ,150));
        paintTicks.setStyle(Paint.Style.FILL);

        paintSelectedTicks = new Paint();
        paintSelectedTicks.setColor(Color.rgb(255, 141, 26));
        paintSelectedTicks.setStyle(Paint.Style.FILL);

        // TEXT

        paintHour = new Paint();
        paintHour.setTextSize(140);
        paintHour.setTextScaleX(0.7f);
        paintHour.setTypeface(engine.FONT_TEXTMEONE_REGULAR);
        paintHour.setColor(Color.WHITE);
        paintHour.setTextAlign(Paint.Align.CENTER);

        paintSeconds = new Paint();
        paintSeconds.setTextSize(50);
        paintSeconds.setTypeface(engine.FONT_TEXTMEONE_REGULAR);
        paintSeconds.setColor(Color.WHITE);

        paintDate = new Paint();
        paintDate.setTextSize(25);
        paintDate.setTypeface(engine.FONT_DIN_BOLD);
        paintDate.setColor(Color.WHITE);
        paintDate.setTextAlign(Paint.Align.CENTER);

        updatePaintsToFullMode();
    }

    void updatePaintsToFullMode(){

        // Change Colors

        paintTop.setColor(Color.BLACK);
        paintBottom.setColor(Color.rgb(42, 123, 155));

        // Enable ShadowLayer and AntiAliasing

        paintTicks.setAntiAlias(true);
        paintTicks.setShadowLayer(4, 0, 0, paintTicks.getColor());

        paintSelectedTicks.setAntiAlias(true);
        paintSelectedTicks.setShadowLayer(4, 0, 0, paintSelectedTicks.getColor());

        paintHour.setAntiAlias(true);
        paintHour.setShadowLayer(4, 2, 2, Color.WHITE);
        paintSeconds.setAntiAlias(true);
        paintSeconds.setShadowLayer(4, 2, 2, Color.WHITE);

        paintDate.setAntiAlias(true);
        paintDate.setShadowLayer(4, 2, 2, Color.WHITE);

    }
    void updatePaintsToAmbientMode(){

        // Change Colors

        paintTop.setColor(Color.BLACK);
        paintBottom.setColor(Color.BLACK);

        // Disable ShadowLayer and AntiAliasing

        paintTicks.clearShadowLayer();
        paintSelectedTicks.clearShadowLayer();

        //paintHour.setAntiAlias(false);
        paintHour.clearShadowLayer();

        //paintDate.setAntiAlias(false);
        paintDate.clearShadowLayer();

    }
    private void setupPaths(){

        pathTicks = new Path();
        pathTicks.setFillType(Path.FillType.WINDING);
        pathSelectedTicks = new Path();
        pathSelectedTicks.setFillType(Path.FillType.WINDING);
        float tickRadius = engine.displayCenterX - 9;
        boolean last = false;
        for(int tickIndex = 0; tickIndex < 96; tickIndex++){
            float tickRot = (float) (Math.PI * 2 / 96 * tickIndex);

            float x = (float) Math.sin(tickRot) * tickRadius;
            float y = (float) Math.cos(tickRot) * tickRadius;

            boolean selected;
            if(last){
                selected = new Random().nextInt(4) != 1;
            }else{
                selected = new Random().nextInt(8) == 1;
            }
            last = selected;

            int radius = (tickIndex % 4 == 0) ? 2 : 1;

            if(selected){
                pathSelectedTicks.addCircle(engine.displayCenterX + x, engine.displayCenterY + y, radius, Path.Direction.CW);
            }else{
                pathTicks.addCircle(engine.displayCenterX + x, engine.displayCenterY + y, radius, Path.Direction.CW);
            }

    }

    }

    void draw(Canvas canvas){


        canvas.drawRect(0, 0, engine.displayWidth, engine.displayCenterY, paintTop);
        canvas.drawRect(0, engine.displayCenterY, engine.displayWidth, engine.displayHeight, paintBottom);

        canvas.drawText(getTime(),  engine.displayCenterX, engine.displayCenterY-20, paintHour);
        if(!engine.isAmbient){

            canvas.drawText(":" + getSeconds(),  engine.displayCenterX + 110, engine.displayCenterY-20, paintSeconds);

            canvas.drawText(getFullDate(),  engine.displayCenterX, engine.displayCenterY-130, paintDate);
        }else{
            canvas.drawText(getShortDate(),  engine.displayCenterX, engine.displayCenterY-130, paintDate);

        }
        if(pathTicks == null){
            Log.d("MyWatchFace", "Setup paths (null)");
            setupPaths();
        }else if(pathTicks.isEmpty()){
            Log.d("MyWatchFace", "Setup paths (empty)");
            setupPaths();
        }

        canvas.drawPath(pathTicks, paintTicks);
        canvas.drawPath(pathSelectedTicks, paintSelectedTicks);

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
