package com.contacts.contactapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class user {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String role = "ROLE_USER";

    @Column(name = "is_admin")
    private boolean isAdmin;

    // No-arg constructor
    public user() {}

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { this.password = password; }

    public String getRole() {
        return role;
    }

    public void setRole(String role) { this.role = role; }
}