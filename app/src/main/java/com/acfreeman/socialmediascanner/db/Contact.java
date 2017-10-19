package com.acfreeman.socialmediascanner.db;

/**
 * Created by jianziyu on 2017/9/19.
 */

public class Contact {
    private long id;
    private String name;
    public Contact()
    {
    }
    public Contact(int id, String name)
    {
        this.id=id;
        this.name=name;

    }
    public Contact(String name){
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
