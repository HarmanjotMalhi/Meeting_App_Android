package com.example.scheduler.data;

public class Meeting {
    private String id;
    private String contact;
    private String title;
    private String date;

    // Constructor
    public Meeting(String id, String contact, String title, String date) {
        this.id = id;
        this.contact = contact;
        this.title = title;
        this.date = date;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setTitle(String contact) {
        this.contact = contact;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString(){
        return id + " " + title + " " +  contact + " " + date;
    }
}

