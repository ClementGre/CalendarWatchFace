package fr.themsou.calendarwatchface;

import java.util.Calendar;

class DateEvents{

    private boolean hasCalledHour = false;

    private final CallBack minute;
    private final CallBack hour;

    DateEvents(CallBack minute, CallBack hour) {
        this.minute = minute;
        this.hour = hour;
    }

    void tick(Calendar calendar){

        if(calendar.get(Calendar.MINUTE) == 0){
            if(!hasCalledHour){
                this.hour.call();
                hasCalledHour = true;
            }
        }else if(calendar.get(Calendar.MINUTE) == 1){
            if(hasCalledHour) hasCalledHour = false;

        }

        if(calendar.get(Calendar.SECOND) == 0){
            this.minute.call();
        }



    }

}
