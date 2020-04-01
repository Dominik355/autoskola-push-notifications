
package com.example.SSEnotifications.models;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Entity
public class EmitterRegistration implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true, updatable = false)
    private int id;
    
    @Column(nullable = false, unique = true, name = "user_id")
    private String userEmail;

    @NotEmpty
    @Column(unique = true, name = "generated_id")
    private String generatedId;
    
    @Column(name = "is_active")
    @NotNull
    private boolean isActive;
    
    private Timestamp lastOnline;

    public EmitterRegistration() {
        this.lastOnline = new Timestamp(System.currentTimeMillis());
        this.isActive = false;
    }

    public EmitterRegistration(String userEmail, String generatedID) {
        this.userEmail = userEmail;
        this.generatedId = generatedID;
        this.lastOnline = new Timestamp(System.currentTimeMillis());
        this.isActive = false;
    }

    public int getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getGeneratedID() {
        return generatedId;
    }

    public void setGeneratedID(String generatedID) {
        this.generatedId = generatedID;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Timestamp getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Timestamp lastOnline) {
        this.lastOnline = lastOnline;
    }
    
}
