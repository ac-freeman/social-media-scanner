package com.acfreeman.socialmediascanner.db;

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
    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }
    public int getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public String getEmail() {
        return email;
    }
}
