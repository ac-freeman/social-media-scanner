package com.acfreeman.socialmediascanner.social;

/**
 * Created by Andrew on 9/26/2017.
 */

public class SocialAdder {
    public SocialAdder(){

    }

    private String uri, type;
    public SocialAdder(String uri, String type){
        this.uri = uri;
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public String getUri(){
        return this.uri;
    }
}
