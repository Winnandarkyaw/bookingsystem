package com.example.bookingsys.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor  // Lombok will generate a default constructor
@AllArgsConstructor // Lombok will generate a constructor with all arguments
@EqualsAndHashCode  // Lombok generates equals() and hashCode() methods based on all fields
@ToString  // Lombok generates a toString() method for this class
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private boolean emailVerified;
    private int credits;

    // Additional fields like createdAt, updatedAt for record keeping
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Lombok will automatically generate getters and setters for all fields
    // No need for explicit constructors or getter/setter methods

    // PreUpdate method for updating the `updatedAt` field
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Custom Constructor to set default values for certain fields
    public User(String username, String password, String email, int credits) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.emailVerified = false; // New users should verify email
        this.credits = credits;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
