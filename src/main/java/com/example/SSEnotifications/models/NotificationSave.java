/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.SSEnotifications.models;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;


@Entity
public class NotificationSave implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true, updatable = false)
    private int id;
    
    @NotNull
    private int hoursToDelete;
    
    private Timestamp date;
    
    @Column(nullable = false, unique = true, name = "user_id")
    private String email;
    
    private String message;
    
    private String schoolName;

    public NotificationSave() {
    
    }
 
    public NotificationSave(PushNotification notification, int hoursToDelete) {
        this.date = notification.getDate();
        this.email = notification.getEmail();
        this.message = notification.getMessage();
        this.schoolName = notification.getSchoolName().orElse("");
        this.hoursToDelete = hoursToDelete;
    }
    
    public NotificationSave(Timestamp date, String email, String message, String schoolName, int hoursToDelete) {
        this.date = date;
        this.email = email;
        this.message = message;
        this.schoolName = schoolName;
        this.hoursToDelete = hoursToDelete;
    }

    @Override
    public String toString() {
        return "PushNotification{" + "date=" + date + ", email=" + email + ", message=" + message + ", schoolName=" + schoolName + '}';
    }

    public int getId() {
        return id;
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

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public int getHoursToDelete() {
        return hoursToDelete;
    }

    public void setHoursToDelete(int hoursToDelete) {
        this.hoursToDelete = hoursToDelete;
    }
    
}
