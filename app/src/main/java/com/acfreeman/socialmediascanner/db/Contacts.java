package com.acfreeman.socialmediascanner.db;

/**
 * Created by jianziyu on 2017/9/19.
 */

public class Contacts {
    private long id;
    private String name;
    public Contacts()
    {
    }
    public Contacts(int id,String name)
    {
        this.id=id;
        this.name=name;

    }
    public Contacts(String name){
        this.name = name;
    }
    public void setId(long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
