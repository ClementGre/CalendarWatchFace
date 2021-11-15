package fr.themsou.calendarwatchface;


import java.util.Calendar;

public class Event {

    private final String name;
    private final long begin;
    private final long end;
    private final boolean allDay;

    public Event(String name, long begin, long end, boolean allDay) {
        this.name = name;
        this.begin = begin;
        this.end = end;
        this.allDay = allDay;
    }

    public long getBegin(){
        return begin;
    }
    public long getEnd(){
        return end;
    }


    public long getRemainingMinutesBeforeBegin(){
        return begin - System.currentTimeMillis()/1000/60;
    }
    public long getRemainingMinutesBeforeEnd(){
        return end - System.currentTimeMillis()/1000/60;
    }
    public long getBeginDateInDayMinutes(Calendar calendar){
        return getDateInDayMinutes(calendar) + getRemainingMinutesBeforeBegin();
    }
    public long getEndDateInDayMinutes(Calendar calendar){
        return getDateInDayMinutes(calendar) + getRemainingMinutesBeforeEnd();
    }

    private long getDateInDayMinutes(Calendar calendar){
        return calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE);
    }


    public long getDuration(){
        return end - begin;
    }
    public String getName() {
        return name;
    }
    public boolean isAllDay() {
        return allDay;
    }
    public static void getStringDate(){

    }

    public boolean isToday(Calendar calendar){
        long minBeforeBegin = getRemainingMinutesBeforeBegin();
        long minBeforeDayEnd = 60*24 - getDateInDayMinutes(calendar);

        return  minBeforeBegin < minBeforeDayEnd;
    }
}
