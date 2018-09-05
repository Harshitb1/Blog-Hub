package com.example.sagar.bloghub;

import java.util.Date;

public class Comment {

    String message, user_id;
    Date timestamp;

    public Comment() { }

    public Comment(String message, String userId, Date timestamp) {
        this.message = message;
        this.user_id = userId;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
