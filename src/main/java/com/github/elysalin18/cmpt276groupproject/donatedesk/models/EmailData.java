package com.github.elysalin18.cmpt276groupproject.donatedesk.models;

public class EmailData {
    private String name;
    private String date;
    private String message;
    private String address;
    private String text;

    public EmailData() {

    }
    public EmailData(String name, String date, String message, String address, String text) {
        this.name = name;
        this.date = date;
        this.message = message;
        this.address = address;
        this.text = text;
    }
    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getMessage() {
        return message;
    }
    public String getAddress() {
        return address;
    }
    public String getText() {
        return text;
    }
}
