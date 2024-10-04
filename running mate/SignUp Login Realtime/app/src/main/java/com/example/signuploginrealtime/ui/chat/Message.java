package com.example.signuploginrealtime.ui.chat;

public class Message {
    private String text;
    private String username;
    private long timestamp;
    //private boolean isRead;
    private String receiver;
    public Message() {}

    public Message(String text, String username,String receiver) {
        this.text = text;
        this.username = username;
        this.timestamp = System.currentTimeMillis();
        //this.isRead = false;
        this.receiver = receiver;
    }

    public String getUsername(){
        return username;
    }

    public String getText(){
        return text;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public String getReceiver() {
        return receiver;
    }
}



