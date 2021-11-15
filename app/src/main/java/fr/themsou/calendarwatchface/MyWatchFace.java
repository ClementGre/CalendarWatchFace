package fr.themsou.calendarwatchface;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn't
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class MyWatchFace extends CanvasWatchFaceService {

    // ADD connection :
    // adb devices
    // adb connect 192.168.192.5:5555

    private static final String TAG = "CalendarWatchFace";
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    @Override
    public Engine onCreateEngine(){
        return new Engine(this);
    }

    class Engine extends CanvasWatchFaceService.Engine {

        /* Handler to update the time once a second in interactive mode. */
        @SuppressLint("HandlerLeak")
        private final Handler mUpdateTimeHandler = new Handler() {
            @Override public void handleMessage(Message message) {
                if(R.id.message_update == message.what){
                    invalidate();
                    if(shouldTimerBeRunning()){
                        long timeMs = System.currentTimeMillis();
                        long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                        mUpdateTimeHandler.sendEmptyMessageDelayed(R.id.message_update, delayMs);
                    }
                }
            }
        };

        private boolean mRegisteredTimeZoneReceiver = false;
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                calendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };

        public Calendar calendar;

        public boolean isAmbient;
        public int displayWidth;
        public int displayHeight;
        public float displayCenterX;
        public float displayCenterY;

        public Typeface FONT_RUBIK;

        public Designer designer = new Designer(this);

        public final MyWatchFace myWatchFace;
        public Engine(MyWatchFace myWatchFace){
            this.myWatchFace = myWatchFace;
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////// CREATE - DESTROY //////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);


            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this).setAcceptsTapEvents(true).build());

            FONT_RUBIK = Typeface.createFromAsset(getAssets(),"fonts/rubik-regular.ttf");
            calendar = Calendar.getInstance();
            designer.setupPaints();

            Log.d(TAG, "----------------------------------------");
            Log.d(TAG, "      CALENDAR WATCH FACE CREATED");
            Log.d(TAG, "----------------------------------------");
        }

        @Override
        public void onDestroy(){
            mUpdateTimeHandler.removeMessages(R.id.message_update);
            super.onDestroy();
            Log.d(TAG, "----------------------------------------");
            Log.d(TAG, "     CALENDAR WATCH FACE DESTROYED");
            Log.d(TAG, "----------------------------------------");
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////// BASE ////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public void onTimeTick(){
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if(isAmbient != inAmbientMode){
                isAmbient = inAmbientMode;

                if(inAmbientMode){
                    designer.updatePaintsToAmbientMode();
                }else{
                    designer.updatePaintsToFullMode();
                }

                invalidate();

                if(inAmbientMode){
                    Log.d(TAG, "---------- AMBIENT MODE ----------");
                }else{
                    Log.d(TAG, "----------- FULL MODE ------------");
                }
            }

            /*
             * Whether the timer should be running depends on whether we're visible (as well as
             * whether we're in ambient mode), so we may need to start or stop the timer.
             */
            updateTimer();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            displayWidth = width;
            displayHeight = height;
            /*
             * Find the coordinates of the center point on the screen.
             * Ignore the window insets so that, on round watches
             * with a "chin", the watch face is centered on the entire screen,
             * not just the usable portion.
             */
            displayCenterX = displayWidth / 2f;
            displayCenterY = displayHeight / 2f;
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            calendar.setTimeInMillis(now);

            designer.draw(canvas);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if(visible){
                registerReceiver();
                // Update time zone in case it changed while we weren't visible.
                calendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }else{
                unregisterReceiver();
            }

            /*
             * Whether the timer should be running depends on whether we're visible
             * (as well as whether we're in ambient mode),
             * so we may need to start or stop the timer.
             */
            updateTimer();
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////// TAP EVENTS ///////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public void onTapCommand(@TapType int tapType, int x, int y, long eventTime){
            switch (tapType){
                case WatchFaceService.TAP_TYPE_TOUCH: // USER FIRST TOUCH SCREEN

                break;
                case WatchFaceService.TAP_TYPE_TOUCH_CANCEL: // DRAG : CANCEL

                break;
                case WatchFaceService.TAP_TYPE_TAP: // SINGLE TAP
                    designer.singleTap(x, y);
                break;
                default:
                    super.onTapCommand(tapType, x, y, eventTime);
                break;
            }
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////// METHODS //////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(R.id.message_update);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(R.id.message_update);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer
         * should only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }
    }
}
