package com.example.student.snaptrash;

/**
 * Created by Student on 5/26/2016.
 */
public class SentPic {
    private boolean viewed;
    private String fromUser;
    private String toUser;
    private String imageLocation;

    public SentPic(){
        viewed = false;
        fromUser = "";
        toUser = "";
        imageLocation = "";
    }

    public boolean isViewed(){
        return viewed;
    }

    public void setViewed(boolean wasViewed){
        viewed = wasViewed;

    }
    public String getToUser(){
        return toUser;
    }
    public void setToUser(String user){
        toUser = user;
    }
    public String getFromUser(){
        return fromUser;
    }
    public void setFromUser(String user){
        fromUser = user;
    }
    public String getImageLocation(){
        return imageLocation;
    }
    public void setImageLocation(String location){
        imageLocation = location;
    }

}
