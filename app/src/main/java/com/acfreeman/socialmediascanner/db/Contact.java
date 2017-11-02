package com.acfreeman.socialmediascanner.db;

import android.graphics.Bitmap;

/**
 * Created by jianziyu on 2017/9/19.
 */

public class Contact {
    private long id;
    private String name;
    private byte[] image;
    private Bitmap bitmap;
    public Contact()
    {
    }
    public Contact(int id, String name)
    {
        this.id=id;
        this.name=name;

    }

    public Contact(int id, String name, byte[] image){
        this.id=id;
        this.name=name;
        this.image=image;

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
    public void setImage(byte[] image) {this.image = image;}
    public void setBitmap(Bitmap bitmap) {this.bitmap = bitmap;}


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] getImage() {return image;}

    public Bitmap getBitmap() {return bitmap;}
}
