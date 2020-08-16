package com.wireddevs.dementassist.Utils;

public class TimeLongHelper {

    long timestamp;
    long seconds;
    long minutes;
    long hours;
    long days;

    public TimeLongHelper(long current,long stamp){
        this.timestamp=stamp-current;
        if(timestamp<=0){
            this.seconds=this.minutes=this.hours=this.days=0;
        }
        else{
            this.seconds = timestamp / 1000;
            this.minutes = seconds / 60;
            this.hours = minutes / 60;
            this.days = hours / 24;
        }
    }

    public String getTypeString(){
        if(days!=0)
            return "days";
        else if(hours!=0)
            return "hours";
        else if(minutes!=0)
            return "minutes";
        else
            return "seconds";
    }

    public long getValue(){
        if(days!=0)
            return days;
        else if(hours!=0)
            return hours;
        else if(minutes!=0)
            return minutes;
        else
            return seconds;
    }

    public int getTypeInt(){
        if(days!=0)
            return 2;
        else if(hours!=0)
            return 1;
        else
            return 0;
    }
}
