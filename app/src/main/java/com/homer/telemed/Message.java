package com.homer.telemed;

public class Message {
    private String text; // message body
    private int sentByPatient;

    public Message(String text, int sentByPatient) {
        this.text = text;
        this.sentByPatient = sentByPatient;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }


    public int getSentByPatient() {
        return sentByPatient;
    }

    public void setSentByPatient(int sentByPatient) {
        this.sentByPatient = sentByPatient;
    }
}