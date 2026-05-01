package com.campusloop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String fullName;
    private String hostelBlock;
    private String department;
    
    private String role; // ROLE_USER, ROLE_ADMIN
    
    private boolean verified = false;
    private double reputationScore = 5.0;

    @ManyToMany
    @JoinTable(
        name = "user_saved_products",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private java.util.Set<Product> savedProducts = new java.util.HashSet<>();

    @PrePersist
    public void validateEmail() {
        if (email != null && !email.contains(".edu") && !email.contains("institution.in")) {
            // In a real app, we'd throw an exception here or handle it in the service
        }
    }
}
