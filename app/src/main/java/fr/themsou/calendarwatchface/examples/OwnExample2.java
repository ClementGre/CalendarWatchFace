package fr.themsou.calendarwatchface.examples;

import android.util.DisplayMetrics;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

public class OwnExample2 extends CanvasWatchFaceService {
    private DisplayMetrics displayMetrics;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {

        private Calendar mCalendar;

        private Typeface din_light;
        private Typeface din_bold;

        private Paint mTimePaint;
        private Paint mDatePaint;
        private Paint mRectPaint;
        private Paint mBackPaint;
        private Paint mnPaint;

        private Path mnPath = new Path();

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            displayMetrics = getResources().getDisplayMetrics();

            din_light = Typeface.createFromAsset(getAssets(), "font/din_light.ttf");
            din_bold = Typeface.createFromAsset(getAssets(), "font/din_bold.ttf");

            setWatchFaceStyle(new WatchFaceStyle.Builder(OwnExample2.this)
                    .setAcceptsTapEvents(true)
                    .build());

            mCalendar = Calendar.getInstance();

            mTimePaint = new Paint();
            mTimePaint.setTextSize(130);
            mTimePaint.setTextScaleX(0.8f);
            //mTimePaint.setStyle(Paint.Style.FILL);
            mTimePaint.setTypeface(din_light);
            mTimePaint.setColor(Color.WHITE);
            mTimePaint.setTextAlign(Paint.Align.CENTER);
            mTimePaint.setAntiAlias(true);

            mDatePaint = new Paint();
            mDatePaint.setTextSize(25);
            mDatePaint.setStyle(Paint.Style.FILL);
            mDatePaint.setTypeface(din_bold);
            mDatePaint.setColor(Color.GRAY);
            mDatePaint.setTextAlign(Paint.Align.CENTER);
            mDatePaint.setAntiAlias(true);

            mRectPaint = new Paint();
            mRectPaint.setColor(Color.rgb(14, 121, 72));
            mRectPaint.setStyle(Paint.Style.FILL);
            mRectPaint.setAntiAlias(true);

            mBackPaint = new Paint();
            mBackPaint.setColor(Color.BLACK);
            mBackPaint.setStyle(Paint.Style.FILL);

            mnPaint = new Paint();
            mnPaint.setColor(Color.GRAY);
            mnPaint.setStyle(Paint.Style.FILL);
            mnPaint.setAntiAlias(true);

        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);


            //System.out.println(getServerStatus());

            String hours = mCalendar.getTime().getHours() + "";
            String minutes = mCalendar.getTime().getMinutes() + "";

            String day = mCalendar.getTime().getDay() + "";
            String date = mCalendar.getTime().getDate() + "";
            String mounth = mCalendar.getTime().getMonth() + "";

            if(mCalendar.getTime().getHours() <= 9) hours = 0 + hours;
            if(mCalendar.getTime().getMinutes() <= 9) minutes = 0 + minutes;
            day = getDay(Integer.parseInt(day)); mounth = getMounth(Integer.parseInt(mounth));

            canvas.drawRect(0, 0, 400, 400, mBackPaint);
            canvas.drawRect(0, 200, 400, 400, mRectPaint);

            canvas.drawText(hours + ":" + minutes, 200, 150, mTimePaint);
            canvas.drawText(day + " " + date + " " + mounth, 200, 180, mDatePaint);

            mnPath.set(createMinutesIndicators(200, 200, 193));
            canvas.drawPath(mnPath, mnPaint);


        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

        }
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            if(inAmbientMode){
                mRectPaint.setColor(Color.BLACK);

                mTimePaint.setStyle(Paint.Style.STROKE);
                mDatePaint.setStyle(Paint.Style.STROKE);
                mnPaint.setStyle(Paint.Style.STROKE);
            }else{

                mRectPaint.setColor(Color.rgb(14, 121, 72));

                mTimePaint.setStyle(Paint.Style.FILL);
                mDatePaint.setStyle(Paint.Style.FILL);
                mnPaint.setStyle(Paint.Style.FILL);
            }

        }
        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();

        }

        private Path createMinutesIndicators(float centerX, float centerY, float radius) {
            Path path = new Path();

            double angleRadians;
            for(int i = 0; i < 96; i++) {
                angleRadians = Math.PI * 2 / 96 * i;
                path.addCircle((float) (centerX + radius * Math.cos(angleRadians)), (float) (centerY + radius * Math.sin(angleRadians)), dpToPx(i % 8 == 0 ? 3f : 1.5f), Path.Direction.CW);
            }
            return path;
        }

        private float dpToPx(float dp) {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        }

        public String getMounth(int mounth){

            if(mounth == 0) return "Janvier"; else if(mounth == 1) return "Février"; else if(mounth == 2) return "Mars";
            else if(mounth == 3) return "Avril"; else if(mounth == 4) return "Mai"; else if(mounth == 5) return "Juin";
            else if(mounth == 6) return "Juillet"; else if(mounth == 7) return "Août"; else if(mounth == 8) return "Septembre";
            else if(mounth == 9) return "Octobre"; else if(mounth == 10) return "Novembre"; else if(mounth == 11) return "Décembre";
            return null;
        }
        public String getDay(int day){

            if(day == 1) return "Lundi"; else if(day == 2) return "Mardi"; else if(day == 3) return "Mercredi";
            else if(day == 4) return "Jeudi"; else if(day == 5) return "Vendredi";
            else if(day == 6) return "Samedi"; else if(day == 0) return "Dimanche";
            return null;
        }
        public String getServerStatus(){

            try{
                URL url = new URL("https://tntgun.fr/include/acceuil.inc.php");
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                return br.readLine();
            }catch(IOException e){
                e.printStackTrace();
            }

            return "Erreur";
        }

    }
}
