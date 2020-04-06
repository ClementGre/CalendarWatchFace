package fr.themsou.calendarwatchface;


import java.util.Calendar;

public class Event {

    private String name;
    private long begin;
    private long end;
    private boolean allDay;

    public Event(String name, long begin, long end, boolean allDay) {
        this.name = name;
        this.begin = begin;
        this.end = end;
        this.allDay = allDay;
    }

    public long getBegin(){
        return begin;
    }
    public long getSinceNowMinutesBegin(){
        return begin - System.currentTimeMillis()/1000/60;
    }
    public long getSinceDayMinuteBegin(Calendar calendar){
        return calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE) + getSinceNowMinutesBegin();
    }

    public long getEnd(){
        return end;
    }
    public long getSinceNowMinutesEnd(){
        return end - System.currentTimeMillis()/1000/60;
    }
    public long getSinceDayMinuteEnd(Calendar calendar){
        return calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE) + getSinceNowMinutesEnd();
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

}
