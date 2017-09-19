package com.acfreeman.socialmediascanner;

/**
 * Created by jianziyu on 2017/9/19.
 */

public class Social {
    private int id;
    private String type;
    private String username;
    public Social()
    {
    }
    public Social(int id,String type,String username)
    {
        this.id=id;
        this.type=type;
        this.username=username;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String type) {
        this.type = type;
    }

    public void setAddress(String username) {
        this.username = username;
    }
    public int getId() {
        return id;
    }
    public String getAddress() {
        return username;
    }
    public String getName() {
        return type;
    }
}
