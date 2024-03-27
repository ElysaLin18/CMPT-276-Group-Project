package com.github.elysalin18.cmpt276groupproject.donatedesk.models;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InboxMeta {
    private String id;
    private String fromAddress;
    private String createdAt;
    private boolean isDeleted;
    
    public InboxMeta() {

    }
    public InboxMeta(String id, String fromAddress, String createdAt, boolean isDeleted) {
        this.id = id;
        this.isDeleted = isDeleted;
    }
    public String getId() {
        return id;
    }
    public String getFromAddress() {
        return fromAddress;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public boolean getIsDeleted() {
        return isDeleted;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setFromAddres(String fromAddress) {
        this.fromAddress = fromAddress;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public boolean dateInequality(String startDate, String endDate) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        try {
            Date start = (Date)formatter.parse(startDate);
            Date end = (Date)formatter.parse(endDate);
            Date date = (Date)formatter.parse(createdAt);

            return start.before(date) && end.after(date);
        }
        catch (ParseException exception) {
            return false;
        }
    }

    @Override 
    public String toString() {
        return id;
    }

    @JsonProperty("from")
    private void unpackNested(Map<String, Object> from) {
        this.fromAddress = (String)from.get("address");
    }
}
