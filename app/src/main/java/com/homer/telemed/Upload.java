package com.homer.telemed;

import com.google.firebase.database.Exclude;

public class Upload {
    private String name, imageUrl, key;
    private int userID;

    public Upload(){
        //empty constructor
    }

    public Upload(String name, String imageUrl, int userID){

        if(name.trim().equals("")){
            this.name = "No Name";
        } else{
            this.name = name;
        }

        this.imageUrl = imageUrl;
        this.userID = userID;

    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public int getUserID(){
        return this.userID;
    }

    public void setUserID(int userID){
        this.userID = userID;
    }

    @Exclude
    public String getKey(){
        return key;
    }

    @Exclude
    public void setKey(String key){
        this.key = key;
    }

}
