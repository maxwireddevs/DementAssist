package com.wireddevs.dementassist.Alarm;

public class Alarm {

    public static final String TABLE_NAME = "alarmslist";

    public static final String COLUMN_ITEM_NAME = "name";
    public static final String COLUMN_ITEM_TIMESTAMP = "timestamp";
    public static final String COLUMN_ITEM_CODE = "requestcode";
    public static final String COLUMN_ITEM_INTERVAL = "intervalcode";
    public static final String COLUMN_ITEM_WEEKLY = "daysinweek";
    public static final String COLUMN_ITEM_LOGO = "alarmlogo";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ITEM_NAME + " TEXT,"
                    + COLUMN_ITEM_TIMESTAMP + " INTEGER,"
                    + COLUMN_ITEM_CODE + " INTEGER,"
                    + COLUMN_ITEM_INTERVAL + " INTEGER,"
                    + COLUMN_ITEM_WEEKLY + " TEXT,"
                    + COLUMN_ITEM_LOGO +" INTEGER"
                    + ")";

    private String name;
    private long timestamp;
    private int code;
    private long interval;
    private String daysinweek;
    private int logo;

    public Alarm(){

    }

    public Alarm(String name, long timestamp, int code, long interval, String daysinweek,int logo){
        this.name=name;
        this.timestamp=timestamp;
        this.code=code;
        this.interval=interval;
        this.daysinweek=daysinweek;
        this.logo=logo;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getCode() {
        return code;
    }

    public long getInterval() {
        return interval;
    }

    public String getDaysinweek() {
        return daysinweek;
    }

    public int getLogo() {
        return logo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void setDaysinweek(String daysinweek) {
        this.daysinweek = daysinweek;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }
}
