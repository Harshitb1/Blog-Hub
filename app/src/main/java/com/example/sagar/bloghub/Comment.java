package com.example.sagar.bloghub;

import java.util.Date;

public class Comment {

    String message, userId;
    Date timestamp;

    public Comment() { }

    public Comment(String message, String userId, Date timestamp) {
        this.message = message;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
