package com.homer.telemed;

public class Therapist {
    private String imageUrl;
    private String name, field, location, specialties, clinic;

    public Therapist(){
        //Empty constructor
    }

    public Therapist(String imageUrl, String name, String field, String location, String specialties, String clinic){
        this.imageUrl = imageUrl;
        this.name = name;
        this.field = field;
        this.location = location;
        this.specialties = specialties;
        this.clinic = clinic;
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

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }
}
