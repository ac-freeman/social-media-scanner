package com.acfreeman.socialmediascanner;

/**
 * Created by jianziyu on 2017/9/19.
 */

public class Emails {
    private int id;
    private String email;
    private String type;
    public Emails()
    {
    }
    public Emails(int id,String email,String type)
    {
        this.id=id;
        this.email=email;
        this.type=type;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String email) {
        this.email = email;
    }

    public void setAddress(String type) {
        this.type = type;
    }
    public int getId() {
        return id;
    }
    public String getAddress() {
        return type;
    }
    public String getName() {
        return email;
    }
}
