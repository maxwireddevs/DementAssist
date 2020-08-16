package com.wireddevs.dementassist.Contacts;

public class Contacts {

    public static final String TABLE_NAME = "contactsname";

    public static final String COLUMN_ITEM_NAME = "name";
    public static final String COLUMN_ITEM_RELATIONS = "relations";
    public static final String COLUMN_ITEM_PHONENUM = "phonenum";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ITEM_NAME + " TEXT,"
                    + COLUMN_ITEM_RELATIONS+" TEXT,"
                    + COLUMN_ITEM_PHONENUM+" TEXT"
                    + ")";

    private String name;
    private String relations;
    private String phonenum;

    public Contacts(String name, String relations, String phonenum){
        this.name=name;
        this.relations=relations;
        this.phonenum=phonenum;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public void setRelations(String relations) {
        this.relations = relations;
    }

    public String getName() {
        return name;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public String getRelations() {
        return relations;
    }
}
