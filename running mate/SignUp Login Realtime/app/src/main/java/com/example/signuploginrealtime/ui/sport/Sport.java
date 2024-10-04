package com.example.signuploginrealtime.ui.sport;

public class Sport {
    String topic;
    String sport;
    String date;
    String time;
    String location;
    String people;
    String note;

    String ownerID;

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    String ownerUserName;

    public Sport() {

    }

    public Sport(String topic, String sport, String date, String time, String location, String people, String note) {
        this.topic = topic;
        this.sport = sport;
        this.date = date;
        this.time = time;
        this.location = location;
        this.people = people;
        this.note = note;
    }

    public void setOwner(String ownerID, String ownerUserName) {
        this.ownerID = ownerID;
        this.ownerUserName = ownerUserName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
