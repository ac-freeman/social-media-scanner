package com.acfreeman.socialmediascanner.db;

/**
 * Created by jianziyu on 2017/9/19.
 */

public class Contacts {
    private int id;
    private String name;
    public Contacts()
    {
    }
    public Contacts(int id,String name)
    {
        this.id=id;
        this.name=name;

    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
