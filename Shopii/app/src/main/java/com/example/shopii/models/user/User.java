package com.example.shopii.models.user;

import java.util.UUID;
import java.util.Date;

public class User {
    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private Address userAddress;
    private Date createdAt;
    private boolean isActive;

    // Constructors
    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = new Date();
        this.isActive = false;
    }

    public User(String username, String email, String passwordHash) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
