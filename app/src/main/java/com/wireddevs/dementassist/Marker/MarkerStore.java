package com.wireddevs.dementassist.Marker;

public class MarkerStore {
    public static final String TABLE_NAME = "markerslist";

    public static final String COLUMN_ITEM_NAME = "name";
    public static final String COLUMN_ITEM_V = "v";
    public static final String COLUMN_ITEM_V1 = "v1";
    public static final String COLUMN_ITEM_TYPE= "type";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ITEM_NAME + " TEXT,"
                    + COLUMN_ITEM_V+" REAL,"
                    + COLUMN_ITEM_V1+" REAL,"
                    + COLUMN_ITEM_TYPE+" INTEGER"
                    + ")";

    private String name;
    private double v;
    private double v1;
    private int type;

    public MarkerStore(String name, double v, double v1, int type){
        this.name=name;
        this.v=v;
        this.v1=v1;
        this.type=type;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setV(double v) {
        this.v = v;
    }

    public void setV1(double v1) {
        this.v1 = v1;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public double getV() {
        return v;
    }

    public double getV1() {
        return v1;
    }

    public int getType() {
        return type;
    }
}
