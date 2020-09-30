package com.homer.telemed;

public class Therapist {
    private String imageUrl;
    private String name;

    public Therapist(){
        //Empty constructor
    }

    public Therapist(String imageUrl, String name){
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}
