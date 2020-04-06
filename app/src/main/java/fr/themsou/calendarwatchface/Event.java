package fr.themsou.calendarwatchface;

public class Event {

    private String name;
    private int begin;
    private int end;
    private boolean allDay;

    public Event(String name, int begin, int end, boolean allDay) {
        this.name = name;
        this.begin = begin;
        this.end = end;
        this.allDay = allDay;
    }

    public int getBegin() {
        return begin;
    }
    public int getDayMinuteBegin(){
        return 0;
    }

    public int getEnd() {
        return end;
    }
    public int getDayMinuteEnd(){
        return 0;
    }

    public int getMinuteDuration(){
        return (end-begin)/1000;
    }

    public String getName() {
        return name;
    }
    public boolean isAllDay() {
        return allDay;
    }

}
