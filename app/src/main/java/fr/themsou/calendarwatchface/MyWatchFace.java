package fr.themsou.calendarwatchface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import java.lang.ref.WeakReference;
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

    /*
     * Updates rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    private Calendar calendar;

    private boolean isAmbient;
    private boolean isLowBitAmbient;
    private boolean isBurnInProtection;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        public EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {


        // Lors de l'initialisation de la WatchFace
        @Override
        public void onCreate(SurfaceHolder holder){
            super.onCreate(holder);
            Log.d("MyWatchFace", "creating watchFace...");

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this).setAcceptsTapEvents(true).build());

            calendar = Calendar.getInstance();

            Log.d("MyWatchFace", "created !");

        }

        @Override
        public void onDestroy(){
            super.onDestroy();
            Log.d("MyWatchFace", "destroyed !");

        }

        // Lorsque des paramètres de la montre changent
        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            Log.d("MyWatchFace", "onPropertyChanged");
            isLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            isBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            Log.d("MyWatchFace", "onTimeTick -> invalidate();");
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            Log.d("MyWatchFace", "AmbientMode changed to " + inAmbientMode);

            isAmbient = inAmbientMode;
            updateWatchHandStyle();

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();

            Log.d("MyWatchFace", "finish ambient mode switch");
        }



        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged(interruptionFilter);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }

        private void initGrayBackgroundBitmap() {
        }

        /**
         * Captures tap event (and tap type). The {@link WatchFaceService#TAP_TYPE_TAP} case can be
         * used for implementing specific logic to handle the gesture.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            calendar.setTimeInMillis(now);

            drawBackground(canvas);
            drawWatchFace(canvas);
        }

        private void initializeBackground(){
        }

        private void initializeWatchFace(){

        }
        private void updateWatchHandStyle() {
            if(isInAmbientMode()){

            }else{

            }
        }

        private void drawBackground(Canvas canvas) {

            if (isInAmbientMode() && (isLowBitAmbient || isBurnInProtection)){
                canvas.drawColor(Color.BLACK);
            }else if(isInAmbientMode()){

            }else{

            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();
                /* Update time zone in case it changed while we weren't visible. */
                calendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }

        private void drawWatchFace(Canvas canvas) {

        }



        private void registerReceiver() {
        }

        private void unregisterReceiver() {
        }

        /**
         * Starts/stops the {#mUpdateTimeHandler} timer based on the state of the watch face.
         */
        private void updateTimer() {
        }

        /**
         * Returns whether the {#mUpdateTimeHandler} timer should be running. The timer
         * should only run in active mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {

        }
    }
}
