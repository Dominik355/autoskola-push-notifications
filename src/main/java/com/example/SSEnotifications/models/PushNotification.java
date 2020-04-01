
package com.example.SSEnotifications.models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;


public class PushNotification implements Serializable {
    
    private Timestamp date;
    
    private String email;
    
    private String message;
    
    private String schoolName;

    public PushNotification() {
    
    }
    
    public PushNotification(String message) {
        this.date = new Timestamp(System.currentTimeMillis());
        this.message = message;
    }

    public PushNotification(NotificationSave save) {
        this.date = save.getDate();
        this.email = save.getEmail();
        this.message = save.getMessage();
        this.schoolName = save.getSchoolName();
    }
    
    public PushNotification(Timestamp date, String email, String message, String schoolName) {
        this.date = date;
        this.email = email;
        this.message = message;
        this.schoolName = schoolName;
    }

    @Override
    public String toString() {
        return "PushNotification{" + "date=" + date + ", email=" + email + ", message=" + message + ", schoolName=" + schoolName + '}';
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Optional<String> getSchoolName() {
        return Optional.ofNullable(schoolName);
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
    
}
