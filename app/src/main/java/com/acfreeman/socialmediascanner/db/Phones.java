package com.acfreeman.socialmediascanner.db;

/**
 * Created by jianziyu on 2017/9/19.
 */

public class Phones {
    private long id;
    private int number;
    private String type;
    public Phones()
    {
    }
    public Phones(long id,int number,String type)
    {
        this.id=id;
        this.number=number;
        this.type=type;
    }
    public void setId(long id) {
        this.id = id;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }
    public long getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public int getNumber() {
        return number;
    }
}
