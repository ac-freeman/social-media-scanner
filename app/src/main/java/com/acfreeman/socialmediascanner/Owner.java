package com.acfreeman.socialmediascanner;

/**
 * Created by yzj_0 on 2017/9/19.
 */

public class Owner {
    private int id;
    private String name;

    public Owner()
    {
    }
    public Owner(int id,String name,String address)
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
