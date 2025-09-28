package com.contacts.contactapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String title;

    private String email;

    @Column(name = "email_label")
    private String emailLabel;


    private String phone;

    @Column(name = "phone_label")
    private String phoneLabel;

    private String address;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEmailLabel() {
        return emailLabel;
    }

    public void setEmailLabel(String emailLabel) {
        this.emailLabel = emailLabel;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPhoneLabel() {
        return phoneLabel;
    }

    public void setPhoneLabel(String phoneLabel) {
        this.phoneLabel = phoneLabel;
    }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
