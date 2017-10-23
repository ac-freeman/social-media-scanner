package com.acfreeman.socialmediascanner.dbAzure;

/**
 * Created by jianziyu on 2017/9/19.
 */

public class Email {
    private long id;
    private String email;
    private String type;
    public Email()
    {
    }
    public Email(Long id, String email, String type)
    {
        this.id = id;
        this.email=email;
        this.type=type;
    }
    public void setId(long id) {
        this.id = id;
    }
    public void setEmail(String email) {
        this.email = email;
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
    public String getEmail() {
        return email;
    }
}
