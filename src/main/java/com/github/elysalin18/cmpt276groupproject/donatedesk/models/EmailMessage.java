package com.github.elysalin18.cmpt276groupproject.donatedesk.models;

public class EmailMessage {
    private String text;
    private String createdAt;

    public EmailMessage() {

    }
    public EmailMessage(String text, String createdAt) {
        this.text = text;
        this.createdAt = createdAt;
    }
    public String getText() {
        return text;
    }
    public String getCreatedAt() {
        return createdAt;
    } 
    public void setText(String text) {
        this.text = text;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}