package fr.themsou.calendarwatchface;


import java.util.Calendar;

public class Event {

    private String name;
    private long begin;
    private long end;
    private boolean allDay;

    Event(String name, long begin, long end, boolean allDay) {
        this.name = name;
        this.begin = begin;
        this.end = end;
        this.allDay = allDay;
    }

    long getBegin(){
        return begin;
    }
    long getSinceNowMinutesBegin(){
        return begin - System.currentTimeMillis()/1000/60;
    }
    long getSinceDayMinuteBegin(Calendar calendar){
        return calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE) + getSinceNowMinutesBegin();
    }

    long getEnd(){
        return end;
    }
    long getSinceNowMinutesEnd(){
        return end - System.currentTimeMillis()/1000/60;
    }
    long getSinceDayMinuteEnd(Calendar calendar){
        return calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE) + getSinceNowMinutesEnd();
    }

    long getDuration(){
        return end - begin;
    }

    String getName() {
        return name;
    }
    boolean isAllDay() {
        return allDay;
    }

    public static void getStringDate(){

    }

}
